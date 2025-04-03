package com.walisport.lib.skin.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.walisport.lib.skin.SportSkinManager
import com.walisport.lib.skin.widget.helper.SportSkinBackGroundHelper
import com.walisport.lib.skin.widget.helper.SportSkinTextHelper
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject


@SuppressLint("CustomViewStyleable")
class SportButton : AppCompatButton {
    private val mTextHelper: SportSkinTextHelper = SportSkinTextHelper(this)
    private val mBackgroundTintHelper: SportSkinBackGroundHelper = SportSkinBackGroundHelper(this)
    private val sportSkinManager: SportSkinManager by inject(SportSkinManager::class.java)



    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = androidx.appcompat.R.attr.buttonStyle
    )
            : super(context, attrs, defStyleAttr) {
        initView(context, attrs, defStyleAttr)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        this.findViewTreeLifecycleOwner()?.lifecycleScope?.apply {
            launch {
                sportSkinManager.skinFlow.collect {
                    mBackgroundTintHelper.updateSkin()
                    mTextHelper.updateSkin()
                }
            }
            launch {
                sportSkinManager.languageFlow.collect {
                    it?.let {
                        mTextHelper.updateLanguage(it.language)
                    }
                }
            }
        }
    }

    private fun initView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        mBackgroundTintHelper.loadFromAttributes(attrs, defStyleAttr)
        mTextHelper.loadFromAttributes(attrs, defStyleAttr)
    }

    override fun setBackgroundResource(@DrawableRes resId: Int) {
        super.setBackgroundResource(resId)
        mBackgroundTintHelper.setSrcId(resId)
    }

    override fun setTextAppearance(resId: Int) {
        setTextAppearance(context, resId)
    }

    override fun setTextAppearance(context: Context, resId: Int) {
        super.setTextAppearance(context, resId)
        mTextHelper.onSetTextAppearance(context, resId)
    }

    override fun setCompoundDrawablesRelativeWithIntrinsicBounds(
        @DrawableRes start: Int,
        @DrawableRes top: Int,
        @DrawableRes end: Int,
        @DrawableRes bottom: Int
    ) {
        super.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom)
        mTextHelper.onSetCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom)
    }

    override fun setCompoundDrawablesWithIntrinsicBounds(
        @DrawableRes left: Int,
        @DrawableRes top: Int,
        @DrawableRes right: Int,
        @DrawableRes bottom: Int
    ) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
        mTextHelper.onSetCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
    }




    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
//        coroutineScope.cancel()
    }


}
