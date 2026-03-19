package org.example

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.example.model.Cursor
import org.example.model.TerminalBuffer
import org.example.model.TerminalLine
import kotlin.assert
import org.example.model.CharacterCell

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

    fun assertCellEquals(expected: CharacterCell, actual: CharacterCell?) {
        assertEquals(expected.char, actual?.char)
        assertEquals(expected.fgColor, actual?.fgColor)
        assertEquals(expected.bgColor, actual?.bgColor)
        assertEquals(expected.styleFlags, actual?.styleFlags)
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
        val scrollback = buffer.getScrollback()
        val screen = buffer.screen

        assertEquals(2, scrollback.size)
        assertEquals(
            "Line 1",
            scrollback[0].cells.map { it.char }.joinToString("")
                .take("Line 1".length)
        )
        assertEquals(
            "Line 2",
            scrollback[1].cells.map { it.char }.joinToString("")
                .take("Line 2".length)
        )
        assertEquals(maxRows, screen.size)
        for (i in 2 until maxRows + 2) {
            val expectedLine = "Line ${i + 1}"
            val actualLine = screen[i - 2].cells.map { it.char }.joinToString("")
                .take(expectedLine.length)
            println("assertions result: $expectedLine, $actualLine")
            assertEquals(expectedLine, actualLine)
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
        buffer.screen = MutableList(maxRows) {
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

    @Test
    fun clearScreen_resetsScreenToEmptyLine() {
        buffer.write(dummyText)
        buffer.addNewLine()
        buffer.write(dummyText)
        buffer.clearScreen()
        assertEquals(1, buffer.screen.size)
        assertEquals(
            (" ").repeat(maxColumns),
            buffer.screen[0].cells.map { it.char }.joinToString("")
        )
        assertPosition(0, 0)
    }

    @Test
    fun clearScreenAndScrollback_resetsBothScreenAndScrollback() {
        buffer.write(dummyText)
        buffer.addNewLine()
        buffer.write(dummyText)
        buffer.clearScreenAndScrollback()
        val screen = buffer.screen
        val scrollback = buffer.getScrollback()

        assertEquals(1, screen.size)
        assertEquals(
            (" ").repeat(maxColumns),
            buffer.screen[0].cells.map { it.char }.joinToString("")
        )
        assertEquals(1, buffer.screen.size)
        assertEquals(0, scrollback.size)

        assertPosition(0, 0)
    }

    @Test
    fun getCharacterFromScreenAt_returnsCorrectCharacter() {
        buffer.write("ABCDE")
        val charCell = buffer.getCharacterFromScreenAt(2, 0)
        assertEquals('C', charCell)
    }

    @Test
    fun getCharacterFromScreenAt_outOfBoundsReturnsNull() {
        buffer.write("ABCDE")
        assertEquals(null, buffer.getCharacterFromScreenAt(-1, 0))
        assertEquals(' ', buffer.getCharacterFromScreenAt(5, 0))
        assertEquals(null, buffer.getCharacterFromScreenAt(0, -1))
        assertEquals(null, buffer.getCharacterFromScreenAt(0, 1))
    }

    @Test
    fun getCharacterFromScrollbackAt_returnsCorrectCharacter() {
        val multiLineText = (1..(maxRows + 2)).joinToString("\n") { "Line $it" }
        buffer.insert(multiLineText)
        val charCell = buffer.getCharacterFromScrollbackAt(2, 0)
        assertEquals('n', charCell)
    }

    @Test
    fun getCharacterFromScrollbackAt_outOfBoundsReturnsNull() {
        val multiLineText = (1..(maxRows + 2)).joinToString("\n") { "Line $it" }
        buffer.insert(multiLineText)
        assertEquals(null, buffer.getCharacterFromScrollbackAt(-1, 0))
        assertEquals(' ', buffer.getCharacterFromScrollbackAt(6, 0))
        assertEquals(null, buffer.getCharacterFromScrollbackAt(0, -1))
        assertEquals(null, buffer.getCharacterFromScrollbackAt(0, 2))
    }

    @Test
    fun getAttributesFromScreenAt_returnsCorrectCharacter() {
        buffer.write("ABCDE")
        val charCell = buffer.getAttributesFromScreenAt(2, 0)
        val expectedCell = CharacterCell('C', 0, 0, emptySet())
        assertCellEquals(expectedCell, charCell)
    }

    @Test
    fun getAttributesFromScreenAt_outOfBoundsReturnsNull() {
        buffer.write("ABCDE")
        val expectedCell = CharacterCell(' ', 0, 0, emptySet())
        assertEquals(null, buffer.getAttributesFromScreenAt(-1, 0))
        assertCellEquals(expectedCell, buffer.getAttributesFromScreenAt(5, 0))
        assertEquals(null, buffer.getAttributesFromScreenAt(0, -1))
        assertEquals(null, buffer.getAttributesFromScreenAt(0, 1))
    }
}