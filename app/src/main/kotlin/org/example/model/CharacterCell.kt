package org.example.model

import org.example.model.StyleFlag

class CharacterCell(
    val char: Char = ' ',
    val fgColor: TerminalColor,
    val bgColor: TerminalColor,
    val styleFlags: Set<StyleFlag>
)