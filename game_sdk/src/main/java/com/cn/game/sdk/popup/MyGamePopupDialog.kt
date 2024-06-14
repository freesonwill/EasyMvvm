package com.cn.game.sdk.popup

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cn.game.sdk.R
import com.cn.game.sdk.databinding.PopupMyGameBinding
import com.lxj.xpopup.core.BottomPopupView

class MyGamePopupDialog(context: Context) : BottomPopupView(context) {

    override fun getImplLayoutId(): Int {
        return R.layout.popup_my_game
    }

    private lateinit var binding: PopupMyGameBinding


    override fun onCreate() {
        super.onCreate()
        binding = PopupMyGameBinding.bind(popupImplView)
        showFragment(MyGameFragment.newInstance(this),"MyGameFragment")
    }

    private fun showFragment(fragment: Fragment?, tag: String) {
        if (fragment === null) {
            return
        }
        val activity  = context as AppCompatActivity
        val transaction = activity.supportFragmentManager.beginTransaction()
        if (!fragment.isAdded) {
            transaction.add(R.id.fragment, fragment, tag)
        } else {
            transaction.show(fragment)
        }
        transaction.commit()
    }
}