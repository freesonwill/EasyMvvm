package arch.cayenne.lib.skin.widget.helper

import android.annotation.SuppressLint
import android.content.res.TypedArray
import android.util.AttributeSet
import arch.cayenne.lib.skin.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab

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
            a = mView.context.obtainStyledAttributes(attrs, R.styleable.SportTabLayoutHelper)
            indicatorColor =
                a!!.getResourceId(R.styleable.SportTabLayoutHelper_tabIndicatorColor, INVALID_ID)
            tabBackground =
                a!!.getResourceId(R.styleable.SportTabLayoutHelper_sportTabBackground, INVALID_ID)
            textColor = a!!.getResourceId(R.styleable.SportTabLayoutHelper_tabTextColor, INVALID_ID)
            textSelectedColor =
                a!!.getResourceId(R.styleable.SportTabLayoutHelper_tabSelectedTextColor, INVALID_ID)
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
    }

    fun updateTabBackground(tab: Tab) {
        tab.view.setBackgroundResource(resourcesManager.getTargetResourceId(mView.context, tabBackground))
    }

    override fun updateLanguage(languageCode: String) {
    }
}