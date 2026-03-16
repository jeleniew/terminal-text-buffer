package org.example.model

class Cursor(
    var column: Int = 0,
    var row: Int = 0,
) {
    fun getPostion(): Pair<Int, Int> {
        return Pair(column, row)
    }

    fun setPosition(column: Int, row: Int) {
        this.column = column
        this.row = row
    }

    fun moveUp() {
        row--
    }

    fun moveDown() {
        row++
    }

    fun moveLeft() {
        column--
    }

    fun moveRight() {
        column++
    }
}