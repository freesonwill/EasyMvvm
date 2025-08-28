package arch.cayenne.lib.test.data.bean

import arch.cayenne.lib.base.ui.animation.AnimationController
import arch.cayenne.lib.base.ui.animation.PathInterpolatorOption
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import com.google.gson.Gson

data class DemoData(
    var duration: Long,
    var controlX1: Float,
    var controlY1: Float,
    var controlX2: Float,
    var controlY2: Float
){
    companion object {
        fun setDemoData(manager: UserDataManager, key: UserDataKey, data: DemoData,serialize:Boolean = true){
            if(serialize) manager.setKeyValue(key,data)
            when(key){
                UserDataKey.KEY_ANIM_ROUTE -> AnimationController.setRouteAnim(data.duration, PathInterpolatorOption(data.controlX1,data.controlY1,data.controlX2,data.controlY2))
                UserDataKey.KEY_ANIM_ZOOM -> AnimationController.setZoomInAnim(data.duration, PathInterpolatorOption(data.controlX1,data.controlY1,data.controlX2,data.controlY2))
                UserDataKey.KEY_ANIM_POPUP -> AnimationController.setPopupAnim(data.duration, PathInterpolatorOption(data.controlX1,data.controlY1,data.controlX2,data.controlY2))
                UserDataKey.KEY_ANIM_DRAWER -> AnimationController.setDrawerAnim(data.duration, PathInterpolatorOption(data.controlX1,data.controlY1,data.controlX2,data.controlY2))
                UserDataKey.KEY_ANIM_SCROLLBAR -> AnimationController.setScrollBarAnim(data.duration, PathInterpolatorOption(data.controlX1,data.controlY1,data.controlX2,data.controlY2))
                else -> throw IllegalStateException("❌ Unsupported key: $key")
            }
        }

        fun getDemoData(manager: UserDataManager, key: UserDataKey):DemoData? {
            return manager.getValue(key)
        }
    }
}