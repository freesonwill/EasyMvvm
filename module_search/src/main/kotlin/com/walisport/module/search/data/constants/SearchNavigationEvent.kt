package com.walisport.module.search.data.constants

sealed class SearchNavigationEvent {
    data class ToSearchResultBase(val searchKey: String) : SearchNavigationEvent()
    data object ToSearchList : SearchNavigationEvent()
    data object ToSearchDirectMatch : SearchNavigationEvent()
    data class ToLiveFragment(val deepLink: String): SearchNavigationEvent()
}