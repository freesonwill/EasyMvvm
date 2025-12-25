package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.view.contains
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.databinding.LayoutEmptyErrorCloseV2Binding
import arch.cayenne.lib.common.ui.view.DynamicStateLayout.States
import arch.cayenne.lib.skin.widget.SkinnableConstraintLayout

class DynamicStateLayoutV2 @JvmOverloads constructor(
    context: Context ,
    attrs: AttributeSet? = null ,
    defStyleAttr: Int = 0
) : SkinnableConstraintLayout(context , attrs , defStyleAttr) {

    //刷新，数据为空,网络异常,关闭
    val binding =
        LayoutEmptyErrorCloseV2Binding.inflate(LayoutInflater.from(context) , this , false)



    // 设置当前状态
    fun setState(state: States , msg: String) {
        when (state) {
            States.DATA_EMPTY -> {
                binding.root.visibility = VISIBLE
                //图片
                binding.ivIcon.visibility = VISIBLE
                binding.ivIcon.setBackgroundResource(R.drawable.data_empty)
                //说明文字
                binding.tvMessage.visibility = VISIBLE
                binding.tvMessage.text = msg

            }

            is States.NETWORK_ANOMALY -> {
                binding.root.visibility = VISIBLE
                //图片
                binding.ivIcon.visibility = VISIBLE
                binding.ivIcon.setBackgroundResource(R.drawable.data_empty)
                //说明文字
                binding.tvMessage.visibility = VISIBLE
                binding.tvMessage.text = msg
                //刷新按钮

            }

            States.CLOSE -> {
                binding.root.visibility = VISIBLE
                //图片
                binding.ivIcon.visibility = VISIBLE
                binding.ivIcon.setBackgroundResource(R.drawable.bg_close)
                //说明文字
                binding.tvMessage.visibility = VISIBLE
                binding.tvMessage.text = msg



            }

            States.NULL -> {
                binding.root.visibility = GONE
            }

            States.LOADING -> {
                binding.root.visibility = VISIBLE
                //图片
                binding.ivIcon.visibility = GONE
                //说明文字
                binding.tvMessage.visibility = GONE


            }
        }

        if (!this.contains(binding.root)) {
            this.addView(binding.root)
        }

    }

}