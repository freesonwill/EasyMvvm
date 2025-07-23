package com.walisport.module.search.ui.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.data.remote.ApiFailedState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.ui.view.ClearableEditText
import arch.cayenne.lib.common.utils.ext.NavResultExt.sendResult
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.lib.skin.res.SkinnableResourceManager.getDrawable
import arch.cayenne.lib.skin.widget.SkinnableImageView
import com.walisport.module.search.R
import com.walisport.module.search.data.constants.SearchNavigationEvent
import com.walisport.module.search.data.constants.SearchTypeEnum
import com.walisport.module.search.databinding.FragmentSearchBinding
import com.walisport.module.search.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import arch.cayenne.lib.common.R as RC

/**
 * @author: caomei
 * @date: 2025/4/24 16:31
 * @description:搜索
 */
class SearchFragment : BaseFragment<SearchViewModel, FragmentSearchBinding>() {
    override val vbClass: KClass<FragmentSearchBinding>
        get() = FragmentSearchBinding::class
    override val vmClass: KClass<SearchViewModel>
        get() = SearchViewModel::class

    private val recommendListFragment by lazy {
        SearchRecommendListFragment().apply {
            onClickListener = { word ->
                dismiss()
                mViewModel.setNavigationEvent(SearchNavigationEvent.ToSearchResultBase(word))
                mViewModel.setSearchKeyWord(word)
                addSearchRecord(word)

            }
            onDismissListener = {
                hideKeyboard(requireContext(), getSearchEditText())
            }
        }
    }

    private val titleBarHintStr: String
        get() =
            SkinnableResourceManager.getString(
                requireContext(),
                R.string.please_input_content,
                mViewModel.getCurrentLanguage()
            )

    private var canSearch: Boolean = true

    private val apiFailedHandler: (ApiFailedState?) -> Unit = { error ->
        error?.let{ showToast(error.msg) }
    }

    companion object {
        const val SEARCH_KEY = "searchKey"
    }

    override fun onStart() {
        mViewModel.notifyStatusBarUpdate()
        super.onStart()
    }

    override fun initView(savedInstanceState: Bundle?) {
        setTitleBar()
        setRecommend()
        setBackPressHandler()
    }

    override fun initListener() = Unit

    override fun createObserver() {
        with(mViewModel) {
            launch(Lifecycle.State.STARTED) {
                launch {
                    currentLanguage.collect {
                        setTitleBar()
                    }
                }
                launch {
                    navigationEvent.collect { event ->
                        doNavigate(event)
                    }
                }
                launch {
                    searchKeyWord.collect { key ->
                        if (key.isNotEmpty()) {
                            updateSearchText(key)
                            setNavigationEvent(SearchNavigationEvent.ToSearchResultBase(key))
                        }
                    }
                }
                launch {
                    resultBackgroundColor.collect { color ->
                        setResultBackground(
                            color != null,
                            color ?: R.color.search_result_default_gradient_start
                        )
                    }
                }
                launch {
                    statusBarUpdateEvent.collect {
                        updateStatusSearchBar()
                    }
                }
            }
        }
    }

    private fun setTitleBar() {
        with(mBinding) {
            with(mViewModel) {
                //设置标题
                titleBar.loadSearchTitleBar(
                    hint = titleBarHintStr,
                    afterTextChanged = { text, binding ->
                        if (!canSearch) return@loadSearchTitleBar

                        val count = text?.length ?: 0
                        // 避免點擊清空搜尋時失去焦點且收起鍵盤
                        if (count == 0) {
                            showKeyboard(requireContext(), getSearchEditText())
                        }

                        // 搜索自动补充词汇
                        if(recommendListFragment.onClickListener == null) {
                            recommendListFragment.onClickListener = { recommendWord ->
                                updateSearchText(recommendWord) {
                                    backToSearchMainFragment()
                                    setNavigationEvent(SearchNavigationEvent.ToSearchResultBase(recommendWord))
                                    recommendListFragment.dismiss()
                                }
                            }
                        }
                        with(text?.toString()) {
                            recommendListFragment.updateKeyword(this)
                            getSearchRecommendList(this, apiFailedHandler)
                        }
                        updateSearchBtnColor()
                    },
                    onSearch = { content, _ ->
                        closeDatePicker()
                        if (TextUtils.isEmpty(content)) {
                            showToast(titleBarHintStr)
                            return@loadSearchTitleBar
                        }
                        recommendListFragment.dismiss()
                        addSearchRecord(content)
                        backToSearchMainFragment()
                        setNavigationEvent(SearchNavigationEvent.ToSearchResultBase(content))
                        hideKeyboard(requireContext(), getSearchEditText())
                    },
                    onBack = {
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                )

                getTitleBarBackIcon().apply {
                    post {
                        setImageDrawable(
                            getDrawable(requireContext(), R.drawable.ic_search_left_arrow)
                        )
                    }
                }

                getSearchEditText().apply {
                    setOnFocusChangeListener { _, isFocused ->
                        updateSearchBtnColor()
                        if (isFocused) {
                            closeDatePicker()
                            if (text?.isNotEmpty() == true) {
                                getSearchRecommendList(text.toString(), apiFailedHandler)
                            }
                        }
                    }
                    setOnClickListener {
                        if (text?.isNotEmpty() == true) {
                            getSearchRecommendList(text?.toString(), apiFailedHandler)
                        }
                    }
                }

                getSearchBtn().apply {
                    TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                        this,
                        10,
                        15,
                        1,
                        TypedValue.COMPLEX_UNIT_SP
                    )
                }
            }
        }
    }

    private fun setRecommend() {
        childFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container_recommend,
                recommendListFragment,
                SearchRecommendListFragment.TAG
            )
            .commit()
    }

    fun addSearchRecord(word: String) {
        if (word.isNotEmpty()) {
            mViewModel.addOneRecord(word)
            notifyUpdateRecordList(word)
        }
    }

    private fun setBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            closeDatePicker()

            val navController = mBinding.fragmentContainer.findNavController()
            val backStackId = navController.previousBackStackEntry?.destination?.id

            when (backStackId) {
                R.id.searchResultBaseFragment -> {
                    navController.popBackStack(R.id.searchResultBaseFragment, true)
                }
                else -> {
                    if (!navController.popBackStack()) {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        }
    }

    private fun closeDatePicker() {
        if (mViewModel.isDatePickerOpen()) {
            mViewModel.setIsDatePickerOpen(false)
        }
    }

    private fun getSearchBar(): LinearLayout {
        return mBinding.titleBar.findViewById(RC.id.ll_search_bar)
    }

    private fun getSearchBtn(): TextView {
        return mBinding.titleBar.findViewById(RC.id.tv_search_text)
    }

    private fun getSearchEditText(): ClearableEditText {
        return mBinding.titleBar.findViewById(RC.id.ce_search)
    }

    private fun getTitleBarBackIcon(): SkinnableImageView {
        return mBinding.titleBar.findViewById(RC.id.iv_back)
    }

    private fun updateSearchText(word: String, afterChange: (() -> Unit)? = null) {
        canSearch = false
        getSearchEditText()
            .apply {
                setText(word)
                setSelection(word.length)
            }
        afterChange?.invoke()
        canSearch = true
    }

    private fun setResultBackground(isShow: Boolean, color: Int? = null) {
        mBinding.clRoot.apply {
            val duration = 100
            if (isShow) {
                GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(
                        color ?: ContextCompat.getColor(context, R.color.search_result_default_gradient_start),
                        Color.BLACK
                    )
                ).let { newDrawable ->
                    background = TransitionDrawable(
                        arrayOf(background, newDrawable)
                    ).apply {
                        startTransition(duration)
                    }
                }
            } else {
                SkinnableResourceManager
                    .getColor(context, R.color.search_main_bg)
                    .toDrawable()
                    .let { newDrawable ->
                        background = TransitionDrawable(
                            arrayOf(background, newDrawable)
                        ).apply {
                            startTransition(duration)
                        }
                    }
            }
        }
    }

    private fun updateStatusSearchBar() {
        updateStatusTitleBar()
        updateTitleBarBackIcon()
        updateSearchTextColor()
        updateSearchBtnColor()
        updateSearchBarBackground()
    }

    private fun isDirectMatch(): Boolean {
        return findChildNavController(mBinding.fragmentContainer.id)
            .currentDestination?.id == R.id.searchResultDirectMatchFragment
    }

    private fun updateStatusTitleBar() {
        with(mBinding) {
            with(SkinnableResourceManager) {
                setStatusBar(
                    StatusBarConfig.apply {
                        statusBarType = StatusBarMode.DRAW_BEHIND()
                        statusBarColor = android.R.color.transparent
                        statusBarDarkFont =
                            if(isDirectMatch()) false
                            else getSkinName().lowercase().startsWith("white")
                    }, clRoot
                )
            }
        }
    }

    private fun updateTitleBarBackIcon() {
        getTitleBarBackIcon().apply {
            post {
                setImageDrawable(
                    if(isDirectMatch()) ContextCompat.getDrawable(requireContext(), R.drawable.ic_search_left_arrow)
                    else getDrawable(requireContext(), R.drawable.ic_search_left_arrow)
                )
            }
        }
    }

    private fun updateSearchTextColor() {
        getSearchEditText().setTextColor(
            if (isDirectMatch()) Color.WHITE
            else SkinnableResourceManager.getColor(
                requireContext(),
                RC.color.search_text_for_search_bar
            )
        )
    }

    private fun updateSearchBtnColor() {
        val isHighLight =
            getSearchEditText().hasFocus() && getSearchEditText().text?.isNotBlank() == true

        getSearchBtn().setTextColor(
            SkinnableResourceManager.getColor(
                requireContext(),
                when {
                    isHighLight -> RC.color.search_btn_highlight
                    isDirectMatch() -> RC.color.search_btn_in_direct_match
                    else -> RC.color.search_btn_normal
                }
            )
        )
    }

    private fun updateSearchBarBackground() {
        getSearchBar().backgroundTintList =
            SkinnableResourceManager.getColorStateList(
                requireContext(),
                if (isDirectMatch()) RC.color.search_bg_in_direct_match
                else RC.color.search_bg
            )
    }

    private fun hideKeyboard(context: Context?, editText: EditText) {
        val im = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(editText.windowToken, 0)
        editText.clearFocus()
    }

    private fun showKeyboard(context: Context?, editText: EditText) {
        editText.requestFocus()
        val im = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun backToSearchMainFragment() {
        val navController = findChildNavController(mBinding.fragmentContainer.id)
        navController.popBackStack(R.id.searchMainFragment, false)
    }

    private fun doNavigate(event: SearchNavigationEvent) {
        val navController = findChildNavController(mBinding.fragmentContainer.id)
        val currentId = navController.currentDestination?.id ?: return

        when (event) {
            is SearchNavigationEvent.ToSearchResultBase -> {
                mapOf(
                    R.id.searchMainFragment to R.id.action_searchMainFragment_to_searchResultBaseFragment,
                    R.id.searchResultListFragment to R.id.action_searchResultListFragment_to_searchResultBaseFragment,
                    R.id.searchResultDirectMatchFragment to R.id.action_searchResultDirectMatchFragment_to_searchResultBaseFragment
                )[currentId]?.let { actionId ->
                    sendResult(
                        key = SEARCH_KEY,
                        value = event.searchKey,
                        destinationId = currentId,
                        navController = navController
                    )
                    navController.navigate(actionId)
                }
            }
            is SearchNavigationEvent.ToSearchDirectMatch -> {
                if (currentId == R.id.searchResultBaseFragment) {
                    val action =
                        SearchResultBaseFragmentDirections
                            .actionSearchResultBaseFragmentToSearchResultDirectMatchFragment(
                                event.data, event.keyword, event.id, event.type ?: SearchTypeEnum.UNKNOWN
                            )
                    navController.navigate(action)
                } else if (currentId == R.id.searchResultListFragment) {
                    val action =
                        SearchResultListFragmentDirections
                            .actionSearchResultListFragmentToSearchResultDirectMatchFragment(
                                event.data, event.keyword, event.id, event.type ?: SearchTypeEnum.UNKNOWN
                            )
                    navController.navigate(action)
                }
            }
            is SearchNavigationEvent.ToSearchList -> {
                if (currentId == R.id.searchResultBaseFragment) {
                    val action = SearchResultBaseFragmentDirections
                        .actionSearchResultBaseFragmentToSearchResultListFragment(event.data)
                    navController.navigate(action)
                }
            }

            is SearchNavigationEvent.ToLiveFragment -> {
                navigate(event.deepLink.toUri())
            }
        }
    }

    private fun notifyUpdateRecordList(key: String) {
        childFragmentManager.fragments
            .filterIsInstance<SearchMainFragment>()
            .forEach { fragment ->
                (fragment as? SearchMainFragment)?.notifyUpdateRecordList(key)
            }
    }
}