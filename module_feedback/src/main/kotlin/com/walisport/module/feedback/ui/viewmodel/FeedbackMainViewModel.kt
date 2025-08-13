package com.walisport.module.feedback.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.LogUtils
import com.google.protobuf.ByteString
import com.walisport.module.feedback.data.FeedbackLabel
import com.walisport.module.feedback.data.FeedbackMainRepository
import plugin.koin.KoinViewModel

@KoinViewModel
class FeedbackMainViewModel(private val repo: FeedbackMainRepository) : BaseViewModel() {

    val maxInputLength = 200

    private val _checkBoxSelected: MutableLiveData<Boolean> = MutableLiveData(false)
    val checkBoxSelected: LiveData<Boolean> = _checkBoxSelected

    private val _textInputted: MutableLiveData<Boolean> = MutableLiveData(false)
    val textInputted: LiveData<Boolean> = _textInputted

    private val _feedbackLabelList = MutableLiveData<List<FeedbackLabel>?>()
    val feedbackLabelList: LiveData<List<FeedbackLabel>?> = _feedbackLabelList

    private val _feedbackContentAdd: MutableLiveData<Boolean> = MutableLiveData()
    val feedbackContentAdd: LiveData<Boolean> = _feedbackContentAdd


    fun getFeedbackLabelList() {
        callApi({
            repo.getFeedbackLabelList()
        }, {
            if (it is ApiResponseState.Succeeded<*>) {
                it.data.let { data ->
                    _feedbackLabelList.value = data as List<FeedbackLabel>? // 主线程更新 LiveData
                }
            }
        })
    }


    fun feedbackLabelsToByteString(labels: List<FeedbackLabel>): ByteString {
        // 筛选 flags 为 true 的 code，转换为 IntArray
        val codes = labels.filter { it.flags }.map { it.code }.toIntArray()
        val byteArray = codes.flatMap { int ->
            listOf(
                (int shr 24 and 0xFF).toByte(),
                (int shr 16 and 0xFF).toByte(),
                (int shr 8 and 0xFF).toByte(),
                (int and 0xFF).toByte()
            )
        }.toByteArray()
        // 转换为 ByteString
        return ByteString.copyFrom(byteArray)
    }


    fun feedbackContentAdd(content: String, code: ByteString) {
        callApi({
            repo.feedbackContentAdd(content, code)
        }, {
            if (it is ApiResponseState.Succeeded<*>) {
                _feedbackContentAdd.value = true
            }
        })
    }

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