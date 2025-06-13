package com.walisport.module.search.data.constants

import android.os.Parcelable
import com.walisport.module.search.data.model.SearchResultBean
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class SearchNavigationEvent : Parcelable {
    data class ToSearchResultBase(val searchKey: String) : SearchNavigationEvent()
    data class ToSearchList(val data: SearchResultBean) : SearchNavigationEvent()
    data class ToSearchDirectMatch(val data: SearchResultBean? = null, val id: String? = null, val type: SearchTypeEnum? = null) : SearchNavigationEvent()
    data class ToLiveFragment(val deepLink: String): SearchNavigationEvent()
}