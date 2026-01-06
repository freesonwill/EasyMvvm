
import android.content.Context
import android.text.InputFilter
import android.text.Spanned
import arch.cayenne.lib.base.BuildConfig
import arch.cayenne.lib.base.utils.ext.launch
import arch.cayenne.lib.base.utils.log.Utils
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.DimensionExt.px2dp
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.test.R
import arch.cayenne.lib.test.data.bean.DemoData
import arch.cayenne.lib.test.databinding.DemoPopupBinding
import arch.cayenne.lib.test.ui.popup.DemoShowPopup
import com.blankj.utilcode.util.ScreenUtils
import com.gyf.immersionbar.ImmersionBar
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import org.koin.java.KoinJavaComponent.inject
import java.util.Locale
import kotlin.getValue

class DemoPopup(context: Context) : BottomPopupView(context) {
    private var vb: DemoPopupBinding? = null
    private val manager: UserDataManager by inject(UserDataManager::class.java)

    override fun getImplLayoutId(): Int { return R.layout.demo_popup }
    override fun getPopupHeight(): Int {
        return (ScreenUtils.getScreenHeight() * 0.7).toInt()
    }

    override fun onCreate() {
        super.onCreate()
        vb = DemoPopupBinding.bind(popupImplView)

        vb?.apply {

            val data1 = DemoData.getDemoData(manager,UserDataKey.KEY_ANIM_ROUTE)!!
            data1Time.setText(data1.duration.toString())
            data1X1.setText(data1.controlX1.toString())
            data1Y1.setText(data1.controlY1.toString())
            data1X2.setText(data1.controlX2.toString())
            data1Y2.setText(data1.controlY2.toString())

            val data2 = DemoData.getDemoData(manager,UserDataKey.KEY_ANIM_ZOOM)!!
            data2Time.setText(data2.duration.toString())
            data2X1.setText(data2.controlX1.toString())
            data2Y1.setText(data2.controlY1.toString())
            data2X2.setText(data2.controlX2.toString())
            data2Y2.setText(data2.controlY2.toString())

            val data3 = DemoData.getDemoData(manager,UserDataKey.KEY_ANIM_POPUP)!!
            data3Time.setText(data3.duration.toString())
            data3X1.setText(data3.controlX1.toString())
            data3Y1.setText(data3.controlY1.toString())
            data3X2.setText(data3.controlX2.toString())
            data3Y2.setText(data3.controlY2.toString())

            val data4 = DemoData.getDemoData(manager,UserDataKey.KEY_ANIM_DRAWER)!!
            data4Time.setText(data4.duration.toString())
            data4X1.setText(data4.controlX1.toString())
            data4Y1.setText(data4.controlY1.toString())
            data4X2.setText(data4.controlX2.toString())
            data4Y2.setText(data4.controlY2.toString())

            val data5 = DemoData.getDemoData(manager,UserDataKey.KEY_ANIM_SCROLLBAR)!!
            data5Time.setText(data5.duration.toString())
            data5X1.setText(data5.controlX1.toString())
            data5Y1.setText(data5.controlY1.toString())
            data5X2.setText(data5.controlX2.toString())
            data5Y2.setText(data5.controlY2.toString())


            tvCancel.clickNoRepeat { dismiss() }
            tvOk.clickNoRepeat {
                if(!checkedDataValid()) return@clickNoRepeat


                val data1 = DemoData(
                    data1Time.text!!.trim().toString().toLong(), data1X1.text!!.trim().toString().toFloat(), data1Y1.text!!.trim().toString().toFloat(), data1X2.text!!.trim().toString().toFloat(), data1Y2.text!!.trim().toString().toFloat()
                )
                val data2 = DemoData(
                    data2Time.text!!.trim().toString().toLong(), data2X1.text!!.trim().toString().toFloat(), data2Y1.text!!.trim().toString().toFloat(), data2X2.text!!.trim().toString().toFloat(), data2Y2.text!!.trim().toString().toFloat()
                )
                val data3 = DemoData(
                    data3Time.text!!.trim().toString().toLong(), data3X1.text!!.trim().toString().toFloat(), data3Y1.text!!.trim().toString().toFloat(), data3X2.text!!.trim().toString().toFloat(), data3Y2.text!!.trim().toString().toFloat()
                )
                val data4 = DemoData(
                    data4Time.text!!.trim().toString().toLong(), data4X1.text!!.trim().toString().toFloat(), data4Y1.text!!.trim().toString().toFloat(), data4X2.text!!.trim().toString().toFloat(), data4Y2.text!!.trim().toString().toFloat()
                )
                val data5 = DemoData(
                    data5Time.text!!.trim().toString().toLong(), data5X1.text!!.trim().toString().toFloat(), data5Y1.text!!.trim().toString().toFloat(), data5X2.text!!.trim().toString().toFloat(), data5Y2.text!!.trim().toString().toFloat()
                )
                DemoData.setDemoData(manager,UserDataKey.KEY_ANIM_ROUTE, data1)
                DemoData.setDemoData(manager,UserDataKey.KEY_ANIM_ZOOM, data2)
                DemoData.setDemoData(manager,UserDataKey.KEY_ANIM_POPUP, data3)
                DemoData.setDemoData(manager,UserDataKey.KEY_ANIM_DRAWER, data4)
                DemoData.setDemoData(manager,UserDataKey.KEY_ANIM_SCROLLBAR, data5)
                dismiss()
            }

            tvShow1.clickNoRepeat {
                XPopup.Builder(context).isViewMode(false).asCustom(DemoShowPopup(context,
                    DemoData(
                        data1Time.text!!.trim().toString().toLong(),
                        data1X1.text!!.trim().toString().toFloat(),
                        data1Y1.text!!.trim().toString().toFloat(),
                        data1X2.text!!.trim().toString().toFloat(),
                        data1Y2.text!!.trim().toString().toFloat())
                    )
                ).show()
            }
            tvShow2.clickNoRepeat {
                XPopup.Builder(context).isViewMode(false).asCustom(DemoShowPopup(context,
                    DemoData(
                        data2Time.text!!.trim().toString().toLong(),
                        data2X1.text!!.trim().toString().toFloat(),
                        data2Y1.text!!.trim().toString().toFloat(),
                        data2X2.text!!.trim().toString().toFloat(),
                        data2Y2.text!!.trim().toString().toFloat())
                    )
                ).show()
            }
            tvShow3.clickNoRepeat {
                XPopup.Builder(context).isViewMode(false).asCustom(DemoShowPopup(context,
                    DemoData(
                        data3Time.text!!.trim().toString().toLong(),
                        data3X1.text!!.trim().toString().toFloat(),
                        data3Y1.text!!.trim().toString().toFloat(),
                        data3X2.text!!.trim().toString().toFloat(),
                        data3Y2.text!!.trim().toString().toFloat())
                    )
                ).show()
            }
            tvShow4.clickNoRepeat {
                XPopup.Builder(context).isViewMode(false).asCustom(DemoShowPopup(context,
                    DemoData(
                        data4Time.text!!.trim().toString().toLong(),
                        data4X1.text!!.trim().toString().toFloat(),
                        data4Y1.text!!.trim().toString().toFloat(),
                        data4X2.text!!.trim().toString().toFloat(),
                        data4Y2.text!!.trim().toString().toFloat())
                )).show()
            }
            tvShow5.clickNoRepeat {
                XPopup.Builder(context).isViewMode(false).asCustom(DemoShowPopup(context,
                    DemoData(
                        data5Time.text!!.trim().toString().toLong(),
                        data5X1.text!!.trim().toString().toFloat(),
                        data5Y1.text!!.trim().toString().toFloat(),
                        data5X2.text!!.trim().toString().toFloat(),
                        data5Y2.text!!.trim().toString().toFloat())
                )).show()
            }
            arrayOf(
                data1X1,data1Y1,data1X2,data1Y2,
                data2X1,data2Y1,data2X2,data2Y2,
                data3X1,data3Y1,data3X2,data3Y2,
                data4X1,data4Y1,data4X2,data4Y2,
                data5X1,data5Y1,data5X2,data5Y2,
            ).forEach {
                it.filters = arrayOf(InputFilterMinMax(0.0, 1.0))
            }

        }
    }
    private fun checkedDataValid():Boolean{
        return true
    }

    private class InputFilterMinMax(
        private val min: Double,
        private val max: Double
    ) : InputFilter {

        override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            dest: Spanned?,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            try {
                // 拼接输入后的完整内容
                val input = (dest?.substring(0, dstart) ?: "") +
                        (source?.substring(start, end) ?: "") +
                        (dest?.substring(dend) ?: "")

                if (input.isEmpty() || input == ".") {
                    return null // 允许输入小数点，但后续判断
                }

                val value = input.toDouble()
                if (value in min..max) {
                    return null // ✅ 在范围内，允许输入
                }
            } catch (_: NumberFormatException) {
            }
            return "" // ❌ 不在范围内，禁止输入
        }
    }


}