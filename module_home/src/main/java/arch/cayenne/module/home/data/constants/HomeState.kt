package arch.cayenne.module.home.data.constants

import arch.cayenne.lib.base.data.constants.DataState

sealed class HomeState: DataState {
    data object PlayTypeClick : HomeState()

    sealed class Sport: HomeState() {
        data object Loading : Sport()
        data object LoadSuccess : Sport()
    }

    sealed class Tournament: HomeState() {
        data object Loading : Tournament()
        data object LoadSuccess : Tournament()
        data object LoadListSuccess : Tournament()
    }

    sealed class Match: HomeState() {
        data object Loading : Match()
        data object LoadSuccess : Match()
    }

    sealed class Schedule: HomeState() {
        data object Loading : Schedule()
        data object LoadSuccess : Schedule()
    }
}