package com.vk.markdown.builder

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper.getMainLooper
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.vk.markdown.R
import com.vk.markdown.domain.usecase.DownloadImageUseCase
import com.vk.analysis.parser.Bold
import com.vk.analysis.parser.Cursive
import com.vk.analysis.parser.Header
import com.vk.analysis.parser.Img
import com.vk.analysis.parser.Node
import com.vk.analysis.parser.Strike
import com.vk.analysis.parser.Table
import com.vk.analysis.parser.Text
import com.vk.analysis.parser.parse
import kotlin.concurrent.thread

class MarkdownViewBuilder() {
    private var downloadImageUseCase: DownloadImageUseCase? = null
    private var context: Context? = null
    private val handler = Handler(getMainLooper())

    fun setDownloadImageUseCase(downloadImageUseCase: DownloadImageUseCase) {
        this.downloadImageUseCase = downloadImageUseCase
    }

    fun setContext(context: Context?) {
        this.context = context
    }

    private val levelToTextSize =
        mapOf(1 to 24, 2 to 22, 3 to 20, 4 to 18, 5 to 16, 6 to 14).mapValues { it.value * 2 }

    fun buildFromString(str: String, root: ViewGroup) {
        parse(str).forEach { node ->
            root.addView(buildFromNode(node, false, false, false))
        }
    }

    private fun buildFromNodes(
        nodes: List<Node>,
        isBold: Boolean,
        isCursive: Boolean,
        isStrike: Boolean,
        textSize: Int = 28
    ): List<View> {
        val built = mutableListOf<View>()
        var ssb = SpannableStringBuilder()
        nodes.forEachIndexed { i, node ->
            val view = buildFromNode(
                node,
                isBold = isBold,
                isCursive = isCursive,
                isStrike = isStrike,
                textSize = textSize
            )
            if (view is TextView) {
                ssb.append((view.text as? Spanned) ?: view.text)
            } else {
                if (ssb.isNotEmpty()) {
                    built.add(
                        TextView(context).apply {
                            text = SpannableString.valueOf(ssb)
                            layoutParams =
                                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                        }
                    )
                    ssb = SpannableStringBuilder()
                }
                built.add(view.apply {
                    layoutParams =
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                })
            }
            if (i == nodes.size - 1) {
                if (ssb.isNotEmpty()) {
                    built.add(
                        TextView(context).apply {
                            text = SpannableString.valueOf(ssb)
                            layoutParams =
                                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                        }
                    )
                    ssb = SpannableStringBuilder()
                }
            }
        }
        return built
    }

    private fun fillViewBySimpleNode(
        nodes: List<Node>,
        isBold: Boolean,
        isCursive: Boolean,
        isStrike: Boolean,
        textSize: Int,
        view: TextView
    ) {
        val ssb = SpannableStringBuilder()
        buildFromNodes(
            nodes,
            isBold = isBold,
            isStrike = isStrike,
            isCursive = isCursive,
            textSize = textSize
        ).forEach { view ->
            if (view is TextView) {
                ssb.append(view.text as Spanned)
            }
        }
        view.apply {
            text = SpannableString.valueOf(ssb)
            layoutParams =
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
    }

    private fun fillViewByHeader(
        node: Header,
        isBold: Boolean,
        isCursive: Boolean,
        isStrike: Boolean,
        textSize: Int,
        linearLayout: LinearLayout
    ) {
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        buildFromNodes(
            node.header,
            isBold = true,
            isStrike = isStrike,
            isCursive = isCursive,
            textSize = levelToTextSize[node.level]!!
        ).forEach { view ->
            linearLayout.addView(view)
        }
        buildFromNodes(
            node.content,
            isBold = isBold,
            isStrike = isStrike,
            isCursive = isCursive,
            textSize = textSize
        ).forEach { view ->
            linearLayout.addView(view)
        }
    }

    private fun fillViewByImage(
        node: Img,
        isBold: Boolean,
        isCursive: Boolean,
        isStrike: Boolean,
        textSize: Int,
        linearLayout: LinearLayout
    ) {
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.layoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        buildFromNodes(
            node.description,
            isBold = isBold,
            isStrike = isStrike,
            isCursive = isCursive,
            textSize = textSize
        ).forEach { view ->
            linearLayout.addView(view)
        }
        val imageView = ImageView(context).apply {
            layoutParams =
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
        linearLayout.addView(imageView)
        setImage(node.link, imageView = imageView, linearLayout = linearLayout)
    }

    private fun setImage(link: String, imageView: ImageView, linearLayout: LinearLayout) {
        thread {
            try {
                val bytes = downloadImageUseCase?.invoke(link)
                bytes?.let {
                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    handler.post {
                        imageView.setImageBitmap(bitmap)
                    }
                }
            } catch (_: Exception) {
                handler.post {
                    linearLayout.removeView(imageView)
                }
            }
        }
    }

    private fun fillViewByText(
        node: Text,
        isBold: Boolean,
        isCursive: Boolean,
        isStrike: Boolean,
        textSize: Int,
        view: TextView
    ) {
        val span = SpannableString(node.text)
        if (isStrike) {
            span.setSpan(
                StrikethroughSpan(),
                0,
                node.text.length,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
        if (isBold) {
            span.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                node.text.length,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
        if (isCursive) {
            span.setSpan(
                StyleSpan(Typeface.ITALIC),
                0,
                node.text.length,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
        span.setSpan(
            AbsoluteSizeSpan(textSize, true),
            0,
            node.text.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        view.apply {
            text = span
            layoutParams =
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
    }

    private fun fillViewByTable(
        node: Table,
        isBold: Boolean,
        isCursive: Boolean,
        isStrike: Boolean,
        textSize: Int,
        table: TableLayout
    ) {
        node.rows.forEach { cells ->
            val row = TableRow(context)
            cells.forEach { cell ->
                val layout = LinearLayout(context)
                buildFromNodes(
                    cell,
                    isBold = isBold,
                    isCursive = isCursive,
                    isStrike = isStrike,
                    textSize = textSize
                ).forEach { view ->
                    layout.addView(view.apply {
                        setBackgroundResource(R.drawable.cell_shape)
                    })
                }
                row.addView(layout)
            }
            table.addView(row)
        }
    }

    private fun buildFromNode(
        node: Node,
        isBold: Boolean,
        isCursive: Boolean,
        isStrike: Boolean,
        textSize: Int = 28
    ): View {
        return when (node) {
            is Bold -> {
                TextView(context).apply {
                    fillViewBySimpleNode(
                        node.content,
                        isBold = true,
                        isCursive = isCursive,
                        isStrike = isStrike,
                        textSize = textSize,
                        this
                    )
                }
            }

            is Cursive -> {
                TextView(context).apply {
                    fillViewBySimpleNode(
                        node.content,
                        isBold = isBold,
                        isCursive = true,
                        isStrike = isStrike,
                        textSize = textSize,
                        this
                    )
                }
            }

            is Header -> {
                LinearLayout(context).apply {
                    fillViewByHeader(
                        node,
                        isBold = isBold,
                        isCursive = isCursive,
                        isStrike = isStrike,
                        textSize = textSize,
                        this
                    )
                }
            }

            is Img -> {
                LinearLayout(context).apply {
                    fillViewByImage(
                        node,
                        isBold = isBold,
                        isCursive = isCursive,
                        isStrike = isStrike,
                        textSize = textSize,
                        this
                    )
                }
            }

            is Strike -> {
                TextView(context).apply {
                    fillViewBySimpleNode(
                        node.content,
                        isBold = isBold,
                        isCursive = isCursive,
                        isStrike = true,
                        textSize = textSize,
                        this
                    )
                }
            }

            is Text -> {
                TextView(context).apply {
                    fillViewByText(
                        node,
                        isBold = isBold,
                        isCursive = isCursive,
                        isStrike = isStrike,
                        textSize = textSize,
                        this
                    )
                }
            }

            is Table -> {
                TableLayout(context).apply {
                    fillViewByTable(
                        node,
                        isBold = isBold,
                        isCursive = isCursive,
                        isStrike = isStrike,
                        textSize = textSize,
                        this
                    )
                }
            }
        }
    }
}