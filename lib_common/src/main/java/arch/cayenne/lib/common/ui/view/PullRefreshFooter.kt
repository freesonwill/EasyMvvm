package arch.cayenne.lib.common.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState

/**
 * @author: wenxi
 * @date: 30/5/25 16:56
 * @description:
 */
class PullRefreshFooter(context: Context) : ClassicsFooter(context) {

    @SuppressLint("SetTextI18n")
    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState
    ) {
        mTextFinish = ""
        mTextFailed = ""
        when (newState) {
            RefreshState.PullUpToLoad -> {
                mTitleText.text = R.string.load_more_up.getString()
            }
            else -> Unit
        }
        mArrowView.visibility = View.GONE
    }
}