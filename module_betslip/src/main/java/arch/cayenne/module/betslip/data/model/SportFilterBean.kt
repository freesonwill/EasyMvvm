package arch.cayenne.module.betslip.data.model

import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.module.betslip.R

data class SportFilterBean(
    val sportId: Int,
    val sportName: String,
    var isSelected: Boolean = false
) {
    companion object {
        const val ALL_TYPE_ID = -1
        fun getAllTypeBean(): SportFilterBean {
            return SportFilterBean(
                sportId = ALL_TYPE_ID,
                sportName = R.string.sport_picker_all.getString(),
                isSelected = true
            )
        }
    }
}