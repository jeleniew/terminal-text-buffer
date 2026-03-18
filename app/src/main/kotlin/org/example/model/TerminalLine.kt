package org.example.model

import org.example.model.CharacterCell

class TerminalLine(
    private val maxWidth: Int,
    private var foreground: Int,
    private var background: Int,
    private var styles: Set<StyleFlag>
) {
    var cells: MutableList<CharacterCell> = MutableList(maxWidth) {
        CharacterCell(' ', foreground, background, styles)
    }

    fun replaceText(newText: String, startColumn: Int) {
        for (i in newText.indices) {
            if (startColumn + i >= maxWidth) break

            val char = newText[i]
            cells[startColumn + i] = CharacterCell(char, foreground, background, styles)
        }
    }

    fun fill(character: Char) {
        for (i in cells.indices) {
            cells[i] = CharacterCell(character, foreground, background, styles)
        }
    }
}