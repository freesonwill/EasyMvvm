package arch.cayenne.lib.common.utils

import arch.cayenne.lib.base.data.model.SkinType
import arch.cayenne.lib.common.R

object ImmersionBarUtils {
    fun immersionBarColorExt(type: String):Int{
        return when (type){
                SkinType.SKIN_CLASSIC.value->{
                    R.color.title_bg_classic
                }
                SkinType.SKIN_BLACK_BLUE.value->{
                    R.color.title_bg_black_blue
                }
                SkinType.SKIN_BLACK_RED.value->{
                    R.color.title_bg_black_red
                }
                SkinType.SKIN_BLACK_GREEN.value->{
                    R.color.title_bg
                }
                SkinType.SKIN_WHITE_GREEN.value->{
                    R.color.title_bg_white_green
                }
                SkinType.SKIN_WHITE_BLUE.value->{
                    R.color.title_bg_white_blue
                }
            else -> {R.color.title_bg}
        }
    }
}

