package com.walisport.module.live.ui

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.ui.view.DynamicStateLayout.States
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.listenAtTop
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import com.blankj.utilcode.util.ToastUtils
import com.walisport.module.live.R
import com.walisport.module.live.databinding.FragmentLiveStandingsBinding
import com.walisport.module.live.ui.adapter.StandingsAdapter
import com.walisport.module.live.ui.viewmodel.LiveMainViewModel
import com.walisport.module.live.ui.viewmodel.LiveStandingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.reflect.KClass

/**
 * 积分榜
 */

class LiveStandingsFragment : BaseFragment<LiveStandingsViewModel, FragmentLiveStandingsBinding>() {

    private val mainViewModel: LiveMainViewModel by sharedViewModel<LiveMainViewModel, LiveMainFragment>()
    override val vbClass: KClass<FragmentLiveStandingsBinding> = FragmentLiveStandingsBinding::class
    override val vmClass: KClass<LiveStandingsViewModel> = LiveStandingsViewModel::class
    private var standsAdapter = StandingsAdapter()
    private var tournamentName = ""

    class StandingsItemDecoration(
        private val spacing: Int = 12.dp2px,         // 常规间距大小（像素）
        private val leftRight: Int = 8.dp2px,        // 左右边距（像素）
        private val bottomSpacing: Int = 20.dp2px,   // 最后一个 item 与底部的距离（像素）
    ) : ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) // item 位置
            val itemCount = parent.adapter?.itemCount ?: 0      // 总 item 数
            outRect.top = if (position == 0) spacing else spacing / 2
            outRect.bottom = if (position == itemCount - 1) bottomSpacing else spacing / 2
            outRect.left = leftRight
            outRect.right = leftRight
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.recyclerStandings.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = standsAdapter
            addItemDecoration(StandingsItemDecoration())
        }
        launch {
            initWebView()
        }
        // 恢复 WebView 状态
        savedInstanceState?.let {
            mBinding.webView.restoreState(it)
        }
        mBinding.webView.loadUrl("https://www.baidu.com/")
        // 使用 ViewTreeObserver 监听滚动
        val observer = mBinding.webView.viewTreeObserver
        observer.addOnScrollChangedListener {
            if (isResumed){
                if (mBinding.webView.scrollY == 0) {
                   // LogUtils.e("LiveStandingsFragment-WebView 已滑动到顶部")
                    mainViewModel.setSonVerticalScrollIsTop(true)
                } else {
                  //  LogUtils.e("LiveStandingsFragment-WebView 未在顶部，当前 scrollY: ${mBinding.webView.scrollY}")
                    mainViewModel.setSonVerticalScrollIsTop(false)
                }
            }
        }
    }
    @SuppressLint("SetJavaScriptEnabled")
    private suspend fun initWebView() {
        val webSettings = mBinding.webView.settings
        with(webSettings) {
            webSettings.javaScriptEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
                domStorageEnabled = true
                displayZoomControls = true
                databaseEnabled = true
                cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                blockNetworkImage = false
                setGeolocationEnabled(true)
                setGeolocationDatabasePath(
                    withContext(Dispatchers.IO){
                        requireActivity().applicationContext.getDir(
                            "database",
                            android.content.Context.MODE_PRIVATE
                        ).path
                    }
                )
                useWideViewPort = true
                loadWithOverviewMode = true
                defaultTextEncodingName = "UTF-8"
                allowContentAccess = true
                allowFileAccess = true
                allowFileAccessFromFileURLs = true
                allowUniversalAccessFromFileURLs = true
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
        }
    }
    override fun initListener() {
        // 监听 RecyclerView 是否滑动到第一条
        mBinding.recyclerStandings.listenAtTop { isAtTop ->
            if (isAtTop) {
                mainViewModel.setSonVerticalScrollIsTop(true)
            }else{
                mainViewModel.setSonVerticalScrollIsTop(false)
            }
        }
    }

    override fun onResume() {
        mBinding.webView.onResume()
        mBinding.webView.resumeTimers()
        if (standsAdapter.itemCount==0){
            mainViewModel.setSonVerticalScrollIsTop(true)
        }
        super.onResume()
    }

    override suspend fun createObserver() {
        launch(Lifecycle.State.RESUMED) {
            mainViewModel.apiStateListener.observe(viewLifecycleOwner) { state ->
                when (state) {
                    DataState.NetworkUnavailable -> {
                        mainViewModel.setSonVerticalScrollIsTop(true)
                            mBinding.mainLayout.setState(
                            States.NETWORK_ANOMALY(),
                            arch.cayenne.lib.common.R.string.error_net.getString()
                        )
                    }
                }
            }
            mViewModel.competitionTables.observe(viewLifecycleOwner) {
                if (it.isEmpty() && standsAdapter.itemCount == 0) {
                    mainViewModel.setSonVerticalScrollIsTop(true)
                    mBinding.mainLayout.setState(
                        States.DATA_EMPTY,
                        R.string.lineup_empty.getString()
                    )
                } else {
                    mainViewModel.setSonVerticalScrollIsTop(true)
                    mBinding.tvStandingsName.text = tournamentName
                    mBinding.mainLayout.setVisibilityGone()
                    standsAdapter.submitList(it)
                }
            }
            mainViewModel.mainMatch.observe(viewLifecycleOwner) {
                it?.let {
                    val leagueID = it.basicInfo.tournamentId
                    tournamentName = it.basicInfo.tournamentName
                  //  mViewModel.getCompetitionData(leagueID)
                }
            }
        }
    }

    override fun onPause() {
        mBinding.webView.onPause()
        mBinding.webView.pauseTimers()
        super.onPause()
    }

    override fun onDestroy() {
        mBinding.webView.destroy()
        super.onDestroy()
    }
}