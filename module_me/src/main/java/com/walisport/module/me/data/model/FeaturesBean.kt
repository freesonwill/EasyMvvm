package com.walisport.module.me.data.model

data class FeaturesBean(
    val id:Int,
    val drawableId: Int,
    val titleResId: Int,
    val clickListener: ()->Unit
)
