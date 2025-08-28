package com.walisport.module.search.ui.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
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
import androidx.core.graphics.createBitmap
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.data.remote.ApiFailedState
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.getViewBind
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.ui.view.ClearableEditText
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.lib.skin.res.SkinnableResourceManager.getDrawable
import arch.cayenne.lib.skin.widget.SkinnableImageView
import com.walisport.module.search.R
import com.walisport.module.search.databinding.FragmentSearchBaseBinding
import com.walisport.module.search.ui.viewmodel.SearchBaseViewModel
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.reflect.KClass
import arch.cayenne.lib.common.R as RC

abstract class SearchBaseFragment<VM : BaseViewModel, CVB : ViewBinding>: BaseFragment<VM, FragmentSearchBaseBinding>() {
    override val vbClass: KClass<FragmentSearchBaseBinding>
        get() = FragmentSearchBaseBinding::class

    abstract val contentVbClass: KClass<CVB>
    private var _contentBinding: CVB? = null
    protected val contentBinding get() = _contentBinding!!

    private val sharedViewModel: SearchBaseViewModel by sharedViewModel<SearchBaseViewModel, SearchFragment>()

    protected val navOptions = NavOptions.Builder()
        .setEnterAnim(RC.anim.slide_in_right)
        .setExitAnim(RC.anim.slide_out_left)
        .setPopEnterAnim(RC.anim.slide_in_left)
        .setPopExitAnim(RC.anim.slide_out_right)
        .build()

    private val apiFailedHandler: (ApiFailedState?) -> Unit = { error ->
        error?.let{ showToast(error.msg) }
    }

    private val recommendListFragment by lazy {
        SearchRecommendListFragment().apply {
            onClickListener = { word ->
                toSearchResult(word)
                addSearchRecord(word)
                dismiss()
            }
            onDismissListener = {
                hideKeyboard(requireContext(), getSearchEditText())
            }
        }
    }

    private val titleBarHintStr: String
        get() = R.string.search_bar_hint.toTranslatedStr()

    private var canSearch: Boolean = true

    override fun onResume() {
        super.onResume()
        updateSearchText(getCurrentKeyword())
    }

    @CallSuper
    override fun initView(savedInstanceState: Bundle?) {
        inflateContentLayout()
        setTitleBar()
        setRecommend()
        setBackPressHandler(::onBackPressed)
    }

    override fun initListener() {
    }

    @CallSuper
    override suspend fun createObserver() {
        with(sharedViewModel) {
            launch(Lifecycle.State.STARTED) {
                launch {
                    currentLanguage.collect {
                        onLanguageChanged(it)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _contentBinding = null
    }

    @CallSuper
    open fun onLanguageChanged(locale: Locale) {
        setTitleBar()
    }

    @CallSuper
    override fun onBackPressed(): Boolean {
        closeDatePicker()
        return super.onBackPressed()
    }

    @CallSuper
    open fun toSearchResult(word: String) {
        parentFragmentManager.clearFragmentResult(GO_BACK_TO_MAIN)
        when {
            this is SearchResultDirectMatchFragment && word == mViewModel.currentTitle -> {
                // DirectMatch頁且關鍵字相同，呼叫子頁自身再次搜索功能
                with(mViewModel) {
                    val id = directMatchId ?: return
                    val type = directMatchType ?: return
                    // 重置選擇日期
                    setSelectedDate(null)
                    getSearchResult(id = id.toString(), type = type)
                }
            }
            this is SearchFragment -> {
                // 在SearchFragment中，直接跳轉到SearchResultBaseFragment
                setCurrentKeyword(word)
                findNavController().navigate(R.id.searchResultBaseFragment, null, navOptions)
            }
            else -> {
                // 在其他Fragment中，要popBackStack到SearchResultBaseFragment
                setCurrentKeyword(word)
                setFragmentResult(FROM_POP_BACK, bundleOf(FROM_POP_BACK to true))
                findNavController().popBackStack(R.id.searchResultBaseFragment, false)
            }
        }
    }

    @CallSuper
    open fun addSearchRecord(word: String) {
        if (word.isNotEmpty()) {
            sharedViewModel.addOneRecord(word)
        }
    }

    protected fun getCurrentKeyword(): String {
        return sharedViewModel.getCurrentKeyword() ?: ""
    }

    private fun setCurrentKeyword(keyword: String) {
        sharedViewModel.setCurrentKeyword(keyword)
    }

    protected fun setTempScreenShot() {
        val view = mBinding.clRoot
        view.doOnLayout {
            val bitmap = createBitmap(view.width, view.height)
            val canvas = Canvas(bitmap)
            view.draw(canvas)

            sharedViewModel.setTempScreenShot(bitmap)
        }
    }

    protected fun getTempScreenShot(): Bitmap? {
        return sharedViewModel.getTempScreenShot()
    }

    protected fun clearTempScreenShot() {
        sharedViewModel.clearTempScreenShot()
    }

    protected fun Int.toTranslatedStr(): String {
        return SkinnableResourceManager.getString(
            requireContext(),
            this,
            sharedViewModel.getCurrentLanguage()
        )
    }

    protected fun updateSearchText(word: String, afterChange: (() -> Unit)? = null) {
        canSearch = false
        getSearchEditText()
            .apply {
                setText(word)
                setSelection(word.length)
            }
        afterChange?.invoke()
        canSearch = true
    }

    private fun setBackPressHandler(onBackPress: (() -> Boolean)? = null) {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if(onBackPress?.invoke() == true) return@addCallback
            isEnabled = false
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun inflateContentLayout() {
        _contentBinding = getViewBind(contentVbClass, mBinding.flContentContainer, false)
        mBinding.flContentContainer.addView(contentBinding.root)
    }

    private fun setTitleBar() {
        with(mBinding) {
            with(sharedViewModel) {
                //设置标题
                titleBar.loadSearchTitleBar(
                    hint = titleBarHintStr,
                    afterTextChanged = { text, _ ->
                        if (!canSearch) return@loadSearchTitleBar

                        val count = text?.length ?: 0
                        // 避免點擊清空搜尋時失去焦點且收起鍵盤
                        if (count == 0) {
                            showKeyboard(requireContext(), getSearchEditText())
                        }

                        // 搜索自动补充词汇
                        recommendListFragment.apply {
                            if(onClickListener == null) {
                                onClickListener = { recommendWord ->
                                    updateSearchText(recommendWord) {
                                        toSearchResult(recommendWord)
                                        dismiss()
                                    }
                                }
                            }
                            text?.toString().let { keyword ->
                                updateKeyword(keyword, apiFailedHandler)
                                if(keyword?.isNotBlank() == true) show()
                                else dismiss()
                            }
                        }
                        updateSearchBtnColor()
                    },
                    onSearch = { content, _ ->
                        if (TextUtils.isEmpty(content)) {
                            showToast(titleBarHintStr)
                            return@loadSearchTitleBar
                        }
                        toSearchResult(content)
                        closeDatePicker()
                        recommendListFragment.dismiss()
                        addSearchRecord(content)
                        hideKeyboard(requireContext(), getSearchEditText())
                    },
                    onBack = {
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                )

                getTitleBarBackIcon().apply {
                    doOnLayout {
                        setImageDrawable(
                            getDrawable(requireContext(), R.drawable.ic_search_left_arrow)
                        )
                    }
                }

                getSearchEditText().setOnFocusChangeListener { _, isFocus ->
                    if(!isFocus) updateSearchBtnColor()
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

    protected fun updateStatusSearchBar(updateSearchBarBackground: Boolean = true) {
        updateStatusTitleBar()
        updateTitleBarBackIcon()
        updateSearchTextColor()
        updateSearchBtnColor()
        if(updateSearchBarBackground) updateSearchBarBackground()
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
            doOnLayout {
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

    @CallSuper
    open fun closeDatePicker() = Unit

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

    companion object {
        const val FROM_POP_BACK = "from_pop_back"
        const val GO_BACK_TO_MAIN = "GO_BACK_TO_MAIN"
    }
}