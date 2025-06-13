package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel

class FloatingButtonControlViewModel: BaseViewModel() {

    private val _isShowButtonListener = MutableLiveData(true)
    val isShowButtonListener: LiveData<Boolean> get() = _isShowButtonListener

    private val _onClickAnimationListener = MutableLiveData<Pair<Float, Float>>()
    val onClickAnimationListener: LiveData<Pair<Float, Float>> get() = _onClickAnimationListener

    fun show() {
        _isShowButtonListener.value = true
    }

    fun hide() {
        _isShowButtonListener.value = false
    }

    fun setClickAnimation(x: Float, y: Float) {
        _onClickAnimationListener.value = Pair(x, y)
    }
}