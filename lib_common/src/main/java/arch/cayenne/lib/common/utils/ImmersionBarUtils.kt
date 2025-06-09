package arch.cayenne.lib.common.utils

import arch.cayenne.lib.common.data.constants.SkinType
import arch.cayenne.lib.common.R

object ImmersionBarUtils {
    fun immersionBarColorExt(type: String):Int{
        return when (SkinType.of(type)){
                SkinType.SKIN_CLASSIC->{
                    R.color.title_bg_classic
                }
                SkinType.SKIN_BLACK_BLUE->{
                    R.color.title_bg_black_blue
                }
                SkinType.SKIN_BLACK_RED->{
                    R.color.title_bg_black_red
                }
                SkinType.SKIN_BLACK_GREEN->{
                    R.color.title_bg
                }
                SkinType.SKIN_WHITE_GREEN->{
                    R.color.title_bg_white_green
                }
                SkinType.SKIN_WHITE_BLUE->{
                    R.color.title_bg_white_blue
                }
                else -> throw IllegalStateException("❌ Unsupported type: $type")
        }
    }

    fun immersionBarSkinTypeExt(type: String): Boolean{
      return  when (SkinType.of(type)) {
            SkinType.SKIN_CLASSIC, SkinType.SKIN_BLACK_BLUE, SkinType.SKIN_BLACK_GREEN, SkinType.SKIN_BLACK_RED -> {
              false
            }
            SkinType.SKIN_WHITE_BLUE, SkinType.SKIN_WHITE_GREEN -> {
               true
            }
            else -> throw IllegalStateException("❌ Unsupported type: $type")
        }
    }
}

