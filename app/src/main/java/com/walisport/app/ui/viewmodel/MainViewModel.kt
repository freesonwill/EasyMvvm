package com.walisport.app.ui.viewmodel

import android.graphics.drawable.Drawable
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.common.ui.viewmodel.BaseActivityViewModel
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.module.order.ui.viewmodel.BetMode
import com.walisport.app.data.repo.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

/**
 * @author: zhangsan
 * @date: 2025/3/14 16:56
 * @description:
 */
class MainViewModel : BaseActivityViewModel() {
    private val repository: MainRepository by inject { parametersOf(viewModelScope) }
    override val shouldBeAutoLogin: Boolean = true
    val betSlotFlow = MutableStateFlow<BetSlot>(BetSlot.BET_RECORD)
    val selectedIndexFlow = MutableStateFlow(0)

    enum class BetSlot(val icon:Drawable, val title:String){
        BET_RECORD(arch.cayenne.module.home.R.drawable.ic_betslip.getDrawable(),arch.cayenne.lib.common.R.string.drawer_bet_record.getString()),
        BET_SLIP(arch.cayenne.module.home.R.drawable.ic_betslip.getDrawable(), arch.cayenne.lib.res.R.string.bet_title.getString())
        ;
        fun toBetMode():BetMode {
            return BetMode.entries.toTypedArray()[this.ordinal]
        }
    }

    override fun initViewModel() {
        super.initViewModel()
        viewModelScope.launch {
            repository.observeLoginChange()
                .filter { it }
                .collect {
                repository.loadSportList()
                repository.observeSystemNotify()
            }
        }

    }

    fun getSkinType(): String {
        return repository.getSkinType()
    }

}