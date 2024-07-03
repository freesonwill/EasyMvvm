package com.cn.game.sdk2.ui


import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.DialogHomeXpopupContainerBinding
import com.cn.game.sdk2.ui.fast3.Fast3HelpFragment
import com.cn.game.sdk2.ui.fast3.Fast3MainFragment
import com.lxj.xpopup.core.BottomPopupView

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/13 18:18
 **/
class HomeXPopupDialog(context: Context, private val fragment: Fragment, private var miniGameId: Int) : BottomPopupView(context) {

    companion object {
        const val TAG = "HomeXPopupDialog"
    }

    var binding: DialogHomeXpopupContainerBinding? = null

    override fun onCreate() {
        super.onCreate()
        binding = DialogHomeXpopupContainerBinding.bind(popupImplView)
        val transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fl_container, fragment,"HomeXPopupDialog").commit()
        Log.d(TAG, "onCreate")
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
        val f = fragmentManager.findFragmentById(R.id.fl_container) ?: return
        fragmentManager.beginTransaction().remove(f).commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    override fun getImplLayoutId(): Int {
        return R.layout.dialog_home_xpopup_container
    }
}
