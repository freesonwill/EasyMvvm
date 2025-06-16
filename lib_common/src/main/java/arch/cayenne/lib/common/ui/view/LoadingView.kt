package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import arch.cayenne.lib.common.databinding.ViewLoadingBinding

class LoadingView : LinearLayout {
    private lateinit var binding: ViewLoadingBinding
    private lateinit var progressDrawable: ProgressDrawable

    constructor(context: Context) : super(context) {
        initView()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        orientation = VERTICAL
        val inflater = LayoutInflater.from(context)
        binding = ViewLoadingBinding.inflate(inflater, this)
        progressDrawable = ProgressDrawable()
        binding.ivProgress.setImageDrawable(progressDrawable)
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        if (visibility == View.VISIBLE) {
            progressDrawable.start()
        } else {
            progressDrawable.stop()
        }
        super.onVisibilityChanged(changedView, visibility)
    }

    override fun onDetachedFromWindow() {
        progressDrawable.stop()
        super.onDetachedFromWindow()
    }
}