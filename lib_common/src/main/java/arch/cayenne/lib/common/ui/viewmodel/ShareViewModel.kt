package arch.cayenne.lib.common.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
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
}