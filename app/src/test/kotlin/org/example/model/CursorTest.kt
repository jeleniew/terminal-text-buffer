package org.example

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.example.model.Cursor

class CursorTest {
    private lateinit var cursor: Cursor

    @BeforeEach
    fun setUp() {
        cursor = Cursor(maxColumns = 80, maxRows = 24)
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
        cursor.moveUp()
        assertPosition(40, 11)
    }

    @Test
    fun cursorCanMoveDown() {
        cursor.moveDown()
        assertPosition(40, 13)
    }

    @Test
    fun cursorCanMoveLeft() {
        cursor.moveLeft()
        assertPosition(39, 12)
    }

    @Test
    fun cursorCanMoveRight() {
        cursor.moveRight()
        assertPosition(41, 12)
    }

    @Test
    fun cursorDoesNotMoveUpWhenAtTop() {
        cursor.setPosition(0, 0)
        cursor.moveUp()
        assertPosition(0, 0)
    }

    @Test
    fun cursorDoesNotMoveDownWhenAtBottom() {
        cursor.setPosition(0, 23)
        cursor.moveDown()
        assertPosition(0, 23)
    }

    @Test
    fun cursorDoesNotMoveLeftWhenAtLeftmost() {
        cursor.setPosition(0, 0)
        cursor.moveLeft()
        assertPosition(0, 0)
    }

    @Test
    fun cursorDoesNotMoveRightWhenAtRightmost() {
        cursor.setPosition(79, 0)
        cursor.moveRight()
        assertPosition(79, 0)
    }
}
