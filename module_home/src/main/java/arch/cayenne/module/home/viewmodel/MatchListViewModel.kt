package arch.cayenne.module.home.viewmodel

import arch.cayenne.module.home.enums.PlayType

class MatchListViewModel : BaseGameListViewModel() {
    override val playType: PlayType = PlayType.TODAY

}