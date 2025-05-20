package arch.cayenne.module.betslip.data.model

import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.module.betslip.R

data class SportFilterBean(
    val sportId: Int,
    val sportName: String,
    var isSelected: Boolean = false
) {
    companion object {
        fun getAllTypeBean(): SportFilterBean {
            return SportFilterBean(
                sportId = -1,
                sportName = R.string.sport_picker_all.getString()
            )
        }
    }
}