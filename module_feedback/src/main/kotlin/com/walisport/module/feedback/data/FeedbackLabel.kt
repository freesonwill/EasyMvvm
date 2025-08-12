package com.walisport.module.feedback.data

data class FeedbackLabel @JvmOverloads constructor(
   val code: Int,           //标签code
   val name: String = "",       // 标签名称
   var flags:Boolean = false, //标签是否选中

)
