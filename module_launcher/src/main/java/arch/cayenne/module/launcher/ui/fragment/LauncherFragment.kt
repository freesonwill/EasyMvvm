package arch.cayenne.module.launcher.ui.fragment

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.ui.viewmodel.BalanceViewModel
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.launcher.databinding.FragmentLauncherBinding
import arch.cayenne.module.launcher.ui.viewmodel.LauncherViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue
import kotlin.reflect.KClass
import org.koin.androidx.viewmodel.ext.android.viewModel

class LauncherFragment: BaseFragment<LauncherViewModel, FragmentLauncherBinding>() {
    override val vbClass: KClass<FragmentLauncherBinding> = FragmentLauncherBinding::class
    override val vmClass: KClass<LauncherViewModel> = LauncherViewModel::class

    private val balanceViewModel: BalanceViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {

        with(mBinding) {
            viewBalance.setBalanceViewModel(balanceViewModel, viewLifecycleOwner)

            // 模擬幣別設置是否顯示
            val isShowCurrency = false
            spaceH50?.visibility = if(isShowCurrency) View.VISIBLE else View.GONE
            ivExit.visibility = if (isShowCurrency) View.VISIBLE else View.GONE
            ivSwitch.visibility = if (isShowCurrency) View.VISIBLE else View.GONE
            ivFullscreen.visibility = if (isShowCurrency) View.VISIBLE else View.GONE
            ivBack.visibility = if(isShowCurrency && !isLandscape()) View.GONE else View.VISIBLE
            ivDoubleCircle.visibility = if(isShowCurrency && !isLandscape()) View.GONE else View.VISIBLE
            ivToggle.visibility = if(isShowCurrency) View.GONE else View.VISIBLE
            viewBalance.visibility = if(isShowCurrency) View.VISIBLE else View.GONE

            // 模擬logo是否顯示
            val isShowLogo = false
            ivGameLogo.visibility = if(isShowLogo) View.VISIBLE else View.GONE
            tvGameName.visibility = if(isShowLogo) View.VISIBLE else View.GONE

            // 模擬hint是否顯示
            val isShowHint = true
            tvHint.visibility = if(isShowHint) View.VISIBLE else View.GONE

            // 動態調整位置（設計稿並非置中，每種狀態位置不一樣，美術說先照設計稿做不修改成置中）
            if(isLandscape()) {
                (ivLoading.layoutParams as ConstraintLayout.LayoutParams).apply { topMargin = if(isShowLogo) 265.dp2px else 175.dp2px }
                (tvHint.layoutParams as ConstraintLayout.LayoutParams).apply { bottomMargin = if(isShowLogo) 175.dp2px else 33.dp2px }
            } else {
                (ivLoading.layoutParams as ConstraintLayout.LayoutParams).apply { topMargin = if(isShowLogo) 326.dp2px else 244.dp2px }
                (tvHint.layoutParams as ConstraintLayout.LayoutParams).apply { bottomMargin = if(isShowLogo) 203.dp2px else 30.5f.dp2px.toInt() }
            }

            // 初始化BalanceView
            viewBalance.init(childFragmentManager)
        }
    }

    override fun initListener() = Unit

    override suspend fun createObserver() = Unit

    override fun onStart() {
        super.onStart()
        StatusBarConfig.statusBarType = if(isLandscape()) StatusBarMode.FULLSCREEN else StatusBarMode.DRAW_BEHIND()
        setStatusBar(StatusBarConfig, mBinding.root)
    }

    private fun isLandscape(): Boolean {
        return requireActivity().requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
}