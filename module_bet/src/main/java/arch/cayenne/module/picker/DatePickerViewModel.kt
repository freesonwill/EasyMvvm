package arch.cayenne.module.picker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.utils.ext.ResourceExt.getStringArray
import arch.cayenne.module.bet.R

class DatePickerViewModel: BaseViewModel() {

    private val _dateTitleListener = MutableLiveData<List<String>>()
    val dateTitleListener: LiveData<List<String>> get() = _dateTitleListener

    init {
        val list = R.array.date_picker.getStringArray()
        _dateTitleListener.value = list
    }
}