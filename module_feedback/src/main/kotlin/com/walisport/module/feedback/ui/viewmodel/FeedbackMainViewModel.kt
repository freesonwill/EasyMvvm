package com.walisport.module.feedback.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.feedback.data.FeedbackMainRepository
import plugin.koin.KoinViewModel

@KoinViewModel
class FeedbackMainViewModel(private val repo: FeedbackMainRepository) : BaseViewModel() {

    val maxInputLength = 200

    private val _checkBoxSelected: MutableLiveData<Boolean> = MutableLiveData(false)
    val checkBoxSelected: LiveData<Boolean> = _checkBoxSelected

    private val _textInputted: MutableLiveData<Boolean> = MutableLiveData(false)
    val textInputted: LiveData<Boolean> = _textInputted

    //checkBoxSelected和textInputted均为true时， btnEnabled才能为true
    val btnEnabled = MediatorLiveData<Boolean>().apply {
        listOf(checkBoxSelected, textInputted).forEach {
            addSource(it) {
                value = listOf(checkBoxSelected, textInputted).all { ele -> ele.value == true }
            }
        }
    }

    override fun initViewModel() {
        super.initViewModel()
    }

    fun setCheckBoxSelected(selected: Boolean) {
        _checkBoxSelected.value = selected
    }

    fun setTextInputted(flag: Boolean) {
        _textInputted.value = flag
    }


}