package arch.cayenne.lib.common.data.constants

enum class SkinType(val value: String) {

    SKIN_CLASSIC("classic"),
    SKIN_BLACK_BLUE("black_blue"),
    SKIN_BLACK_GREEN("black_green"),
    SKIN_BLACK_RED("black_red"),
    SKIN_WHITE_BLUE("white_blue"),
    SKIN_WHITE_GREEN("white_green");

    companion object {
        val DEFAULT = SKIN_BLACK_GREEN.value
        fun of(v: String): SkinType? = entries.find { it.value == v }

        /**
         *  UI界面上有6种主题，但是逻辑上暂时就白蓝和经典两种
         *  黑色映射成经典，白色映射成白蓝
         * @param skinType
         * @return
         */
        fun getLogicSkinType(skinType: String): String {
            return when (skinType) {
                SKIN_WHITE_BLUE.value -> SKIN_WHITE_BLUE.value
                SKIN_WHITE_GREEN.value -> SKIN_WHITE_BLUE.value
                else -> SKIN_CLASSIC.value
            }
        }
    }
}