package com.walisport.module.popup.slot.data

/**
 *
 * @date: 2025/12/26 22:17
 * @description:
 */


data class PopupVo(
    val popupId: Long,
    val items: List<PopupItemVo>
)

data class PopupItemVo(
    val operateType: String,
    val operateParams: List<String>,
    val bottomImagePath: String
)