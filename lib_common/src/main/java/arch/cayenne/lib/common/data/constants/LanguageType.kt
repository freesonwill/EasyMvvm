package arch.cayenne.lib.common.data.constants

enum class LanguageType(val value: String) {
    LANGUAGE_SIMPLE("zh"),   //简体中文
    LANGUAGE_ENGLISH("en"),  //英文
    LANGUAGE_ID("in"),       //印尼
    LANGUAGE_PT("pt");       //葡萄牙语

    companion object {
        fun findLanguage(value: String): LanguageType {
            return entries.firstOrNull { it.value == value } ?: LANGUAGE_SIMPLE
        }
    }
}
