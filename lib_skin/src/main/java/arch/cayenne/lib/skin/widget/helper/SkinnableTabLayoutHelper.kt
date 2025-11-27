package arch.cayenne.lib.skin.widget.helper

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.skin.R
import arch.cayenne.lib.skin.data.SkinMsgType
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import java.util.Locale

class SkinnableTabLayoutHelper(mView: TabLayout) : SkinnableHelper(mView) {
    private var indicatorColor: Int = INVALID_ID
    private var tabBackground: Int = INVALID_ID
    private var textColor: Int = INVALID_ID
    private var textSelectedColor: Int = INVALID_ID
    private var tabResArray:IntArray = intArrayOf()
    override val mView: TabLayout
        get() = super.mView as TabLayout

    private var stringContext: Context? = null //更新语言
    private var lastLanguage: Locale? = null

    override fun loadFromAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        var a: TypedArray? = null
        try {
            a = mView.context.obtainStyledAttributes(attrs, R.styleable.SportTabLayoutHelper)
            indicatorColor =
                a.getResourceId(R.styleable.SportTabLayoutHelper_tabIndicatorColor, INVALID_ID)
            tabBackground =
                a.getResourceId(R.styleable.SportTabLayoutHelper_sportTabBackground, INVALID_ID)
            textColor = a.getResourceId(R.styleable.SportTabLayoutHelper_tabTextColor, INVALID_ID)
            textSelectedColor =
                a.getResourceId(R.styleable.SportTabLayoutHelper_tabSelectedTextColor, INVALID_ID)
        } finally {
            a?.recycle()
        }
        updateSkin(SkinMsgType.SELF)
    }

    fun updateTabResArray(resArray:IntArray){
        tabResArray = resArray
    }

    override fun updateSkin(msgType: SkinMsgType) {
        if(checkSkinName(msgType)){
            return
        }
        val context = mView.context
        if (checkResourceIdValid(indicatorColor)) {
            val color = resourcesManager.getColor(context, indicatorColor)
            if (checkResourceIdValid(color)) {
                mView.setSelectedTabIndicatorColor(color)
            }
        }
        if (checkResourceIdValid(textColor) && checkResourceIdValid(textSelectedColor)) {
            val normal = resourcesManager.getColor(context, textColor)
            val selected = resourcesManager.getColor(context, textSelectedColor)
            if (checkResourceIdValid(normal) && checkResourceIdValid(selected)) {
                mView.setTabTextColors(normal, selected)
            }
        }
    }

    fun updateTabBackground(tab: Tab) {
        if (checkResourceIdValid(tabBackground)) {
            tab.view.setBackgroundResource(resourcesManager.getTargetResourceId(mView.context, tabBackground))
        }
    }

    /**
     * 更新语言时，更新context的configuration local
     * */
    fun refreshContext(locale: Locale?) {
        if (lastLanguage == locale) {
            return
        }
        lastLanguage = locale
        val configuration = mView.context.resources.configuration
        configuration.setLocale(locale)
        stringContext = mView.context.applicationContext.createConfigurationContext(configuration)
    }


    /**
     * 更新tab语言
     * */
    fun updateLanguage(locale: Locale){
        if (tabResArray.size != mView.tabCount) {
            "ignore updateLanguage local:$locale,because tabResArray:${tabResArray.size} != tabCount:${mView.tabCount},mView:$mView".loge(TAG)
            return
        }

        refreshContext(locale)
        for (i in 0 until mView.tabCount) {
            if(checkResourceIdValid(tabResArray[i])){
                mView.getTabAt(i)?.let {
                    it.text = resourcesManager.getTextResourceText(stringContext ?: mView.context, tabResArray[i],)
                }
            }
        }
    }

}