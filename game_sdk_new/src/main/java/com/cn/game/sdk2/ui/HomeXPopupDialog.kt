package com.cn.game.sdk2.ui


import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.DialogHomeXpopupContainerBinding
import com.cn.game.sdk2.ui.fast3.Fast3MainFragment
import com.lxj.xpopup.core.BottomPopupView

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/13 18:18
 **/
open class HomeXPopupDialog(context: Context, private var miniGameId: Int) : BottomPopupView(context) {
    var binding:DialogHomeXpopupContainerBinding ?= null

    override fun onCreate() {
        super.onCreate()
        binding = DialogHomeXpopupContainerBinding.bind(popupImplView)
        val activity = context as FragmentActivity
        val transaction = activity!!.supportFragmentManager.beginTransaction();
        val fragment  = Fast3MainFragment();
        transaction.add(R.id.fl_container,fragment).commit();

    }

    override fun getImplLayoutId(): Int {
        return R.layout.dialog_home_xpopup_container
    }


}
