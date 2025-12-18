package com.walisport.module.hall.data

import arch.cayenne.lib.http.data.PaginationVo

/**
 *
 * @date: 2025/12/18 17:36
 * @description:
 */
data class BettingPageVo(val pagination: PaginationVo , val list: List<BettingVo>)
