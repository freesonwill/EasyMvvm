package com.walisport.module.hall.data

import arch.cayenne.lib.http.data.PaginationVo

/**
 *
 * @date: 2025/12/11 11:38
 * @description:
 */
data class GameCommonVo(val apiHost:String, val resourceHost:String,val chatHost :String,val gameType: List<GameAllVo>,val gameSupplier: List<GameSupplier>,val category : List<GameCategoryVo>,val sysAvatars: List<SystemAvatarVo>)