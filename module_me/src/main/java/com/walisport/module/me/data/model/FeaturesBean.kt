package com.walisport.module.me.data.model

data class FeaturesBean(
    val id:Int,
    val drawableId: Int,
    val titleResId: Int,
    val isShowRectangleText: Boolean = false,
    val rectangleText: String = "",
    val clickListener: ()->Unit
)
