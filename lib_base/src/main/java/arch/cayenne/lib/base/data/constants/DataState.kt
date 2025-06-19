package arch.cayenne.lib.base.data.constants

/***
 * 負責業務與UI交互狀態 (ViewModel to UI)
 */
sealed interface DataState {
    data object None : DataState
    data object Loading : DataState
    data object DataEmpty : DataState
    data object NetworkUnavailable : DataState
    data object LoadSuccess : DataState
}