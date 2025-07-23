package com.walisport.module.search.ui.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.data.remote.ApiFailedState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.ui.view.ClearableEditText
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.lib.skin.res.SkinnableResourceManager.getDrawable
import arch.cayenne.lib.skin.widget.SkinnableImageView
import com.walisport.module.search.R
import com.walisport.module.search.data.constants.SearchNavigationEvent
import com.walisport.module.search.databinding.FragmentSearchBaseBinding
import com.walisport.module.search.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

abstract class SearchBaseFragment<VM : BaseViewModel, CVB : ViewBinding>: BaseFragment<VM, FragmentSearchBaseBinding>() {
    override val vbClass: KClass<FragmentSearchBaseBinding>
        get() = FragmentSearchBaseBinding::class

    protected lateinit var contentBinding: CVB
    abstract val contentLayoutId: Int

    private val sharedViewModel: SearchViewModel by sharedViewModel<SearchViewModel, SearchFragment>()

    private val apiFailedHandler: (ApiFailedState?) -> Unit = { error ->
        error?.let{ showToast(error.msg) }
    }

    private val recommendListFragment by lazy {
        SearchRecommendListFragment().apply {
            onClickListener = { word ->
                dismiss()
                sharedViewModel.setNavigationEvent(SearchNavigationEvent.ToSearchResultBase(word))
                sharedViewModel.setSearchKeyWord(word)
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
                sharedViewModel.getCurrentLanguage()
            )

    private var canSearch: Boolean = true

    @CallSuper
    override fun initView(savedInstanceState: Bundle?) {
        inflateContentLayout()
        setTitleBar()
        setRecommend()
        setBackPressHandler()
    }

    override fun initListener() = Unit

    @CallSuper
    override fun createObserver() {
        with(sharedViewModel) {
            launch(Lifecycle.State.STARTED) {
                launch {
                    currentLanguage.collect {
                        setTitleBar()
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
                    statusBarUpdateEvent.collect {
                        updateStatusSearchBar()
                    }
                }
            }
        }
    }

    @CallSuper
    open fun setBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            closeDatePicker()

            isEnabled = false
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onStart() {
        sharedViewModel.notifyStatusBarUpdate()
        super.onStart()
    }

    private fun inflateContentLayout() {
        mBinding.viewStubContent.layoutResource = contentLayoutId
        val inflatedView = mBinding.viewStubContent.inflate()
        contentBinding = (DataBindingUtil.bind(inflatedView) as CVB?)!!
    }

    private fun addSearchRecord(word: String) {
        if (word.isNotEmpty()) {
            sharedViewModel.addOneRecord(word)
//            notifyUpdateRecordList(word)
        }
    }

    private fun setTitleBar() {
        with(mBinding) {
            with(sharedViewModel) {
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

    private fun updateSearchBtnColor() {
        val isHighLight =
            getSearchEditText().hasFocus() && getSearchEditText().text?.isNotBlank() == true

        getSearchBtn().setTextColor(
            SkinnableResourceManager.getColor(
                requireContext(),
                when {
                    isHighLight -> arch.cayenne.lib.common.R.color.search_btn_highlight
                    isDirectMatch() -> arch.cayenne.lib.common.R.color.search_btn_in_direct_match
                    else -> arch.cayenne.lib.common.R.color.search_btn_normal
                }
            )
        )
    }

    private fun updateSearchBarBackground() {
        getSearchBar().backgroundTintList =
            SkinnableResourceManager.getColorStateList(
                requireContext(),
                if (isDirectMatch()) arch.cayenne.lib.common.R.color.search_bg_in_direct_match
                else arch.cayenne.lib.common.R.color.search_bg
            )
    }

    private fun updateStatusSearchBar() {
        updateStatusTitleBar()
        updateTitleBarBackIcon()
        updateSearchTextColor()
        updateSearchBtnColor()
        updateSearchBarBackground()
    }

    private fun isDirectMatch(): Boolean {
        return findNavController().currentDestination?.id == R.id.searchResultDirectMatchFragment
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
                arch.cayenne.lib.common.R.color.search_text_for_search_bar
            )
        )
    }

    private fun closeDatePicker() {
        if (sharedViewModel.isDatePickerOpen()) {
            sharedViewModel.setIsDatePickerOpen(false)
        }
    }

    private fun getSearchBar(): LinearLayout {
        return mBinding.titleBar.findViewById(arch.cayenne.lib.common.R.id.ll_search_bar)
    }

    private fun getSearchBtn(): TextView {
        return mBinding.titleBar.findViewById(arch.cayenne.lib.common.R.id.tv_search_text)
    }

    private fun getSearchEditText(): ClearableEditText {
        return mBinding.titleBar.findViewById(arch.cayenne.lib.common.R.id.ce_search)
    }

    private fun getTitleBarBackIcon(): SkinnableImageView {
        return mBinding.titleBar.findViewById(arch.cayenne.lib.common.R.id.iv_back)
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
        findNavController().popBackStack(R.id.searchMainFragment, false)
    }

    companion object {
        const val SEARCH_KEY = "searchKey"
    }
}