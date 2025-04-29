package arch.cayenne.lib.common.ui.view

import arch.cayenne.lib.common.R
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.contains
import arch.cayenne.lib.skin.widget.SportConstraintLayout

class DynamicStateLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SportConstraintLayout(context, attrs, defStyleAttr) {

    //数据为空,网络异常,关闭
    private var emptyView: View =
        LayoutInflater.from(context).inflate(R.layout.layout_empty_error_close, this, false)

    enum class States {
        DATA_EMPTY,//数据为空
        NETWORK_ANOMALY,//网络异常
        CLOSE,//关闭含,聊天,盘口
        NULL,
    }
    private var currentState: States = States.NULL

    // 设置当前状态
    fun setState(state: States,msg:String) {
        currentState = state
        when(currentState){
            States.DATA_EMPTY->{
                emptyView.findViewById<ImageView>(R.id.iv_icon).setBackgroundResource(R.drawable.icon_empty)
            }
            States.NETWORK_ANOMALY->{
                emptyView.findViewById<ImageView>(R.id.iv_icon).setBackgroundResource(R.drawable.icon_error_net)
            }
            States.CLOSE->{
                emptyView.findViewById<ImageView>(R.id.iv_icon).setBackgroundResource(R.drawable.icon_close)
            }
            States.NULL ->{}
        }
        emptyView.findViewById<TextView>(R.id.tv_message).text=msg
        if(!this.contains(emptyView)){
            addView(emptyView)
        }
    }

    fun setVisibilityGone(){
        if (currentState!=States.NULL){
            currentState = States.NULL
            emptyView.visibility = GONE
        }
    }
}