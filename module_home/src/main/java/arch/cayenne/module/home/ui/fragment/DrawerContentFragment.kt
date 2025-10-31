package arch.cayenne.module.home.ui.fragment

import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.ui.viewmodel.observeEvent
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.SportIntExt.getFormalMoney
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.account.data.constants.KeyConfig
import arch.cayenne.module.home.R
import arch.cayenne.module.home.data.model.CommonFeaturesBean
import arch.cayenne.module.home.databinding.FragmentDrawerContentBinding
import arch.cayenne.module.home.ui.adapter.DrawerFeaturesAdapter
import arch.cayenne.module.home.ui.viewmodel.DrawerContentViewModel
import arch.cayenne.module.home.utils.DateUtils
import com.walisport.module.message.data.NotificationBean
import kotlin.reflect.KClass

class DrawerContentFragment : BaseFragment<DrawerContentViewModel, FragmentDrawerContentBinding>() {
    override val vbClass: KClass<FragmentDrawerContentBinding> = FragmentDrawerContentBinding::class
    override val vmClass: KClass<DrawerContentViewModel> = DrawerContentViewModel::class
    private var onFunctionClick: (() -> Unit)? = null
    private val spanCount = 3
    //note:login入口開關
    private val showBtnLogin = false

    companion object {
        const val TAG = "DrawerContentFragment"
    }

    private val drawerFeaturesAdapter by lazy {
        DrawerFeaturesAdapter()
    }

    private val serviceFeaturesAdapter by lazy {
        DrawerFeaturesAdapter()
    }

    private val earningFeaturesAdapter by lazy {
        DrawerFeaturesAdapter()
    }

    override fun initView(savedInstanceState: Bundle?) {
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND()
        StatusBarConfig.statusBarDarkFont = false
        setStatusBar(StatusBarConfig, mBinding.root)

        initRvCommonFeatures()
        initRvServiceFeatures()
        initRvEarningFeatures()
    }

    private fun initRvCommonFeatures() {
        mBinding.rvCommonFeatures.apply {
            //某些机型上， RecyclerView存在过滚动效果。 禁用scroll, 禁用过滚动效果
            layoutManager = object : GridLayoutManager(requireContext(), spanCount) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }

                override fun canScrollVertically(): Boolean {
                    return false
                }
            }// 每行3个
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val layoutManager = parent.layoutManager as? GridLayoutManager ?: return
                    val position = parent.getChildAdapterPosition(view)
                    if (position == RecyclerView.NO_POSITION) return

                    val spanCount = layoutManager.spanCount
                    val spanSizeLookup = layoutManager.spanSizeLookup

                    // 獲取當前 Item 所在的「行索引」(group index)
                    // 這是 GridLayoutManager 判斷「行」的正確方式，不受 span size 影響
                    val spanGroupIndex = spanSizeLookup.getSpanGroupIndex(position, spanCount)

                    // 1. 處理 Top Margin：只在第一行 (spanGroupIndex == 0) 加上 topMargin
                    if (spanGroupIndex == 0) {
                        outRect.top = 12.dp2px
                    } else {
                        outRect.top = 20.dp2px // 其他行不加
                    }
                    if (spanGroupIndex == 2) {
                        outRect.bottom = 16.dp2px
                    }
                }
            })
            adapter = drawerFeaturesAdapter
            itemAnimator = null
        }

        var id = 0
        drawerFeaturesAdapter.submitList(
            listOf(
                CommonFeaturesBean(
                    id++, arch.cayenne.lib.common.R.drawable.ic_drawer_fund_details,
                    arch.cayenne.lib.common.R.string.drawer_fund_details
                ) {
                    navigatePage(arch.cayenne.lib.res.R.string.nav_module_fund_detail_fragment.deeplink())
                },
                CommonFeaturesBean(
                    id++, arch.cayenne.lib.common.R.drawable.ic_drawer_bet_record,
                    arch.cayenne.lib.common.R.string.drawer_bet_record
                ) {
                    showToast(arch.cayenne.lib.common.R.string.drawer_bet_record.getString())
                },
                CommonFeaturesBean(
                    id++, arch.cayenne.lib.common.R.drawable.ic_drawer_realtime_cashback,
                    arch.cayenne.lib.common.R.string.drawer_cash_back
                ) {
                    navigatePage(arch.cayenne.lib.res.R.string.nav_module_realtime_cashback_fragment.deeplink())
                },

                CommonFeaturesBean(
                    id++, arch.cayenne.lib.common.R.drawable.ic_drawer_recently_played,
                    arch.cayenne.lib.common.R.string.drawer_recently_played
                ) {
                    showToast(arch.cayenne.lib.common.R.string.drawer_recently_played.getString())
                },
                CommonFeaturesBean(
                    id++, arch.cayenne.lib.common.R.drawable.ic_drawer_game_collection,
                    arch.cayenne.lib.common.R.string.drawer_game_collections
                ) {
                    showToast(arch.cayenne.lib.common.R.string.drawer_game_collections.getString())
                },
                CommonFeaturesBean(
                    id++, arch.cayenne.lib.common.R.drawable.ic_drawer_match_collection,
                    arch.cayenne.lib.common.R.string.drawer_match_collections
                ) {
                    navigatePage(arch.cayenne.lib.res.R.string.nav_module_collectlist_fragment.deeplink())
                },

                CommonFeaturesBean(
                    id++, arch.cayenne.lib.common.R.drawable.ic_drawer_gift,
                    arch.cayenne.lib.common.R.string.drawer_gift
                ) {
                    showToast(arch.cayenne.lib.common.R.string.drawer_gift.getString())
                },
                CommonFeaturesBean(
                    id++, arch.cayenne.lib.common.R.drawable.ic_drawer_settings,
                    arch.cayenne.lib.common.R.string.drawer_settings
                ) {
                    navigatePage(arch.cayenne.lib.res.R.string.nav_module_setting_fragment.deeplink())
                },
            )
        )


    }

    /**
     * 初始化服务功能区域
     */
    private fun initRvServiceFeatures() {
        mBinding.rvServiceFeatures.apply {
            //某些机型上， RecyclerView存在过滚动效果。 禁用scroll, 禁用过滚动效果
            layoutManager = object : GridLayoutManager(requireContext(), spanCount) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }

                override fun canScrollVertically(): Boolean {
                    return false
                }
            }// 每行3个
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    if (position == RecyclerView.NO_POSITION) return
                    // 1. 處理 Top Margin：只在第一行 (spanGroupIndex == 0) 加上 topMargin
                    outRect.top = 12.dp2px
                    outRect.bottom = 16.dp2px
                }
            })
            adapter = serviceFeaturesAdapter
            itemAnimator = null
        }

        var id = 0
        serviceFeaturesAdapter.submitList(
            listOf(
                CommonFeaturesBean(
                    id++, arch.cayenne.lib.common.R.drawable.ic_drawer_help,
                    arch.cayenne.lib.common.R.string.drawer_help
                ) {
                    navigatePage(Uri.parse("walisport://module_handicap/HandicapFragment?homeId=${R.id.newHomeFragment}"))
                },
                CommonFeaturesBean(
                    id++, arch.cayenne.lib.common.R.drawable.ic_drawer_feedback,
                    arch.cayenne.lib.common.R.string.drawer_feedback
                ) {
                    navigatePage(arch.cayenne.lib.res.R.string.nav_module_feedback_fragment.deeplink())
                },
                CommonFeaturesBean(
                    id++, arch.cayenne.lib.common.R.drawable.ic_drawer_customer_service,
                    arch.cayenne.lib.common.R.string.drawer_customer_service
                ) {
                    showToast(arch.cayenne.lib.common.R.string.drawer_customer_service.getString())
                },
            )
        )


    }


    /**
     * 初始化赚钱功能区域
     */
    private fun initRvEarningFeatures() {
        mBinding.rvEarningFeatures.apply {
            //某些机型上， RecyclerView存在过滚动效果。 禁用scroll, 禁用过滚动效果
            layoutManager = object : GridLayoutManager(requireContext(), 3) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }

                override fun canScrollVertically(): Boolean {
                    return false
                }
            }// 每行3个
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    if (position == RecyclerView.NO_POSITION) return
                    // 1. 處理 Top Margin：只在第一行 (spanGroupIndex == 0) 加上 topMargin
                    outRect.top = 12.dp2px
                    outRect.bottom = 16.dp2px
                }
            })
            adapter = earningFeaturesAdapter
            itemAnimator = null
        }

        var id = 0
        earningFeaturesAdapter.submitList(
            listOf(
                CommonFeaturesBean(
                    id++, arch.cayenne.lib.common.R.drawable.ic_drawer_invite,
                    arch.cayenne.lib.common.R.string.drawer_invite,
                    true,
                    getString(arch.cayenne.lib.common.R.string.crazy_earn_text)
                ) {
                    navigatePage(arch.cayenne.lib.res.R.string.nav_module_invite_friends_fragment.deeplink())
                },
                CommonFeaturesBean(
                    id++, arch.cayenne.lib.common.R.drawable.ic_drawer_partner,
                    arch.cayenne.lib.common.R.string.drawer_partner
                ) {
                    navigatePage(arch.cayenne.lib.res.R.string.nav_module_partner_fragment.deeplink())
                },

                )
        )


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

            mBinding.llDrawerRecharge.addScaleOnTouchAnimation()
            mBinding.llDrawerRecharge.clickNoRepeat {
                navigatePage(arch.cayenne.lib.res.R.string.nav_module_topup_fragment.deeplink())
            }

            mBinding.llDrawerWithdraw.addScaleOnTouchAnimation()
            mBinding.llDrawerWithdraw.clickNoRepeat {
                navigatePage(arch.cayenne.lib.res.R.string.nav_module_withdraw_fragment.deeplink())
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
                }
            }
            notificationBean.observeEvent(viewLifecycleOwner, this@DrawerContentFragment) { list ->
                setNotificationItem(list)
            }
            currentBalanceChange.observe(viewLifecycleOwner) {
                mBinding.tvBalance.text =
                    getString(
                        R.string.balance_format,
                        CurrencySymbols.getSymbol(it?.currency ?: ""),
                        (it?.balance ?: 0L).getFormalMoney()
                    )
            }
        }
    }

    private fun setNotificationItem(list: List<NotificationBean>) {
        with(mBinding) {
            if (list.isEmpty()) {
                itemNotification1.visibility = View.GONE
                itemNotification2.visibility = View.GONE
                viewRipperTop.visibility = View.GONE
                viewRipper.visibility = View.VISIBLE
                newMessageTitle.visibility = View.INVISIBLE
                notificationBadge.visibility = View.INVISIBLE
                return
            }
            viewRipperTop.visibility = View.VISIBLE
            viewRipper.visibility = View.GONE
            newMessageTitle.visibility = View.VISIBLE
            notificationBadge.visibility = View.VISIBLE
            notificationBadge.setNotificationCount(list.size)
            if (list.size == 1) {
                val item = list[0]
                itemNotification2.visibility = View.VISIBLE
                itemNotification1.visibility = View.GONE
                tvMessage2.text = item.title
                tvTime2.text = DateUtils.getMessageTime(item.createTime)
            } else {
                val item1 = list[0]
                val item2 = list[1]
                itemNotification2.visibility = View.VISIBLE
                itemNotification1.visibility = View.VISIBLE
                tvMessage1.text = item1.title
                tvTime1.text = DateUtils.getMessageTime(item1.createTime)
                tvMessage2.text = item2.title
                tvTime2.text = DateUtils.getMessageTime(item2.createTime)
            }
        }
    }

    private fun Int.getSkinnableResourceId(): Int {
        return SkinnableResourceManager.getTargetResourceId(requireContext(), this)
    }

    private fun refreshDefaultNickName() {
        val defaultResId = mViewModel.getDefaultResId()
        with(mBinding) {
            if (defaultResId == -1) {
                ivIconNickname.setImageResource(arch.cayenne.lib.common.R.drawable.ic_avatar_default)
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

    override fun onStart() {
        super.onStart()
    }

}