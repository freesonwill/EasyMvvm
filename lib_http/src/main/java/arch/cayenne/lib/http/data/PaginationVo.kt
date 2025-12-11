package arch.cayenne.lib.http.data

/**
 *分页对象
 * @date: 2025/12/11 11:40
 * @description:
 */
data class PaginationVo(
    val currentPage: Int ,//当前页
    val totalPages: Int ,// 总页数
    val totalItems: Int ,//总记录数
    val limit: Int ,//限制
    val hasMore: Boolean//是否有更多数据
)
