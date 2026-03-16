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

    fun moveUp(n: Int) {
        row = Math.max(0, row - n)
    }

    fun moveDown(n: Int) {
        row = Math.min(maxRows - 1, row + n)
    }

    fun moveLeft(n: Int) {
        column = Math.max(0, column - n)
    }

    fun moveRight(n: Int) {
        column = Math.min(maxColumns - 1, column + n)
    }
}