package arch.cayenne.module.home.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.account.data.constants.KeyConfig
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.FragmentDrawerContentBinding
import arch.cayenne.module.home.ui.viewmodel.DrawerContentViewModel
import arch.cayenne.module.home.utils.DateUtils
import kotlin.reflect.KClass

class DrawerContentFragment : BaseFragment<DrawerContentViewModel, FragmentDrawerContentBinding>() {
    override val vbClass: KClass<FragmentDrawerContentBinding> = FragmentDrawerContentBinding::class
    override val vmClass: KClass<DrawerContentViewModel> = DrawerContentViewModel::class
    private var onFunctionClick: (() -> Unit)? = null
    companion object {
        const val TAG = "DrawerContentFragment"
    }
    override fun initView(savedInstanceState: Bundle?) {
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        setStatusBar(StatusBarConfig,mBinding.root)

        //更改导航栏样式调整底部偏移
        ViewCompat.setOnApplyWindowInsetsListener(requireActivity().window.decorView) { v, insets ->
            val navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            val navBarHeight = ViewUtils.getNavigationBarHeight(requireContext())
            //"导航栏 bottom = ${navInsets.bottom},navBarHeight:$navBarHeight".logd(TAG)
            mBinding.clDrawerBottom.layoutParams.let{ lp-> lp as MarginLayoutParams
                lp.bottomMargin = if(navInsets.bottom == 0) navBarHeight else 0
            }
            ViewCompat.setOnApplyWindowInsetsListener(requireActivity().window.decorView, null)
            insets
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.getMessageList()
    }

    override fun initListener() {
        with(mBinding) {
            clDrawerNickname.clickNoRepeat {
                requireActivity().supportFragmentManager.setFragmentResultListener(
                    KeyConfig.VALUE_NICKNAME_RESULT,
                    viewLifecycleOwner
                ) { _, bundle ->
                    requireActivity().supportFragmentManager.clearFragmentResultListener(KeyConfig.VALUE_NICKNAME_RESULT)
                    val isSaveSuccess = bundle.getBoolean(KeyConfig.VALUE_SAVE_NICKNAME, false)
                    if (isSaveSuccess) {
                        refreshDefaultNickName()
                    }
                }
                navigatePage(arch.cayenne.lib.res.R.string.nav_module_personal_info_fragment.deeplink())
            }
            clNotificationHeader.clickNoRepeat {
                navigatePage(arch.cayenne.lib.res.R.string.nav_module_message_fragment.deeplink())
            }
            itemNotification1.clickNoRepeat {
                navigatePage(arch.cayenne.lib.res.R.string.nav_module_message_fragment.deeplink())
            }
            itemNotification2.clickNoRepeat {
                navigatePage(arch.cayenne.lib.res.R.string.nav_module_message_fragment.deeplink())
            }
            llRecharge.clickNoRepeat {
                navigatePage(arch.cayenne.lib.res.R.string.nav_module_topup_fragment.deeplink())
            }
            llRecharge.addScaleOnTouchAnimation()
            llDrawerTutorial.clickNoRepeat {
                navigatePage(Uri.parse("walisport://module_handicap/HandicapFragment?homeId=${R.id.newHomeFragment}"))
            }
            llDrawerTutorial.addScaleOnTouchAnimation()
            llDrawerSetting.clickNoRepeat {
                navigatePage(arch.cayenne.lib.res.R.string.nav_module_setting_fragment.deeplink())
            }
            llDrawerFeedback.clickNoRepeat {
                navigatePage(arch.cayenne.lib.res.R.string.nav_module_feedback_fragment.deeplink())
            }
            llBetSlip.clickNoRepeat {
                onFunctionClick?.invoke()
                navigate(NewHomeFragmentDirections.actionNewHomeFragmentToHomeBetSlipFragment())
            }
            llBetSlip.addScaleOnTouchAnimation()
        }
    }

    private fun navigatePage(uri: Uri) {
        onFunctionClick?.invoke()
        navigate(uri)
    }
    fun setOnFunctionClickListener(listener: () -> Unit) {
        onFunctionClick = listener
    }

    override suspend fun createObserver() {

            with (mViewModel) {
                launch {
                    selectedSkinType.observe(viewLifecycleOwner) {
                        refreshDefaultNickName()
                    }
                }
                notificationBean.observeEvent(viewLifecycleOwner,this@DrawerContentFragment) { list ->
                    if (list.isEmpty()) return@observeEvent
                    val untilIndex = if (list.size < 2) list.size else 2
                    for(i in 0 until untilIndex) {
                        val item = list[i]
                        with (mBinding) {
                            if (i == 0) {
                                itemNotification1.visibility = View.VISIBLE
                                itemNotification2.visibility = View.GONE
                                tvMessage1.text = item.title
                                //status = 1未读 2已读 3删除
                                ivMessage1Dot.visibility = if (item.state == 2) View.INVISIBLE else View.VISIBLE
                                tvTime1.text = DateUtils.getMessageTime(item.createTime)
                            } else {
                                itemNotification2.visibility = View.VISIBLE
                                tvMessage2.text = item.title
                                ivMessage2Dot.visibility = if (item.state == 2) View.INVISIBLE else View.VISIBLE
                                tvTime2.text = DateUtils.getMessageTime(item.createTime)
                            }
                        }
                    }
                }
        }

    }
    private fun refreshDefaultNickName() {
        val defaultResId = mViewModel.getDefaultResId()
        with(mBinding) {
            if (defaultResId == -1) {
                ivIconNickname.setImageResource(R.drawable.ic_drawer_nickname)
            } else {
                ivIconNickname.setImageDrawable(ResourcesCompat.getDrawable(resources, defaultResId, null))
            }
            val defaultNickName = mViewModel.getDefaultNickName()
            if (defaultNickName.isNotEmpty()) {
                tvTitleNickname.text = defaultNickName
            } else {
                tvTitleNickname.text = getString(R.string.drawer_nickname_title)
            }
        }
    }
}