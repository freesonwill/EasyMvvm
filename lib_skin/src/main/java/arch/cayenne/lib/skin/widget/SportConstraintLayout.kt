package arch.cayenne.lib.skin.widget

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.skin.SportSkinManager
import arch.cayenne.lib.skin.widget.helper.SportSkinBackGroundHelper
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

open class SportConstraintLayout : ConstraintLayout {
    private val backGroundHelper: SportSkinBackGroundHelper = SportSkinBackGroundHelper(this)
    private val sportSkinManager: SportSkinManager by inject(SportSkinManager::class.java)

    constructor(context: Context) : super(context){
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context,attrs)
    }

    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr) {
        initView(context,attrs,defStyleAttr)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        this.findViewTreeLifecycleOwner()?.lifecycleScope?.apply {
            launch {
                sportSkinManager.skinFlow.collect {
                    backGroundHelper.updateSkin()

                }
            }
        }
    }

    private fun initView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0){
        backGroundHelper.loadFromAttributes(attrs, defStyleAttr)
    }



}