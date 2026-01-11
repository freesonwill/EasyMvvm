package com.walisport.module.business.common.data

/**
 * @date: 2026/1/8 15:40
 * @description:
 */
data class BannerActiveBean(
    val activityType: Int,
    val operateType: Int,
    val imagePath: String,
    val operateParams: List<String>
) {
    companion object {
        const val ACTIVITY_TYPE_HOME_FIXED = 1 // 首页固定活动区
        const val ACTIVITY_TYPE_INVITE_FRIEND = 2 // 邀请好友活动
    }
    val targetUrl get() = operateParams.getOrNull(0) ?: ""
}