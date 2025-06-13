package arch.cayenne.lib.common.ui.view

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.LinearInterpolator
import androidx.core.view.contains
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.databinding.LayoutEmptyErrorCloseBinding
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.startSafeObjectAnimator
import arch.cayenne.lib.skin.widget.SkinnableConstraintLayout

class DynamicStateLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SkinnableConstraintLayout(context, attrs, defStyleAttr) {

    //数据为空,网络异常,关闭
    val binding = LayoutEmptyErrorCloseBinding.inflate(LayoutInflater.from(context), this, false)

    enum class States {
        LOADING,//加载中
        DATA_EMPTY,//数据为空
        NETWORK_ANOMALY,//网络异常
        CLOSE,//关闭
        NULL,
    }

    /**
     * 转圈动画
     */
    private var loadingAnim: ObjectAnimator? = null

    // 设置当前状态
    fun setState(state: States, msg: String, onRefresh: (() -> Unit)? = null) {
        when (state) {
            States.DATA_EMPTY -> {
                binding.root.visibility = VISIBLE
                //图片
                binding.ivIcon.visibility = VISIBLE
                binding.ivIcon.setBackgroundResource(R.drawable.icon_empty)
                //说明文字
                binding.tvMessage.visibility = VISIBLE
                binding.tvMessage.text = msg
                //刷新按钮
                binding.btnRefresh.visibility = GONE
                //加载态
                binding.llLoading.visibility = GONE
                loadingAnim?.cancel()
            }

            States.NETWORK_ANOMALY -> {
                binding.root.visibility = VISIBLE
                //图片
                binding.ivIcon.visibility = VISIBLE
                binding.ivIcon.setBackgroundResource(R.drawable.icon_error_net)
                //说明文字
                binding.tvMessage.visibility = VISIBLE
                binding.tvMessage.text = msg
                //刷新按钮
                binding.btnRefresh.visibility = VISIBLE
                binding.btnRefresh.clickNoRepeat { onRefresh?.invoke() }
                //加载态
                binding.llLoading.visibility = GONE
                loadingAnim?.cancel()
            }

            States.CLOSE -> {
                binding.root.visibility = VISIBLE
                //图片
                binding.ivIcon.visibility = VISIBLE
                binding.ivIcon.setBackgroundResource(R.drawable.icon_close)
                //说明文字
                binding.tvMessage.visibility = VISIBLE
                binding.tvMessage.text = msg
                //刷新按钮
                binding.btnRefresh.visibility = GONE
                //加载态
                binding.llLoading.visibility = GONE
                loadingAnim?.cancel()

            }

            States.NULL -> {
                binding.root.visibility = GONE
                loadingAnim?.cancel()
            }

            States.LOADING -> {
                binding.root.visibility = VISIBLE
                //图片
                binding.ivIcon.visibility = GONE
                //说明文字
                binding.tvMessage.visibility = GONE
                //刷新按钮
                binding.btnRefresh.visibility = GONE
                //加载态
                binding.llLoading.visibility = VISIBLE

                // 创建旋转动画
                loadingAnim = binding.ivLoading.startSafeObjectAnimator(
                    "rotation",  // 属性名称
                    0f, 360f, // 从 0 度旋转到 360 度
                    duration = 1000L, // 持续时间 1 秒
                    repeatCount = ObjectAnimator.INFINITE, // 无限循环
                    interpolator = LinearInterpolator() // 匀速旋转
                )
            }
        }

        if (!this.contains(binding.root)) {
            this.addView(binding.root)
        }

    }

    fun setVisibilityGone() {
        binding.root.visibility = GONE
    }
}