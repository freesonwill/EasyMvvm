package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.util.AttributeSet
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshFooter

class PullRefreshLayout: SmartRefreshLayout {
    constructor(context: Context?) : super(context) {
        init()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        setRefreshHeader(PullRefreshHeader(context))
    }

    fun pullRefreshAddFooter(){
        setRefreshFooter(PullRefreshFooter(context))
    }
}