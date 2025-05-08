package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.util.AttributeSet
import com.scwang.smart.refresh.layout.SmartRefreshLayout

class SportRefreshLayout: SmartRefreshLayout {
    constructor(context: Context?) : super(context) {
        init()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        setRefreshHeader(SportRefreshHeader(context))
    }
}