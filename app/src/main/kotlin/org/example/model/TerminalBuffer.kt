package org.example.model

import org.example.model.TerminalLine

class TerminalBuffer(
    val width: Int,
    val height: Int,
    val scrollbackMaxSize: Int
) {
    var foreground: Int = 0
    var background: Int = 0
    var styles: Set<StyleFlag> = emptySet()
    var cursor: Cursor = Cursor(maxColumns = width, maxRows = height)
    var screen: MutableList<TerminalLine> = MutableList(height) {
        TerminalLine(width, foreground, background, styles)
    }
    private var scrollback: MutableList<TerminalLine> = mutableListOf()

    fun getScrollback(): List<TerminalLine> {
        return scrollback.toList()
    }

    private fun moveLineFromScreenToScrollback() {
        val lineToMove = screen.first()
        scrollback.add(lineToMove)
        
        if (scrollback.size > scrollbackMaxSize) {
            scrollback = scrollback.drop(scrollback.size - scrollbackMaxSize)
                .toMutableList()
        }
        screen.removeAt(0)
        screen.add(TerminalLine(width, foreground, background, styles))
    }

    fun write(text: String) {
        var position = cursor.getPosition()
        val currentLine = screen[position.second]

        currentLine.replaceText(text, position.first)
        cursor.moveRight(text.length)
    }

    fun insert(text: String) {
        val splitText = text.split("\n")
        for ((index, line) in splitText.withIndex()) {
            write(line)

            if (index < splitText.lastIndex) {
                val (_, row) = cursor.getPosition()

                val newRow = if (row < height - 1) {
                    row + 1
                } else {
                    moveLineFromScreenToScrollback()
                    row
                }
                cursor.setPosition(0, newRow)
            }
        }
    }

    fun fillLine(character: Char) {
        cursor.getPosition().let { (_, row) ->
            if (row < screen.size) {
                screen[row].fill(character)
            }
        }
    }

    fun clearScreen() {
        screen = MutableList(height) {
            TerminalLine(width, foreground, background, styles)
        }
        cursor.setPosition(0, 0)
    }

    private fun clearScrollback() {
        scrollback = mutableListOf()
    }

    fun clearScreenAndScrollback() {
        clearScreen()
        clearScrollback()
    }

    fun getCharacterFromScreenAt(column: Int, row: Int): Char? {
        if (row < 0 || row >= screen.size) return null
        val line = screen[row]
        if (column < 0 || column >= line.cells.size) return null
        return line.cells[column].char
    }

    fun getCharacterFromScrollbackAt(column: Int, row: Int): Char? {
        if (row < 0 || row >= scrollback.size) return null
        val line = scrollback[row]
        if (column < 0 || column >= line.cells.size) return null
        return line.cells[column].char
    }

    fun getAttributesFromScreenAt(column: Int, row: Int): CharacterCell? {
        if (row < 0 || row >= screen.size) return null
        val line = screen[row]
        if (column < 0 || column >= line.cells.size) return null
        return line.cells[column]
    }

    fun getAttributesFromScrollbackAt(column: Int, row: Int): CharacterCell? {
        if (row < 0 || row >= scrollback.size) return null
        val line = scrollback[row]
        if (column < 0 || column >= line.cells.size) return null
        return line.cells[column]
    }

    fun getLineAsStringFromScreenAt(row: Int): String? {
        if (row < 0 || row >= screen.size) return null
        return screen[row].cells.map { it.char }.joinToString("")
    }

    fun getLineAsStringFromScrollbackAt(row: Int): String? {
        if (row < 0 || row >= scrollback.size) return null
        return scrollback[row].cells.map { it.char }.joinToString("")
    }

    fun getEntireScreenContentAsString(): String {
        val nonEmptyLines = screen.dropLastWhile { line ->
            line.cells.all { it.char == ' ' }
        }
        return nonEmptyLines.joinToString("\n") { line ->
            line.cells.map { it.char }.joinToString("").trimEnd()
        }
    }

    private fun getEntireScrollbackContentAsString(): String {
        return scrollback.joinToString("\n") { line ->
            line.cells.map { it.char }.joinToString("").trimEnd()
        }
    }

    fun getEntireContentAsString(): String {
        val scrollbackContent = getEntireScrollbackContentAsString()
        val screenContent = getEntireScreenContentAsString()
        return if (scrollbackContent.isEmpty()) {
            screenContent
        } else {
            "$scrollbackContent\n$screenContent"
        }
    }
}