package arch.cayenne.lib.common.ui.view

import arch.cayenne.lib.common.R
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.view.contains
import arch.cayenne.lib.common.databinding.LayoutEmptyErrorCloseBinding
import arch.cayenne.lib.skin.res.SportSkinResourceManager
import arch.cayenne.lib.skin.widget.SportConstraintLayout

class DynamicStateLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SportConstraintLayout(context, attrs, defStyleAttr) {

    //数据为空,网络异常,关闭
    val binding = LayoutEmptyErrorCloseBinding.inflate(LayoutInflater.from(context), this, false)

    enum class States {
        DATA_EMPTY,//数据为空
        NETWORK_ANOMALY,//网络异常
        CLOSE,//关闭含,聊天,盘口
        NULL,
    }

    private var currentState: States = States.NULL

    // 设置当前状态
    fun setState(state: States, msg: String) {
        if (currentState != States.NULL) {
            removeView(binding.root)
        }
        currentState = state
        when (currentState) {
            States.DATA_EMPTY -> {
                binding.ivIcon.background =
                    SportSkinResourceManager.getDrawable(context, R.drawable.icon_empty)
            }

            States.NETWORK_ANOMALY -> {
                binding.ivIcon.background =
                    SportSkinResourceManager.getDrawable(context, R.drawable.icon_error_net)
            }

            States.CLOSE -> {
                binding.ivIcon.background =
                    SportSkinResourceManager.getDrawable(context, R.drawable.icon_close)
            }

            States.NULL -> {}
        }
        binding.tvMessage.text = msg
        if (!this.contains(binding.root)) {
            addView(binding.root)
        }
    }

    fun setVisibilityGone() {
        if (currentState != States.NULL) {
            removeView(binding.root)
            currentState = States.NULL
        }
    }
}