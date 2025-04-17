package arch.cayenne.module.home.viewmodel

import arch.cayenne.module.home.enums.PlayType

class EarlyGameListViewModel: BaseGameListViewModel() {
    override val playType: PlayType = PlayType.EARLY

}