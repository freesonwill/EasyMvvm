package arch.cayenne.module.chat.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import plugin.koin.KoinViewModel

@KoinViewModel
class ChatUserInfoViewModel : BaseViewModel() {
    //子类判断是否滑动到顶部
    private val _sonVerticalScrollIsTop = MutableLiveData<Boolean?>()
    val sonVerticalScrollIsTop: LiveData<Boolean?> = _sonVerticalScrollIsTop

    fun setSonVerticalScrollIsTop(boo:Boolean){
        if (boo!=sonVerticalScrollIsTop.value){
            _sonVerticalScrollIsTop.value = boo
        }
    }

    fun getSonVerticalScrollIsTop():Boolean?{
        return sonVerticalScrollIsTop.value
    }
}