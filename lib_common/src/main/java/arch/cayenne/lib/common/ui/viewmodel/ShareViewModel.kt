package arch.cayenne.lib.common.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.data.constants.ShareAppBean
import arch.cayenne.lib.common.data.constants.ShareBean
import arch.cayenne.lib.common.data.constants.ShareLinkEnum

class ShareViewModel : BaseViewModel() {

    private val _shareApps = MutableLiveData<List<ShareBean>>()
    val shareApps: LiveData<List<ShareBean>> get() = _shareApps

    private val _shareLink = MutableLiveData<List<ShareBean>>()
    val shareLink: LiveData<List<ShareBean>> get() = _shareLink

    init {
        _shareLink.value = ShareLinkEnum.entries.toList()
    }

    fun shareApps(): List<ShareBean> {
        return arrayListOf(
            ShareAppBean("聊天室", R.drawable.icon_share_chat),
            ShareAppBean("巴西-中国直播间", R.drawable.icon_share_soccer),
            ShareAppBean("阿森纳-曼联直播间", R.drawable.icon_share_soccer),
            ShareAppBean("WhatsApp", R.drawable.icon_share_what),
            ShareAppBean("更多", R.drawable.icon_share_more)
        )
    }

}