package org.example

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.example.model.Cursor
import org.example.model.TerminalBuffer

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
}