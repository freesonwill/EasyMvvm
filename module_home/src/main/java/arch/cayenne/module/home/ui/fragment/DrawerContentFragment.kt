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
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.account.data.constants.KeyConfig
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.FragmentDrawerContentBinding
import arch.cayenne.module.home.ui.viewmodel.DrawerContentViewModel
import arch.cayenne.module.home.utils.DateUtils
import com.walisport.module.message.data.NotificationBean
import kotlin.reflect.KClass

class DrawerContentFragment : BaseFragment<DrawerContentViewModel, FragmentDrawerContentBinding>() {
    override val vbClass: KClass<FragmentDrawerContentBinding> = FragmentDrawerContentBinding::class
    override val vmClass: KClass<DrawerContentViewModel> = DrawerContentViewModel::class
    private var onFunctionClick: (() -> Unit)? = null

    //note:login入口開關
    private val showBtnLogin = false

    companion object {
        const val TAG = "DrawerContentFragment"
    }

    override fun initView(savedInstanceState: Bundle?) {
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        setStatusBar(StatusBarConfig, mBinding.root)
        mBinding.btnLogin.visibility = if (showBtnLogin) {
            View.VISIBLE
        } else {
            View.GONE
        }

        //更改导航栏样式调整底部偏移
        ViewCompat.setOnApplyWindowInsetsListener(requireActivity().window.decorView) { v, insets ->
            val navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            val navBarHeight = ViewUtils.getNavigationBarHeight(requireContext())
            //"导航栏 bottom = ${navInsets.bottom},navBarHeight:$navBarHeight".logd(TAG)
            mBinding.clDrawerBottom.layoutParams.let { lp ->
                lp as MarginLayoutParams
                lp.bottomMargin = if (navInsets.bottom == 0) navBarHeight else 0
            }
            ViewCompat.setOnApplyWindowInsetsListener(requireActivity().window.decorView, null)
            insets
        }
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
            mBinding.viewRipperTop.clickNoRepeat {
                navigatePage(arch.cayenne.lib.res.R.string.nav_module_message_fragment.deeplink())
            }
            mBinding.viewRipper.clickNoRepeat {
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

            btnLogin.clickNoRepeat {
                navigatePage(arch.cayenne.lib.res.R.string.nav_module_login_fragment.deeplink("userId" to "abcd"))
            }
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
        with(mViewModel) {
            launch {
                selectedSkinType.observe(viewLifecycleOwner) {
                    refreshDefaultNickName()
                    mViewModel.getMessageList()
                }
            }
            notificationBean.observeEvent(viewLifecycleOwner, this@DrawerContentFragment) { list ->
                setNotificationItem(list)
            }
        }
    }

    private fun setNotificationItem(list: List<NotificationBean>) {
        if (list.isEmpty()) {
            mBinding.itemNotification1.visibility = View.GONE
            mBinding.itemNotification2.visibility = View.GONE
            mBinding.viewRipperTop.visibility = View.GONE
            mBinding.viewRipper.visibility = View.VISIBLE
            return
        }
        mBinding.viewRipperTop.visibility = View.VISIBLE
        mBinding.viewRipper.visibility = View.GONE
        if (list.size == 1) {
            val item = list[0]
            mBinding.itemNotification2.visibility = View.VISIBLE
            mBinding.itemNotification1.visibility = View.GONE
            mBinding.tvMessage2.text = item.title
            mBinding.tvTime2.text = DateUtils.getMessageTime(item.createTime)
            //status = 1未读 2已读 3删除
            mBinding.ivRedPoint2.visibility = if (item.state == 2) View.INVISIBLE else View.VISIBLE
        } else {
            val item1 = list[0]
            val item2 = list[1]
            mBinding.itemNotification2.visibility = View.VISIBLE
            mBinding.itemNotification1.visibility = View.VISIBLE
            mBinding.tvMessage1.text = item1.title
            mBinding.tvTime1.text = DateUtils.getMessageTime(item1.createTime)
            mBinding.ivRedPoint1.visibility = if (item1.state == 2) View.INVISIBLE else View.VISIBLE
            mBinding.tvMessage2.text = item2.title
            mBinding.tvTime2.text = DateUtils.getMessageTime(item2.createTime)
            mBinding.ivRedPoint2.visibility = if (item2.state == 2) View.INVISIBLE else View.VISIBLE
        }
    }

    private fun Int.getSkinnableResourceId(): Int {
        return SkinnableResourceManager.getTargetResourceId(requireContext(), this)
    }

    private fun refreshDefaultNickName() {
        val defaultResId = mViewModel.getDefaultResId()
        with(mBinding) {
            if (defaultResId == -1) {
                ivIconNickname.setImageResource(R.drawable.ic_drawer_nickname)
            } else {
                ivIconNickname.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        defaultResId,
                        null
                    )
                )
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