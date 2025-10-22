package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import arch.cayenne.lib.common.databinding.ViewCustomGameTabGroupBinding

class CustomGameTabGroupLayout : FrameLayout {
    private lateinit var binding: ViewCustomGameTabGroupBinding
    constructor(context: Context) : super(context) {
        initView(context)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet? = null) {
        binding = ViewCustomGameTabGroupBinding.inflate(LayoutInflater.from(context), this, true)
    }

}