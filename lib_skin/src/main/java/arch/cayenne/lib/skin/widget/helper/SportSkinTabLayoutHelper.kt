package arch.cayenne.lib.skin.widget.helper

import android.annotation.SuppressLint
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.Log
import com.google.android.material.R
import com.google.android.material.tabs.TabLayout

class SportSkinTabLayoutHelper(mView: TabLayout) : SportSkinHelper(mView) {
    private var indicatorColor: Int = INVALID_ID
    private var tabBackground: Int = INVALID_ID
    private var textColor: Int = INVALID_ID
    private var textSelectedColor: Int = INVALID_ID

    override val mView: TabLayout
        get() = super.mView as TabLayout

    @SuppressLint("Recycle", "PrivateResource")
    override fun loadFromAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        var a: TypedArray? = null
        try {
            a = mView.context.obtainStyledAttributes(attrs, R.styleable.TabLayout)
            indicatorColor = a!!.getResourceId(R.styleable.TabLayout_tabIndicator, INVALID_ID)
            tabBackground = a!!.getResourceId(R.styleable.TabLayout_tabBackground, INVALID_ID)
            textColor = a!!.getResourceId(R.styleable.TabLayout_tabTextColor, INVALID_ID)
            textSelectedColor =
                a!!.getResourceId(R.styleable.TabLayout_tabSelectedTextColor, INVALID_ID)
        } finally {
            a?.recycle()
        }
        updateSkin()


    }

    override fun updateSkin() {
        val context = mView.context
        if (checkResourceIdValid(indicatorColor)) {
            val color = resourcesManager.getColor(context, indicatorColor)
            if (checkResourceIdValid(color)) {
                mView.setSelectedTabIndicatorColor(color)
            }
        }
        if (checkResourceIdValid(textColor)) {
            val normal = resourcesManager.getColor(context, textColor)
            val selectd = resourcesManager.getColor(context, textSelectedColor)

            if (checkResourceIdValid(normal) && checkResourceIdValid(selectd)) {
                mView.setTabTextColors(normal, selectd)
            }
        }
        updateTabItemBack()
    }


    private fun updateTabItemBack() {
        if (checkResourceIdValid(tabBackground)) {
                for (i in 0 until mView.tabCount) {
                    val tab = mView.getTabAt(i)
//                    if(tab?.view?.isAttachedToWindow == true){
                        tab?.view?.setBackgroundResource(resourcesManager.getTargetResourceId(mView.context,tabBackground))
//                    }
            }
        }
    }


    override fun updateLanguage(languageCode: String) {
    }
}