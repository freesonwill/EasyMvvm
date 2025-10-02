package arch.cayenne.lib.common.utils

import arch.cayenne.lib.common.data.constants.SkinType
import arch.cayenne.lib.common.R

object ImmersionBarUtils {
    fun immersionBarColorExt(type: String): Int {
        return when (SkinType.of(type)) {
            SkinType.SKIN_BLACK_BLUE -> {
                R.color.title_bg
            }

            SkinType.SKIN_WHITE_BLUE -> {
                R.color.title_bg_white_blue
            }

            else -> throw IllegalStateException("❌ Unsupported type: $type")
        }
    }

    fun immersionBarSkinTypeExt(type: String): Boolean {
        return when (SkinType.of(type)) {
            SkinType.SKIN_BLACK_BLUE -> {
                false
            }

            SkinType.SKIN_WHITE_BLUE -> {
                true
            }

            else -> throw IllegalStateException("❌ Unsupported type: $type")
        }
    }
}

