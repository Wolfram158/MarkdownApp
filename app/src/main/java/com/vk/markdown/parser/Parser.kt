package com.vk.markdown.parser

import java.util.Collections.addAll

fun parse(str: String): List<Node> {
    var rest = str
    val result = mutableListOf<Node>()
    val text = StringBuilder()
    var isNewLine = true

    while (rest.isNotEmpty()) {
        if (isNewLine && rest.length >= 2 && isPrefixConstant(
                rest,
                '#',
                1
            ) && rest[1] == ' '
        ) {
            freeText(text, result)
            parseHeader(rest, 1, isNewLine, result).let {
                rest = rest.substring(it.end)
                isNewLine = it.isNewLine
            }
        } else if (isNewLine && rest.length >= 3 && isPrefixConstant(
                rest,
                '#',
                2
            ) && rest[2] == ' '
        ) {
            freeText(text, result)
            parseHeader(rest, 2, isNewLine, result).let {
                rest = rest.substring(it.end)
                isNewLine = it.isNewLine
            }
        } else if (isNewLine && rest.length >= 4 && isPrefixConstant(
                rest,
                '#',
                3
            ) && rest[3] == ' '
        ) {
            freeText(text, result)
            parseHeader(rest, 3, isNewLine, result).let {
                rest = rest.substring(it.end)
                isNewLine = it.isNewLine
            }
        } else if (isNewLine && rest.length >= 5 && isPrefixConstant(
                rest,
                '#',
                4
            ) && rest[4] == ' '
        ) {
            freeText(text, result)
            parseHeader(rest, 4, isNewLine, result).let {
                rest = rest.substring(it.end)
                isNewLine = it.isNewLine
            }
        } else if (isNewLine && rest.length >= 6 && isPrefixConstant(
                rest,
                '#',
                5
            ) && rest[5] == ' '
        ) {
            freeText(text, result)
            parseHeader(rest, 5, isNewLine, result).let {
                rest = rest.substring(it.end)
                isNewLine = it.isNewLine
            }
        } else if (isNewLine && rest.length >= 7 && isPrefixConstant(
                rest,
                '#',
                6
            ) && rest[6] == ' '
        ) {
            freeText(text, result)
            parseHeader(rest, 6, isNewLine, result).let {
                rest = rest.substring(it.end)
                isNewLine = it.isNewLine
            }
        } else if (rest.length >= 3 && isPrefixConstant(rest, '*', 2)
            && !rest[2].isWhitespace() && trySimpleNode(rest, 3, "**")
        ) {
            freeText(text, result)
            parseStyle<Bold>(rest, "**", isNewLine, result, ::Bold).let {
                rest = rest.substring(it.end)
                isNewLine = it.isNewLine
            }
        } else if (rest.length >= 3 && isPrefixConstant(rest, '~', 2)
            && !rest[2].isWhitespace() && trySimpleNode(rest, 3, "~~")
        ) {
            freeText(text, result)
            parseStyle<Strike>(rest, "~~", isNewLine, result, ::Strike).let {
                rest = rest.substring(it.end)
                isNewLine = it.isNewLine
            }
        } else if (rest.length >= 2 && isPrefixConstant(rest, '*', 1)
            && !rest[1].isWhitespace() && trySimpleNode(rest, 2, "*")
        ) {
            freeText(text, result)
            parseStyle<Cursive>(rest, "*", isNewLine, result, ::Cursive).let {
                rest = rest.substring(it.end)
                isNewLine = it.isNewLine
            }
        } else if (rest.length >= 5 && rest[0] == '|' && tryTable(rest) && isNewLine) {

        } else {
            text.append(rest[0])
            isNewLine = isNewLine(rest, 0, isNewLine)
            rest = rest.substring(1)
        }
    }
    freeText(text, result)
    return result
}

private fun tryTable(str: String): Boolean {
    return false
}

private fun freeText(text: StringBuilder, result: MutableList<Node>) {
    if (text.isNotEmpty()) {
        result.add(Text(text.toString()))
        text.setLength(0)
    }
}

private fun <T : Node> parseStyle(
    rest: String,
    stylePattern: String,
    isNewLine: Boolean,
    result: MutableList<Node>,
    nodeGetter: () -> T
): AnalysisInfo {
    var j = stylePattern.length
    val content = StringBuilder()
    var isNewLine = isNewLine
    for (i in j..<rest.length) {
        if (rest.startsWith(stylePattern, i)) {
            j = i
            break
        } else {
            isNewLine = isNewLine(rest, i, isNewLine)
            content.append(rest[i])
        }
    }
    if (stylePattern == "**" && j + 2 < rest.length && rest[j + 2] == '*') {
        content.append(rest[j])
        j += 1
    }
    result.add(nodeGetter().apply {
        this.content = parse(content.toString())
    })
    return AnalysisInfo(j + stylePattern.length, isNewLine)
}

private fun trySimpleNode(str: String, start: Int, pattern: String): Boolean {
    for (i in start..<str.length) {
        if (str[i] == '\n') {
            return false
        }
        if (str.startsWith(pattern, i)) {
            return true
        }
    }
    return false
}

private fun isPrefixConstant(str: String, template: Char, count: Int): Boolean {
    for (i in 0..<minOf(count, str.length)) {
        if (str[i] != template) {
            return false
        }
    }
    return true
}

private data class AnalysisInfo(val end: Int, val isNewLine: Boolean)

private fun isNewLine(rest: String, j: Int, isNewLine: Boolean): Boolean {
    if (!rest[j].isWhitespace()) {
        return false
    } else if (rest[j] == '\n') {
        return true
    }
    return isNewLine
}

private fun parseHeader(
    rest: String,
    level: Int,
    isNewLine: Boolean,
    result: MutableList<Node>
): AnalysisInfo {
    var j = level + 1
    val header = StringBuilder()
    var isNewLine = isNewLine
    val set = setOf<String>().apply {
        addAll(mutableListOf<String>().apply {
            IntRange(1, level).forEach { add("#".repeat(it)) }
        })
    }
    while (j < rest.length && rest[j] != '\n') {
        if (!rest[j].isWhitespace()) {
            isNewLine = false
        }
        header.append(rest[j])
        j++
    }
    val content = StringBuilder()
    while ((j + level < rest.length && rest.substring(
            j,
            j + level
        ) !in set && rest[j + level] != ' ' && !isNewLine)
        || j < rest.length
    ) {
        isNewLine = isNewLine(rest, j, isNewLine)
        content.append(rest[j])
        j++
    }
    result.add(Header(level, parse(header.toString())).apply {
        this.content = parse(content.toString())
    })
    return AnalysisInfo(j, isNewLine)
}

fun main() {
}