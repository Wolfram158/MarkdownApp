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
import android.widget.TextView
import com.vk.markdown.domain.usecase.DownloadImageUseCase
import com.vk.markdown.parser.Bold
import com.vk.markdown.parser.Cursive
import com.vk.markdown.parser.Header
import com.vk.markdown.parser.Img
import com.vk.markdown.parser.Node
import com.vk.markdown.parser.Strike
import com.vk.markdown.parser.Table
import com.vk.markdown.parser.Text
import com.vk.markdown.parser.parse
import kotlin.concurrent.thread

class Builder() {
    private var downloadImageUseCase: DownloadImageUseCase? = null
    private var context: Context? = null
    private val handler = Handler(getMainLooper())

    fun setDownloadImageUseCase(downloadImageUseCase: DownloadImageUseCase) {
        this.downloadImageUseCase = downloadImageUseCase
    }

    fun setContext(context: Context) {
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

    private fun buildFromNode(
        node: Node,
        isBold: Boolean,
        isCursive: Boolean,
        isStrike: Boolean,
        textSize: Int = 28
    ): View {
        return when (node) {
            is Bold -> {
                val ssb = SpannableStringBuilder()
                buildFromNodes(
                    node.content,
                    isBold = true,
                    isStrike = isStrike,
                    isCursive = isCursive,
                    textSize = textSize
                ).forEach { view ->
                    if (view is TextView) {
                        ssb.append(view.text as Spanned)
                    }
                }
                TextView(context).apply {
                    text = SpannableString.valueOf(ssb)
                    layoutParams =
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                }
            }

            is Cursive -> {
                val ssb = SpannableStringBuilder()
                buildFromNodes(
                    node.content,
                    isBold = isBold,
                    isStrike = isStrike,
                    isCursive = true,
                    textSize = textSize
                ).forEach { view ->
                    if (view is TextView) {
                        ssb.append(view.text as Spanned)
                    }
                }
                TextView(context).apply {
                    text = SpannableString.valueOf(ssb)
                    layoutParams =
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                }
            }

            is Header -> {
                val linearLayout = LinearLayout(context)
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
                linearLayout
            }

            is Img -> {
                val linearLayout = LinearLayout(context)
                linearLayout.orientation = LinearLayout.VERTICAL
                linearLayout.layoutParams =
                    LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                buildFromNodes(
                    node.description,
                    isBold = isBold,
                    isStrike = isStrike,
                    isCursive = isCursive
                ).forEach { view ->
                    linearLayout.addView(view)
                }
                val imageView = ImageView(context).apply {
                    layoutParams =
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                }
                linearLayout.addView(imageView)
                thread {
                    try {
                        val bytes = downloadImageUseCase?.invoke(node.link)
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
                linearLayout
            }

            is Strike -> {
                val ssb = SpannableStringBuilder()
                buildFromNodes(
                    node.content,
                    isBold = isBold,
                    isStrike = true,
                    isCursive = isCursive,
                    textSize = textSize
                ).forEach { view ->
                    if (view is TextView) {
                        ssb.append(view.text as Spanned)
                    }
                }
                TextView(context).apply {
                    text = SpannableString.valueOf(ssb)
                    layoutParams =
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                }
            }

            is Text -> {
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
                TextView(context).apply {
                    text = span
                    layoutParams =
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                }
            }

            is Table -> TODO()
        }
    }
}