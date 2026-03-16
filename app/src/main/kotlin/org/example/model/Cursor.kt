package org.example.model

class Cursor(
    var column: Int = 0,
    var row: Int = 0,
    private val maxColumns: Int,
    private val maxRows: Int
) {
    fun getPostion(): Pair<Int, Int> {
        return Pair(column, row)
    }

    fun setPosition(column: Int, row: Int) {
        this.column = column
        this.row = row
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