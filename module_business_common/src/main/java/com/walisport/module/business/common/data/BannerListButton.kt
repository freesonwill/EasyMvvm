package com.walisport.module.business.common.data

data class BannerListButton(
    val buttonImagePath: String,
    val buttonImageVersion: Int,
    val buttonImageX: Int,
    val buttonImageY: Int,
    val operateParams: List<String>,
    val operateType: Int
)