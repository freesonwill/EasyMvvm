package arch.cayenne.module.order.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.order.data.model.ShareBean

class ShareViewModel : BaseViewModel() {

    private val _shareApps = MutableLiveData<List<ShareBean>>()
    val shareApps: LiveData<List<ShareBean>> get() = _shareApps

    private val _shareLink = MutableLiveData<List<ShareBean>>()
    val shareLink: LiveData<List<ShareBean>> get() = _shareLink
}