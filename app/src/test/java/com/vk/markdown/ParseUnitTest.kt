package com.vk.markdown

import com.vk.analysis.parser.Bold
import com.vk.analysis.parser.Cursive
import com.vk.analysis.parser.Header
import com.vk.analysis.parser.Img
import com.vk.analysis.parser.Strike
import com.vk.analysis.parser.Table
import com.vk.analysis.parser.Text
import com.vk.analysis.parser.parse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ParseUnitTest {
    @Test
    fun testBold() {
        assertEquals(
            listOf(Bold().apply {
                content = listOf(Text("hello"))
            }),
            parse("**hello**")
        )
    }

    @Test
    fun testBoldStrike() {
        assertEquals(
            listOf(Bold().apply {
                content = listOf(Strike().apply {
                    content = listOf(Text("hello"))
                })
            }),
            parse("**~~hello~~**")
        )
    }

    @Test
    fun testBoldCursive() {
        assertEquals(
            listOf(Bold().apply {
                content = listOf(Cursive().apply {
                    content = listOf(Text("hello"))
                })
            }),
            parse("***hello***")
        )
    }

    @Test
    fun testHeaderBold() {
        val parsed = parse("# **hello** \nWorld")
        assertTrue(parsed.isNotEmpty())
        assertTrue(parsed[0] is Header)
        assertEquals(
            (parsed[0] as Header).header,
            listOf(
                Bold().apply {
                    content = listOf(Text("hello"))
                },
                Text(" ")
            )
        )
        assertEquals(
            parsed[0].content,
            listOf(Text("\nWorld"))
        )
    }

    @Test
    fun testNestedHeaders() {
        assertEquals(
            listOf(
                Header(1, listOf(Text("A")))
                    .apply {
                        content = listOf(
                            Text("\n"),
                            Header(2, listOf(Text("B")))
                                .apply {
                                    content = listOf(
                                        Text("\n"),
                                        Header(3, listOf(Text("C")))
                                    )
                                })
                    }),
            parse("# A\n## B\n### C")
        )
    }

    @Test
    fun testImage() {
        assertEquals(
            listOf(
                Img(
                    link = "https://vk.com",
                    description = listOf(
                        Text("VKCOM")
                    )
                )
            ),
            parse("![VKCOM](https://vk.com)")
        )
    }

    @Test
    fun testImageBoldDescription() {
        assertEquals(
            listOf(
                Img(
                    link = "https://vk.com",
                    description = listOf(
                        Bold().apply {
                            content = listOf(Text("VKCOM"))
                        }
                    )
                )
            ),
            parse("![**VKCOM**](https://vk.com)")
        )
    }

    @Test
    fun testTable() {
        assertEquals(
            listOf(
                Table(
                    rows = listOf(
                        listOf(
                            listOf(Text("a")),
                            listOf(Text("b"))
                        ),
                        listOf(
                            listOf(Text("c")),
                            listOf(Text("d"))
                        )
                    )
                )
            ),
            parse("|a|b|\n|-|\n|c|d|")
        )
    }
}
