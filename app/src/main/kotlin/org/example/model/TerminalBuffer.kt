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
    var screen: List<TerminalLine> = emptyList()
    val scrollback: List<TerminalLine> = emptyList()
}