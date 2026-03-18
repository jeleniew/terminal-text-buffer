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
    var screen: List<TerminalLine> = listOf(
        TerminalLine(width, foreground, background, styles)
    )
    val scrollback: List<TerminalLine> = emptyList()

    fun write(text: String) {
        var position = cursor.getPosition()
        if (position.second >= screen.size) {
            for (i in screen.size..position.second) {
                screen = screen + 
                    TerminalLine(width, foreground, background, styles)
            }
        }
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

                if (row + 1 >= height) {
                    break
                }

                cursor.setPosition(0, row + 1)
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

    fun addNewLine() {
        if (screen.size < height) {
            screen = screen + TerminalLine(width, foreground, background, styles)
        }
    }
}