package arch.cayenne.module.home.test.viewmodel

import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import kotlinx.coroutines.flow.MutableStateFlow
import plugin.koin.KoinViewModel

/**
 * @author: zhangsan
 * @date: 2025/5/8 14:54
 * @description:
 */
@KoinViewModel
class ThirdViewModel :BaseViewModel(){
    var textColor:Int? = null
    var textString:String? = null
    var textColorFlow : MutableStateFlow<Int?> = MutableStateFlow(null)
    val textStringFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    var textColorLiveData : MutableLiveData<Int?> = MutableLiveData(null)
    val textStringLiveData: MutableLiveData<String?> = MutableLiveData(null)


    override fun initViewModel() {
        super.initViewModel()
        "keepViewOnNavigation--initViewModel--->${this}".logd(TAG)
    }


    override fun onCleared() {
        super.onCleared()
        "keepViewOnNavigation--onCleared--->${this}".logd(TAG)
    }

}