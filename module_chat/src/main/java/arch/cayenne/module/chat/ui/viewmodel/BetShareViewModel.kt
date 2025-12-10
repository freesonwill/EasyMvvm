package arch.cayenne.module.chat.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd

/**
 * @author: wenxi
 * @date: 8/12/25 14:54
 * @description:
 */
class BetShareViewModel : BaseViewModel() {
    private val _expandLivedata: MutableLiveData<Boolean> = MutableLiveData()
    val expandLiveData: LiveData<Boolean> = _expandLivedata
    private val _closeLivedata: MutableLiveData<Boolean> = MutableLiveData()
    val closeLiveData: LiveData<Boolean> = _closeLivedata


    fun expandDialog() {
        val value = _expandLivedata.value?.let { !it } ?: false
        _expandLivedata.value = value
    }

    fun closeDialog() {
        val value = _closeLivedata.value?.let { !it } ?: false
        _closeLivedata.value = value
    }
}