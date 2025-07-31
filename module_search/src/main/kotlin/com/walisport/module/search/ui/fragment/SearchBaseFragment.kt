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
import arch.cayenne.lib.common.utils.ext.NavResultExt.sendResult
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.lib.skin.res.SkinnableResourceManager.getDrawable
import arch.cayenne.lib.skin.widget.SkinnableImageView
import com.walisport.module.search.R
import com.walisport.module.search.databinding.FragmentSearchBaseBinding
import com.walisport.module.search.ui.fragment.SearchResultBaseFragment.Companion.GO_BACK_TO_MAIN
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
        get() = R.string.please_input_content.toTranslatedStr()

    private var canSearch: Boolean = true

    @CallSuper
    override fun initView(savedInstanceState: Bundle?) {
        inflateContentLayout()
        setTitleBar()
        setRecommend()
        setBackPressHandler(::onBackPressed)
    }

    override fun initListener() = Unit

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
        if (findNavController().currentDestination?.id != R.id.searchFragment) {
            parentFragmentManager.setFragmentResult(SEARCH_KEY, bundleOf(SEARCH_KEY to word))
            findNavController().popBackStack(R.id.searchResultBaseFragment, false)
        } else {
            sendResult(
                key = SEARCH_KEY,
                value = word,
                destinationId = R.id.searchFragment,
                navController = findNavController()
            )
            findNavController().navigate(R.id.searchResultBaseFragment, null, navOptions)
        }
    }

    @CallSuper
    open fun addSearchRecord(word: String) {
        if (word.isNotEmpty()) {
            sharedViewModel.addOneRecord(word)
        }
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
            closeDatePicker()

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
                                    toSearchResult(recommendWord)
                                    recommendListFragment.dismiss()
                                }
                            }
                        }
                        with(text?.toString()) {
                            recommendListFragment.updateKeyword(this, apiFailedHandler)
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

                getSearchEditText().apply {
                    setOnFocusChangeListener { _, isFocused ->
                        updateSearchBtnColor()
                        if (isFocused) {
                            closeDatePicker()
                            if (text?.isNotEmpty() == true) {
                                recommendListFragment.updateKeyword(text.toString(), apiFailedHandler)
                            }
                        }
                    }
                    setOnClickListener {
                        if (text?.isNotEmpty() == true) {
                            recommendListFragment.updateKeyword(text?.toString(), apiFailedHandler)
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

    protected fun updateStatusSearchBar() {
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
        const val SEARCH_KEY = "searchKey"
    }
}