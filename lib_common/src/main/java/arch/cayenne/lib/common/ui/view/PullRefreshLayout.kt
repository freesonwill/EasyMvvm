package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.util.AttributeSet
import com.scwang.smart.refresh.layout.SmartRefreshLayout

class PullRefreshLayout : SmartRefreshLayout {

    private lateinit var header: PullRefreshHeader
    private lateinit var footer: PullRefreshFooter

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        header = PullRefreshHeader(context)
        setRefreshHeader(header)
        footer = PullRefreshFooter(context)
        setRefreshFooter(footer)
    }

    //设置联赛日程模式后刷新状态文字颜色不随着皮肤变化而变化
    fun setLeagueMode() {
        post {
            header.setLeagueMode()
            footer.setLeagueMode()
        }
    }
}