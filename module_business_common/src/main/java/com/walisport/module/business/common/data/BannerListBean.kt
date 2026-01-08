package com.walisport.module.business.common.data

data class BannerListBean(
    val bottomImagePath: String,
    val bottomImageVersion: Int,
    val buttons: List<BannerListButton>,
    val imageDomain: String
)