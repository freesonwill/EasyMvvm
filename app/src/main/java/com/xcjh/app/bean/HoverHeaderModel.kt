package com.xcjh.app.bean

import com.drake.brv.item.ItemHover

/**
 *悬停
 */
data class HoverHeaderModel(
    val url: String,   // 不同格式的视频地址
    val name: String,   // 普通, 清晰
): ItemHover{
    override var itemHover: Boolean = true
}
