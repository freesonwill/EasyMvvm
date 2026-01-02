package com.walisport.module.search.data.constants

import arch.cayenne.lib.database.entity.GameSupplierDataModel
import com.walisport.module.search.data.model.SearchResultBean

sealed class SearchResultUiState(type: SearchResultTypeEnum?, data: SearchResultBean? = null) : SearchResultBaseUiState(type) {
    data class ResultList(override val data: SearchResultBean) : SearchResultUiState(SearchResultTypeEnum.LIST, data)
    data class DirectMatch(override val type: SearchResultTypeEnum, override val data: SearchResultBean) : SearchResultUiState(type, data)
    
    /** 遊戲供應商精準匹配 */
    data class VendorDirectMatch(
        val keyword: String,
        val supplier: GameSupplierDataModel,
    ) : SearchResultUiState(null, null)

    /** 遊戲分類匹配（例如：電子、老虎機） */
    data class GameCategoryMatch(
        val keyword: String,
        val gameTypeId: Int
    ) : SearchResultUiState(null, null)
}

open class SearchResultBaseUiState(
    open val type: SearchResultTypeEnum? = null,
    open val data: SearchResultBean? = null,
)