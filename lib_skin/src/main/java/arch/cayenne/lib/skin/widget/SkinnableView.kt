package arch.cayenne.lib.skin.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.skin.SkinnableManager
import arch.cayenne.lib.skin.widget.helper.SkinnableBackGroundHelper
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
 open class SkinnableView :View {

     private lateinit var backgroundTintHelper:SkinnableBackGroundHelper
    private val sportSkinManager: SkinnableManager by inject(SkinnableManager::class.java)

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
                }
            }
        }
    }

    private fun initView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) {
        backgroundTintHelper =  SkinnableBackGroundHelper(this)
        backgroundTintHelper.loadFromAttributes(attrs, defStyleAttr)

    }

}