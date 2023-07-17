package com.xcjh.app.vm

import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.callback.livedata.BooleanLiveData


class MainVm : BaseViewModel() {

    var feedbackOk = BooleanLiveData()
    /**
     * 反馈
     */
    fun feedback(content: String) {
       /* request(
            { apiService.feedback(FeedbackDTO(content, Constants.APP_ID)) },
            {
                myToast(appContext.getString(R.string.feedback_ok))
                feedbackOk.value = true
            }, {
                //请求失败
                myToast(it.errorMsg)
            }, true
        )*/
    }

}