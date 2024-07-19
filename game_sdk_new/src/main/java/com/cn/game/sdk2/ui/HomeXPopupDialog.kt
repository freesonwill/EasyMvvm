package com.cn.game.sdk2.ui


import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.EventKey
import com.cn.game.sdk2.databinding.DialogHomeXpopupContainerBinding
import com.cn.game.sdk2.ui.view.VerticalBottomPopupView
import com.cn.game.sdk2.utils.FlowBus
import com.lxj.xpopup.core.BottomPopupView

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/13 18:18
 **/
class HomeXPopupDialog(context: Context, private val fragment: Fragment, private var miniGameId: Int) : VerticalBottomPopupView(context) {

    companion object {
        const val TAG = "HomeXPopupDialog"
    }
    var binding: DialogHomeXpopupContainerBinding? = null
    private var isLoadFragment = false

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        binding = DialogHomeXpopupContainerBinding.bind(popupImplView)
        if(!fragment.isAdded){
            val transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.fl_container, fragment,"HomeXPopupDialog").commit()
        }
    }

    override fun doShowAnimation() {
        super.doShowAnimation()
        if(!isLoadFragment){
            isLoadFragment = true
            FlowBus.with<Boolean>(EventKey.LOAD_FRAGMENT).post(lifecycleScope,true)
        }
    }

    private val fragmentManager
        get() = run {
            when (context) {
                is FragmentActivity -> (context as FragmentActivity).supportFragmentManager
                is ContextWrapper -> (((context as ContextWrapper).baseContext) as Fragment).childFragmentManager
                else -> throw IllegalStateException("illegal context:$context ,type:${context.javaClass.simpleName}")
            }
        }

    override fun onDismiss() {
        super.onDismiss()
        Log.d(TAG, "onDismiss")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        /*val f = fragmentManager.findFragmentById(R.id.fl_container) ?: return
        fragmentManager.beginTransaction().remove(f).commit()*/
    }

    override fun getImplLayoutId(): Int {
        return R.layout.dialog_home_xpopup_container
    }

}
