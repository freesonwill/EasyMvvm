package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.bet.data.OddsChangeEnum
import arch.cayenne.module.bet.repo.OddsChangeRepository

class OddsChangeViewModel(private val repo: OddsChangeRepository): BaseViewModel() {

    private val _oddsChangeListener = MutableLiveData<OddsChangeEnum>()
    val oddsChangeListener: LiveData<OddsChangeEnum> = _oddsChangeListener

    init {
        _oddsChangeListener.value = repo.getOddsChange()
    }

    fun saveOddsChange(value: OddsChangeEnum) {
        repo.saveOddsChange(value)
        _oddsChangeListener.value = value
    }
}