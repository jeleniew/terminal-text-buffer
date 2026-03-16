enum class StyleFlag {
    BOLD,
    ITALIC,
    UNDERLINE,
}

class CharacterCell(
    val char: Char = ' ',
    val fgColor: Int,
    val bgColor: Int,
    val styleFlags: Set<StyleFlag>
)