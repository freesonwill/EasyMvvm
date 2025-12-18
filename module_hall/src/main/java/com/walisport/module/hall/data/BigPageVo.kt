package com.walisport.module.hall.data

import arch.cayenne.lib.http.data.PaginationVo
import com.walisport.module.hall.data.constants.BigVo

/**
 *
 * @date: 2025/12/18 22:01
 * @description:
 */
data class BigPageVo(val pagination: PaginationVo , val list: List<BigVo>)