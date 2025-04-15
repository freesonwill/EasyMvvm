package arch.cayenne.lib.skin.widget

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import arch.cayenne.lib.skin.SportSkinManager
import arch.cayenne.lib.skin.widget.helper.SportSkinBackGroundHelper
import arch.cayenne.lib.skin.widget.helper.SportSkinTabLayoutHelper
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class SportTabLayout : TabLayout {

    private val backgroundTintHelper = SportSkinBackGroundHelper(this)
    private val sportSkinManager: SportSkinManager by inject(SportSkinManager::class.java)
    private val tabLayoutHelper = SportSkinTabLayoutHelper(this)

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr) {
        initView(context, attrs, defStyleAttr)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        this.findViewTreeLifecycleOwner()?.lifecycleScope?.apply {
            launch {
                sportSkinManager.skinFlow.collect {
                    backgroundTintHelper.updateSkin()
                    tabLayoutHelper.updateSkin()
                }
            }
        }
    }

    private fun initView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        backgroundTintHelper.loadFromAttributes(attrs, defStyleAttr)
        tabLayoutHelper.loadFromAttributes(attrs, defStyleAttr)
    }
}