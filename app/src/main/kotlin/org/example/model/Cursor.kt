package org.example.model

class Cursor(
    private var column: Int = 0,
    private var row: Int = 0,
    private val maxColumns: Int,
    private val maxRows: Int
) {
    fun getPosition(): Pair<Int, Int> {
        return Pair(column, row)
    }

    fun setPosition(column: Int, row: Int) {
        if (column in 0 until maxColumns && row in 0 until maxRows) {
            this.column = column
            this.row = row
        }
    }

    fun moveUp() {
        if (row > 0)
            row--
    }

    fun moveDown() {
        if (row < maxRows - 1)
            row++
    }

    fun moveLeft() {
        if (column > 0)
            column--
    }

    fun moveRight() {
        if (column < maxColumns - 1)
            column++
    }
}