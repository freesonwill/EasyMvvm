package com.walisport.module.topup.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.topup.data.WithdrawFilterBean
import kotlinx.coroutines.launch

class WithdrawPickerViewModel: BaseViewModel() {

    private val _onSportListener = MutableLiveData<List<WithdrawFilterBean>>()
    val onSportListener: LiveData<List<WithdrawFilterBean>> get() = _onSportListener

    init {
        viewModelScope.launch {
            val temp0 = WithdrawFilterBean(0,"全部", true)
            val temp1 = WithdrawFilterBean(1,"EE钱包", false)
            val temp2 = WithdrawFilterBean(2,"银行卡", false)
            val temp3 = WithdrawFilterBean(3,"USDT", false)
            _onSportListener.value = listOf(temp0, temp1,temp2,temp3)
        }
    }

    fun setSelectedById(id: Int) {
        val current = _onSportListener.value ?: return
        val defaultId = WithdrawFilterBean.ALL_TYPE_ID
        _onSportListener.value = if (id == defaultId) {
            current.map { sport ->
                if (sport.txId == defaultId)  {
                    sport.copy(isSelected = true)
                } else {
                    sport.copy(isSelected = false)
                }
            }
        } else {
            current.map { sport ->
                when (sport.txId) {
                    defaultId -> sport.copy(isSelected = false)
                    id -> sport.copy(isSelected = true)
                    else -> sport
                }
            }
        }
    }

    fun setSelectedById(ids: IntArray) {
        val current = _onSportListener.value ?: return
        if (ids.size == 1) {
            setSelectedById(ids[0])
        } else {
            _onSportListener.value = current.map { sport ->
                sport.copy(isSelected = ids.contains(sport.txId))
            }
        }
    }

    fun getSelectedSportBean(): List<WithdrawFilterBean> {
        return _onSportListener.value?.filter { it.isSelected } ?: listOf(WithdrawFilterBean.getAllTypeBean())
    }

    override fun reset() {
        setSelectedById(intArrayOf(-1))
    }
}