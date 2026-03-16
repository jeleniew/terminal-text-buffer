package org.example.model

import org.example.model.StyleFlag

class CharacterCell(
    val char: Char = ' ',
    val fgColor: Int,
    val bgColor: Int,
    val styleFlags: Set<StyleFlag>
)