package arch.cayenne.lib.common.data.constants

enum class SkinType(val value: String) {

    SKIN_BLACK_BLUE("black_blue"),
    SKIN_WHITE_BLUE("white_blue");

    companion object {
        val DEFAULT = SKIN_BLACK_BLUE.value
        fun of(v: String): SkinType? = entries.find { it.value == v }
    }
}