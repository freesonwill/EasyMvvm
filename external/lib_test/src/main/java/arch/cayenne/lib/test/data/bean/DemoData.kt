package arch.cayenne.lib.test.data.bean

import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.PathInterpolatorOption
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager

open class DemoData(
    val duration: Long,
    val controlX1: Float,
    val controlY1: Float,
    val controlX2: Float,
    val controlY2: Float
){
    class ScaleDemoData(
        duration: Long,
        controlX1: Float,
        controlY1: Float,
        controlX2: Float,
        controlY2: Float,
        val alpha:FloatArray,
        val scale:FloatArray,
    ): DemoData(duration,controlX1,controlY1, controlX2, controlY2)

    companion object {
        fun setDemoData(manager: UserDataManager, key: UserDataKey, data: DemoData,serialize:Boolean = true,){
            if(serialize) manager.setKeyValue(key,data)
            when(key){
                UserDataKey.KEY_ANIM_ROUTE -> AnimationController.setRouteAnim(data.duration, PathInterpolatorOption(data.controlX1,data.controlY1,data.controlX2,data.controlY2))
                UserDataKey.KEY_ANIM_ZOOM -> AnimationController.setZoomInAnim(
                    data.duration,
                    PathInterpolatorOption(data.controlX1,data.controlY1,data.controlX2,data.controlY2),
                    alpha = (data as ScaleDemoData).alpha,
                    scale = (data).scale
                )
                UserDataKey.KEY_ANIM_POPUP -> AnimationController.setPopupAnim(data.duration, PathInterpolatorOption(data.controlX1,data.controlY1,data.controlX2,data.controlY2))
                UserDataKey.KEY_ANIM_DRAWER -> AnimationController.setDrawerAnim(data.duration, PathInterpolatorOption(data.controlX1,data.controlY1,data.controlX2,data.controlY2))
                UserDataKey.KEY_ANIM_SCROLLBAR -> AnimationController.setScrollBarAnim(data.duration, PathInterpolatorOption(data.controlX1,data.controlY1,data.controlX2,data.controlY2))
                else -> throw IllegalStateException("❌ Unsupported key: $key")
            }
        }

        inline fun <reified T:DemoData> getDemoData(manager: UserDataManager, key: UserDataKey): T? {
            return manager.getValue<T>(key)
        }
    }
}