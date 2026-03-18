package org.example

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.example.model.Cursor
import org.example.model.TerminalBuffer
import org.example.model.TerminalLine

class TerminalBufferTest {
    private lateinit var buffer: TerminalBuffer
    private val N = 5
    private val maxColumns = 20
    private val maxRows = 5
    private val dummyText = "Hello, World!"

    @BeforeEach
    fun setUp() {
        buffer = TerminalBuffer(width = maxColumns, height = maxRows, scrollbackMaxSize = 100)
        buffer.cursor.setPosition(0, 0)
    }

    fun assertPosition(expectedColumn: Int, expectedRow: Int) {
        val position = buffer.cursor.getPosition()
        print("Expected position: ($expectedColumn, $expectedRow), Actual position: (${position.first}, ${position.second})")
        assertEquals(expectedColumn, position.first)
        assertEquals(expectedRow, position.second)
    }

    @Test
    fun writeAppendsTextOnEmptyLine() {
        buffer.write(dummyText)
        assertEquals(dummyText, buffer.screen[0].cells.map { it.char }
            .joinToString("").take(dummyText.length))
        assertPosition(dummyText.length, 0)
    }

    @Test
    fun writeOverridesExistingText() {
        val line1 = "First line"
        val line2 = "Second line"

        buffer.write(line1)
        buffer.cursor.setPosition(N, 0)
        buffer.write(line2)
        assertEquals(1, buffer.screen.size)
        assertEquals(
            "${line1.take(N)}${line2}",
            buffer.screen[0].cells.map { it.char }.joinToString("")
            .take(N + line2.length)
        )
        assertPosition(N + line2.length, 0)
    }

    @Test
    fun insertWritesOneLine() {
        buffer.insert(dummyText)
        assertEquals(dummyText, buffer.screen[0].cells.map { it.char }
            .joinToString("").take(dummyText.length))
        assertPosition(dummyText.length, 0)
    }

    @Test
    fun insertWritesMultipleLines() {
        val multiLineText = "Line 1\nLine 2\nLine 3"
        buffer.insert(multiLineText)
        val expectedLines = multiLineText.split("\n")
        for (i in expectedLines.indices) {
            assertEquals(expectedLines[i], buffer.screen[i].cells.map { it.char }
                .joinToString("").take(expectedLines[i].length))
        }
        assertPosition(expectedLines.last().length, expectedLines.size - 1)
    }

    @Test
    fun insertDoesNotWriteBeyondMaxRows() {
        val multiLineText = (1..(maxRows + 2)).joinToString("\n") { "Line $it" }
        buffer.insert(multiLineText)
        for (i in 0 until maxRows) {
            assertEquals("Line ${i + 1}", buffer.screen[i].cells.map { it.char }
                .joinToString("").take("Line ${i + 1}".length))
        }
        assertPosition(6, maxRows - 1)
    }

    @Test
    fun fillLine_fillsCurrentLineWithCharacter() {
        val fillChar = '*'
        buffer.fillLine(fillChar)
        assertEquals(
            fillChar.toString().repeat(maxColumns),
            buffer.screen[0].cells.map { it.char }.joinToString("")
        )
        assertPosition(0, 0)
    }

    @Test
    fun fillLine_fillsCorrectRow() {
        val fillChar = '*'
        buffer.screen = List(maxRows) {
            TerminalLine(maxColumns, 0, 0, emptySet())
        }
        buffer.cursor.setPosition(0, 2)
        buffer.fillLine(fillChar)
        assertEquals(
            fillChar.toString().repeat(maxColumns),
            buffer.screen[2].cells.map { it.char }.joinToString("")
        )
        assertPosition(0, 2)
    }

    @Test
    fun addNewLine_addsNewLineToScreen() {
        buffer.addNewLine()
        assertEquals(2, buffer.screen.size)
        assertPosition(0, 0)
    }

    @Test
    fun addNewLine_doesNotExceedMaxRows() {
        for (i in 1..(maxRows + 2)) {
            buffer.addNewLine()
        }
        assertEquals(maxRows, buffer.screen.size)
        assertPosition(0, 0)
    }
}