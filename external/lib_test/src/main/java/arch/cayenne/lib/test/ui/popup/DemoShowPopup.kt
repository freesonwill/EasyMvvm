package arch.cayenne.lib.test.ui.popup

import android.content.Context
import android.view.View
import android.view.animation.PathInterpolator
import arch.cayenne.lib.base.ui.animation.PathInterpolatorOption
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.test.R
import arch.cayenne.lib.test.data.bean.DemoData
import arch.cayenne.lib.test.databinding.DemoPopupShowBinding
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ScreenUtils
import com.google.gson.Gson
import com.lxj.xpopup.core.BottomPopupView
import org.koin.java.KoinJavaComponent.inject
import kotlin.getValue

class DemoShowPopup(context: Context,private val demoData:DemoData) : BottomPopupView(context) {
    private var vb: DemoPopupShowBinding? = null
    private val manager: UserDataManager by inject(UserDataManager::class.java)

    override fun getImplLayoutId(): Int {
        return R.layout.demo_popup_show
    }

    override fun getPopupHeight(): Int {
        return (ScreenUtils.getScreenHeight() * 0.55).toInt()
    }
    override fun onCreate() {
        super.onCreate()
        vb = DemoPopupShowBinding.bind(popupImplView)
        vb?.apply {
            view.translationX = 0f
            view.alpha = 1f
            var width1 = 500f
            viewRoot.postDelayed( {
                width1 = viewRoot.width.toFloat()
                width1.toString().loge("width1")
                start(view,width1)
            },500)
            tvCancel.clickNoRepeat { dismiss() }
            tvOk.clickNoRepeat { start(view,width1) }
        }
    }

    private fun start(view: View,x: Float) {
        view.animate()
            .translationX(x)
            .setDuration((demoData.duration))
            .setInterpolator(PathInterpolator(demoData.controlX1, demoData.controlY1, demoData.controlX2, demoData.controlY2))
            .withEndAction {
                view.translationX = 0f
            }
            .start()
    }
}