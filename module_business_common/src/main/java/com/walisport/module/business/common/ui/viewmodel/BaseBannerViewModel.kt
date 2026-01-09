package com.walisport.module.business.common.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.http.data.Result
import com.walisport.module.business.common.ui.adapter.BannerImageMatchAdapter
import com.walisport.module.business.common.utils.biz.BannerBiz

/**
 * @date: 2026/1/8 22:33
 * @description:
 */
abstract class BaseBannerViewModel:BaseViewModel() {


    suspend fun getBannerList():List<BannerImageMatchAdapter.ImageData>{
        val result =  BannerBiz.getBannerList()
        return if( result is Result.Success){
            result.data.data.map {
                BannerImageMatchAdapter.ImageData(
                    imgUrl = it.imageDomain+it.bottomImagePath,
                    targetUrl = it.buttons[0].operateParams[0]
                )
            }
        }else {
            "getBannerList failed:$result".loge()
            emptyList()
        }
    }
}