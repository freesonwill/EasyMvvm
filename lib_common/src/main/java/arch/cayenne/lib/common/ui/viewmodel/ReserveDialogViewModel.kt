package arch.cayenne.lib.common.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.common.data.constants.OddsDisplayEnum
import arch.cayenne.lib.common.data.repo.ReserveDialogRepository
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.common.utils.ext.SportStringExt.toOdds

class ReserveDialogViewModel(repo: ReserveDialogRepository) : NumberCalculatorViewModel() {

    companion object {
        private const val ODDS_LIMIT = 999900L
    }

    private val _displayType = MutableLiveData(repo.getOddsType())

    private val _isConfirmEnable = MediatorLiveData<Boolean>().apply {
        var lastType: OddsDisplayEnum? = null
        var lastNumber: Int? = null

        fun update() {
            value = when (lastType) {
                OddsDisplayEnum.EU -> (lastNumber ?: 0) > 100
                OddsDisplayEnum.HK -> (lastNumber ?: 0) > 0
                else -> false
            }
        }

        addSource(_displayType) { type ->
            lastType = type
            update()
        }
        addSource(onEditNumber) { number ->
            lastNumber = number.toOdds()
            update()
        }
    }
    val isConfirmEnable: LiveData<Boolean> get() = _isConfirmEnable

    val minOdds = 1

    fun init(odds: Int) {
        setRemainingNumber(ODDS_LIMIT)
        setMaxNumber(ODDS_LIMIT)
        setNumberLimit(minOdds.toLong(), ODDS_LIMIT)
        setEditNumber(odds.getOdds())
    }

    fun addMixRate() {
        val odds = onEditNumber.value?.let {
            if (it.isEmpty()) {
                minOdds
            } else {
                val rate = if (it.last() == '.') {
                    it.substring(0, it.length - 1)
                } else {
                    it
                }
                (rate.toOdds() + minOdds)
            }
        } ?: minOdds
        setEditNumber(odds.getOdds())
    }
}