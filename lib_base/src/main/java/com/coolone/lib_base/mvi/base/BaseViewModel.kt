package com.coolone.lib_base.mvi.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MVI模式的基类
 */
abstract class BaseViewModel<UiState : IUiState, UiIntent : IUiIntent> : ViewModel() {
    private var _uiStateFlow = MutableStateFlow(initUiState())
    val uiStateFlow: StateFlow<UiState> = _uiStateFlow

    //这个地方为什么不适用Flow而用Channel
    private val _uiIntentFlow: Channel<UiIntent> = Channel()
    val uiIntentFlow: Flow<UiIntent> = _uiIntentFlow.receiveAsFlow()

    init {
        viewModelScope.launch {
            uiIntentFlow.collect {
                handleIntent(it)
            }
        }
    }

    /******************************** Method ******************************************/
    abstract fun initUiState(): UiState

    /**
     * 发送UI State
     */
    protected fun sendUiState(producer: UiState.() -> UiState) {
        _uiStateFlow.update { producer(_uiStateFlow.value) }
    }

    fun sendUiIntent(intent: UiIntent) {
        viewModelScope.launch {
            _uiIntentFlow.send(intent)
        }
    }

    abstract fun handleIntent(intent: UiIntent)
}