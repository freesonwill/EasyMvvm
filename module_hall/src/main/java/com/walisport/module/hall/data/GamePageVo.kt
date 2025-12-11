package com.walisport.module.hall.data

import arch.cayenne.lib.http.data.PaginationVo

/**
 *
 * @date: 2025/12/11 11:38
 * @description:
 */
data class GamePageVo(val pagination: PaginationVo , val list: List<GameVo>) {
}
