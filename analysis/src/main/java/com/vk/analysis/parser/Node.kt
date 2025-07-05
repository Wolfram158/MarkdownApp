package com.vk.analysis.parser

sealed class Node {
    var content: List<Node> = listOf()

    override fun toString(): String {
        return "${this::class.simpleName}(${content.joinToString(", ") { it.toString() }})"
    }

    override fun equals(other: Any?): Boolean {
        return other != null && other is Node && content == other.content
    }

    override fun hashCode(): Int {
        throw NotImplementedError("Currently method hashCode is not implemented")
    }
}

data class Header(val level: Int, val header: List<Node>) : Node()

class Bold() : Node()

class Strike() : Node()

class Cursive() : Node()

data class Table(val rows: List<List<List<Node>>>) : Node()

data class Img(val description: List<Node>, val link: String) : Node()

data class Text(val text: String) : Node()