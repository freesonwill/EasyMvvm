package arch.cayenne.module.chat.ui.fragment
import android.annotation.SuppressLint
import arch.cayenne.module.chat.databinding.FragmentChatUserInfoBinding
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.chat.R
import arch.cayenne.module.chat.ui.viewmodel.ChatUserInfoViewModel
import com.walisport.module.live.ui.widget.ChatInfoGestureListener
import com.walisport.module.live.ui.widget.ChatUserInfoLayoutInterceptTouch
import com.walisport.module.live.ui.widget.ChatUserInfoLayoutInterceptTouch.ChatInfoSlideDirection
import kotlin.reflect.KClass

class ChatUserInfoFragment : BaseFragment<ChatUserInfoViewModel,FragmentChatUserInfoBinding>() {

    override val vbClass: KClass<FragmentChatUserInfoBinding>
        get() = FragmentChatUserInfoBinding::class
    override val vmClass: KClass<ChatUserInfoViewModel>
        get() = ChatUserInfoViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {
        // 上层 View 触摸事件
        mBinding.LayoutInterceptTouch.setOnTouchListener { _, event ->
            // 将触摸事件传递给下层 View
            mBinding.content.dispatchTouchEvent(event)
            false // 返回 false 不消耗事件，允许事件继续传递
        }

        mBinding.LayoutInterceptTouch.seGestureListener(object : ChatInfoGestureListener{
            override fun onAdjustLayoutScroll(deltaY: Float,direction: ChatInfoSlideDirection) {
                //往下滑动,子类的rv,sc是否滑到了第一条或者顶部
                if (direction==ChatInfoSlideDirection.DOWN){
                    // 如果当前高度在 max-min  范围内，返回 true，表示可以滑动
                    if (mBinding.topScale.isDirectionToScroll()){
                        mBinding.topScale.adjustLayout(deltaY,direction)
                    }else{
                        mBinding.topScale.adjustLayout(deltaY,direction)
//                        var bool : Boolean? = mViewModel.sonVerticalScrollIsTop.value
//                        bool?.let {
//                            if(it) mBinding.topScale.adjustLayout(deltaY,direction)
//                        }
                    }
                }else{
                    mBinding.topScale.adjustLayout(deltaY,direction)
                }
            }
        })
    }

    override suspend fun createObserver() {

    }

}