package arch.cayenne.module.home.data.constants

enum class TournamentListState {
    INIT_LIST,   // 初始化分組後的完整列表
    RESTORE_LIST,   // 離開搜尋模式, 恢復完整列表
    LIST_DATA_EMPTY,         // 沒有任何聯賽資料
    SEARCH_INIT,       // 搜尋模式下，預設搜尋框為空, 列表為空
    SEARCH_MATCH,         // 搜尋有結果
    SEARCH_DATA_EMPTY         // 搜尋關鍵字但無匹配結果
}