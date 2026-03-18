package org.example

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.example.model.Cursor
import org.example.model.TerminalBuffer
import org.example.model.TerminalLine

class TerminalLineTest {
    private lateinit var line: TerminalLine
    private val maxColumns = 20
    private val dummyText = "Hello, World!"
    private val N = 5

    @BeforeEach
    fun setUp() {
        line = TerminalLine(
            maxWidth = maxColumns,
            foreground = 0,
            background = 0,
            styles = emptySet()
        )
    }

    @Test
    fun replaceText_writesTextFromBeginning() {
        line.replaceText(dummyText, 0)
        assertEquals(
            dummyText,
            line.cells.map { it.char }.joinToString("").take(dummyText.length)
        )
    }

    @Test
    fun replaceText_writesTextFromMiddle() {
        line.replaceText(dummyText, N)
        assertEquals(
            "${" ".repeat(N)}$dummyText",
            line.cells.map { it.char }
                .joinToString("").take(N + dummyText.length)
        )
    }

    @Test
    fun replaceText_doesNotWriteBeyondMaxWidth() {
        val longText = "This text is definitely longer than the max width of the line."
        line.replaceText(longText, 0)
        assertEquals(
            longText.take(maxColumns),
            line.cells.map { it.char }.joinToString("")
        )
    }

    @Test
    fun fill_fillsLineWithCharacter() {
        val fillChar = '*'
        line.fill(fillChar)
        assertEquals(
            fillChar.toString().repeat(maxColumns),
            line.cells.map { it.char }.joinToString("")
        )
    }

    @Test
    fun fill_overridesExistingText() {
        line.replaceText(dummyText, 0)
        val fillChar = '#'
        line.fill(fillChar)
        assertEquals(
            fillChar.toString().repeat(maxColumns),
            line.cells.map { it.char }.joinToString("")
        )
    }
}