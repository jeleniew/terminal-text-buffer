package org.example

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.example.model.Cursor

class CursorTest {
    private lateinit var cursor: Cursor
    private val N = 5
    private val maxColumns = 80
    private val maxRows = 24

    @BeforeEach
    fun setUp() {
        cursor = Cursor(maxColumns = maxColumns, maxRows = maxRows)
        cursor.setPosition(40, 12)
    }

    fun assertPosition(expectedColumn: Int, expectedRow: Int) {
        val position = cursor.getPosition()
        assertEquals(expectedColumn, position.first)
        assertEquals(expectedRow, position.second)
    }

    @Test
    fun cursorHasInitialPosition() {
        assertPosition(40, 12)
    }

    @Test
    fun cursorCanMoveUp() {
        cursor.moveUp(N)
        assertPosition(40, 12 - N)
    }

    @Test
    fun cursorCanMoveDown() {
        cursor.moveDown(N)
        assertPosition(40, 12 + N)
    }

    @Test
    fun cursorCanMoveLeft() {
        cursor.moveLeft(N)
        assertPosition(40 - N, 12)
    }

    @Test
    fun cursorCanMoveRight() {
        cursor.moveRight(N)
        assertPosition(40 + N, 12)
    }

    @Test
    fun cursorDoesNotMoveUpWhenAtTop() {
        cursor.setPosition(0, 0)
        cursor.moveUp(N)
        assertPosition(0, 0)
    }

    @Test
    fun cursorDoesNotMoveDownWhenAtBottom() {
        cursor.setPosition(0, maxRows - 1)
        cursor.moveDown(N)
        assertPosition(0, maxRows - 1)
    }

    @Test
    fun cursorDoesNotMoveLeftWhenAtLeftmost() {
        cursor.setPosition(0, 0)
        cursor.moveLeft(N)
        assertPosition(0, 0)
    }

    @Test
    fun cursorDoesNotMoveRightWhenAtRightmost() {
        cursor.setPosition(maxColumns - 1, 0)
        cursor.moveRight(N)
        assertPosition(maxColumns - 1, 0)
    }
}
