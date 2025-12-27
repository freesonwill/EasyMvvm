package arch.cayenne.module.chat.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.Window
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import arch.cayenne.lib.base.ui.fragment.BasePositionDialogFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.chat.R
import arch.cayenne.module.chat.databinding.FragmentLanguageDialogLayoutBinding
import arch.cayenne.module.chat.ui.adapter.LanguageAdapter
import arch.cayenne.module.chat.ui.viewmodel.ChatLanguageDialogViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

/**
 * @author: wenxi
 * @date: 30/10/25 10:26
 * @description:
 */
class ChatLanguageDialogFragment :
    BasePositionDialogFragment<ChatLanguageDialogViewModel, FragmentLanguageDialogLayoutBinding>() {
    companion object {
        private const val LOCATION_X = "locationX"
        private const val LOCATION_Y = "locationY"
        private const val SELECT_POSITION = "selectPosition"

        fun newInstance(
            positionX: Int,
            positionY: Int,
            selectPosition:Int,
            listenerLanguage:(position:Int) -> Unit
        ): ChatLanguageDialogFragment {
            val b = Bundle()
            b.putInt(LOCATION_X, positionX)
            b.putInt(LOCATION_Y, positionY)
            b.putInt(SELECT_POSITION,selectPosition)
            return ChatLanguageDialogFragment().apply {
                arguments = b
                addSelectListen {
                    listenerLanguage.invoke(it)
                }
            }
        }
    }

    private var selectListener:((position:Int)->Unit)? = null
    private var selectPosition:Int = -1


    override fun setDialogPosition(w: Window) {
        val positionX = requireArguments().getInt(LOCATION_X, -1)
        val positionY = requireArguments().getInt(LOCATION_Y, -1)
        if (positionX != -1 && positionY != -1) {
            val layoutParams = w.attributes
            layoutParams.gravity = Gravity.BOTTOM or Gravity.START
            layoutParams.width = 284.dp2px
            layoutParams.height = 399.dp2px
            layoutParams.x = 10.dp2px
            layoutParams.y = 125.dp2px
            layoutParams.dimAmount = 0f
            w.attributes = layoutParams

        }
    }

    override val vbClass: KClass<FragmentLanguageDialogLayoutBinding>
        get() = FragmentLanguageDialogLayoutBinding::class
    override val vmClass: KClass<ChatLanguageDialogViewModel>
        get() = ChatLanguageDialogViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        selectPosition = requireArguments().getInt(SELECT_POSITION,-1)
        mBinding.apply {
            recycler.layoutManager = GridLayoutManager(requireActivity(),2)
            val nAdapter = LanguageAdapter()
            nAdapter.addSelectListenPosition(selectPosition){ position ->
                selectListener?.invoke(position)
            }
            nAdapter.submitList(resources.getStringArray(R.array.languages).toList())
            nAdapter.setRecyclerItemClick(object :RecyclerItemListener<String>{
                override fun onItemClick(item: String?, position: Int) {
                    lifecycleScope.launch {
                        delay(200)
                     if(dialog?.isShowing == true){
                         dismiss()
                     }
                    }
                }
            })
            recycler.adapter = nAdapter
        }

    }

    fun addSelectListen(listen:(position:Int) -> Unit){
        selectListener = listen
    }


    override fun initListener() {
    }
}