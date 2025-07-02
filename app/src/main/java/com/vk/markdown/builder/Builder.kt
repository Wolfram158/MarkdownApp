package com.vk.markdown.builder

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import com.vk.markdown.parser.Bold
import com.vk.markdown.parser.Cursive
import com.vk.markdown.parser.Header
import com.vk.markdown.parser.Img
import com.vk.markdown.parser.Node
import com.vk.markdown.parser.Strike
import com.vk.markdown.parser.Table
import com.vk.markdown.parser.Text
import com.vk.markdown.parser.parse

private val levelToTextSize =
    mapOf(1 to 24, 2 to 22, 3 to 20, 4 to 18, 5 to 16, 6 to 14).mapValues { it.value * 2 }

fun buildFromString(str: String): SpannableString {
    val ssb = SpannableStringBuilder()
    for (node in parse(str)) {
        ssb.append(buildFromNode(node, false, false, false))
    }
    return SpannableString.valueOf(ssb)
}

fun buildFromNodes(
    nodes: List<Node>,
    isBold: Boolean,
    isCursive: Boolean,
    isStrike: Boolean,
    textSize: Int = 28
): SpannableString {
    val ssb = SpannableStringBuilder()
    nodes.forEach { node ->
        ssb.append(
            buildFromNode(
                node,
                isBold = isBold,
                isCursive = isCursive,
                isStrike = isStrike,
                textSize = textSize
            )
        )
    }
    return SpannableString.valueOf(ssb)
}

fun buildFromNode(
    node: Node,
    isBold: Boolean,
    isCursive: Boolean,
    isStrike: Boolean,
    textSize: Int = 28
): SpannableString {
    return when (node) {
        is Bold -> {
            buildFromNodes(
                node.content,
                isBold = true,
                isStrike = isStrike,
                isCursive = isCursive,
                textSize = textSize
            )
        }

        is Cursive -> {
            buildFromNodes(
                node.content,
                isBold = isBold,
                isStrike = isStrike,
                isCursive = true,
                textSize = textSize
            )
        }

        is Header -> {
            val ssb = SpannableStringBuilder()
            val builtHeader = buildFromNodes(
                node.header,
                isBold = true,
                isStrike = isStrike,
                isCursive = isCursive,
                textSize = levelToTextSize[node.level]!!
            )
            val builtContent = buildFromNodes(
                node.content,
                isBold = isBold,
                isStrike = isStrike,
                isCursive = isCursive,
                textSize = textSize
            )
            ssb.append(builtHeader).append(builtContent)
            SpannableString.valueOf(ssb)
        }

        is Img -> TODO()
        is Strike -> {
            buildFromNodes(
                node.content,
                isBold = isBold,
                isStrike = true,
                isCursive = isCursive,
                textSize = textSize
            )
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
            return span
        }

        is Table -> TODO()
    }
}