package com.vk.analysis.parser

import java.util.Collections.addAll

fun parse(str: String): List<Node> {
    var rest = str
    val result = mutableListOf<Node>()
    val text = StringBuilder()
    val description: Wrapper<String?> = Wrapper(null)
    val link: Wrapper<String?> = Wrapper(null)
    val position: Wrapper<Int?> = Wrapper(null)
    var isNewLine = true

//    if (rest == "") {
//        result.add(Text(""))
//    }

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
        } else if (tryTable(rest, isNewLine)) {
            rest = parseTable(rest, result)
            isNewLine = true
        } else if (tryImage(rest, description, link, position)) {
            freeText(text, result)
            position.value?.let { pos ->
                rest = rest.substring(pos)
            }
            val link = link.value
            val description = description.value
            if (link != null && description != null) {
                result.add(Img(description = parse(description), link = link))
            }
        } else {
            text.append(rest[0])
            isNewLine = isNewLine(rest, 0, isNewLine)
            rest = rest.substring(1)
        }
    }
    freeText(text, result)
    return result
}

private data class Wrapper<T>(private var _value: T) {
    var value: T
        get() = _value
        set(value) {
            _value = value
        }
}

private val prefixTableRegex = "(\\|([^\n|]*))+\\|([ \t\r])*\n(\\|-+)+\\|([ \t\r])*\n.*"
    .toRegex(RegexOption.DOT_MATCHES_ALL)
private val rowTableRegex = "((\\|([^\n|]*))+\\|([ \t\r])*\n.*)|((\\|([^\n|]*))+\\|([ \t\r])*\\s*)"
    .toRegex(RegexOption.DOT_MATCHES_ALL)
private val separatorTableRegex = "(\\|-+)+\\|([ \t\r])*\n.*"
    .toRegex(RegexOption.DOT_MATCHES_ALL)

private fun tryTable(str: String, isNewLine: Boolean): Boolean {
    return str.matches(prefixTableRegex) && isNewLine
}

private fun parseTable(str: String, result: MutableList<Node>): String {
    var rest = str
    var j = 0
    val table = mutableListOf<List<List<Node>>>()
    val rowCells = mutableListOf<List<Node>>()
    val currentContent = StringBuilder()
    var linesCount = 0
    while (j < rest.length) {
        if (rest[j] == '|') {
            if (linesCount > 0) {
                rowCells.add(parse(currentContent.toString()))
                currentContent.setLength(0)
            }
            linesCount++
            j++
        } else if (rest[j] == '\n') {
            linesCount = 0
            table.add(rowCells.toList())
            rowCells.clear()
            currentContent.setLength(0)
            j++
            rest = if (j < rest.length) {
                rest.substring(j)
            } else {
                ""
            }
            j = 0
            if (rest.matches(separatorTableRegex)) {
                while (j < rest.length && rest[j] != '\n') {
                    j++
                }
                j++
            } else if (!rest.matches(rowTableRegex)) {
                break
            }
        } else {
            currentContent.append(rest[j])
            j++
        }
    }
    rest = if (j < rest.length) {
        rest.substring(j)
    } else {
        ""
    }
    if (rowCells.isNotEmpty()) {
        table.add(rowCells.toList())
    }
    result.add(Table(table))
    return rest
}

private fun tryImage(
    str: String,
    description: Wrapper<String?>,
    link: Wrapper<String?>,
    position: Wrapper<Int?>
): Boolean {
    if (str.length >= 5) {
        if (str[0] != '!' && str[1] != '[') {
            return false
        }
        val descriptionSb = StringBuilder()
        var j = 2
        while (j < str.length && str[j] != ']') {
            if (str[j] == '\n') {
                return false
            }
            descriptionSb.append(str[j])
            j++
        }
        j++
        if (j >= str.length || str[j] != '(') {
            return false
        }
        j++
        val linkSb = StringBuilder()
        while (j < str.length && str[j] != ')') {
            if (str[j] == '\n') {
                return false
            }
            linkSb.append(str[j])
            j++
        }
        if (j == str.length) {
            return false
        }
        description.value = descriptionSb.toString()
        link.value = linkSb.toString()
        position.value = ++j
        return true
    }
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