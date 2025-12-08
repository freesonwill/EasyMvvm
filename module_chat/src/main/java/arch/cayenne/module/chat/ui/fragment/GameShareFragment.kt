package arch.cayenne.module.chat.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.module.chat.databinding.FragmentGameShareLayoutBinding
import arch.cayenne.module.chat.ui.viewmodel.BetShareViewModel
import kotlin.reflect.KClass

/**
 * @author: wenxi
 * @date: 8/12/25 19:50
 * @description:游戏注单分享
 */
class GameShareFragment:BaseFragment<BetShareViewModel,FragmentGameShareLayoutBinding>() {
    override val vbClass: KClass<FragmentGameShareLayoutBinding>
        get() = FragmentGameShareLayoutBinding::class
    override val vmClass: KClass<BetShareViewModel>
        get() = BetShareViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        "GameShareFragment ${mViewModel}".logd("aaa")
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }
}