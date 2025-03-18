package com.walisport.lib_base.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * @author: zhangsan
 * @date: 2025/3/17 18:19
 * @description:
 */
abstract class BaseRepository : IRepository {
    override val scope: CoroutineScope by lazy { CoroutineScope(Dispatchers.IO) }
}