package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.bet.data.CombThreeListData

class BetCombViewModel : BaseViewModel() {

    private val _onCombinationListener = MutableLiveData<List<CombThreeListData>>()
    val onCombinationListener: LiveData<List<CombThreeListData>> get() = _onCombinationListener

    private val _onTwoListener = MutableLiveData<List<CombThreeListData>>()
    val onTwoListener: LiveData<List<CombThreeListData>> get() = _onTwoListener

    fun getCombinationData() {
        val tmp1 = CombThreeListData(
            1,
            0f,
            0f,
            "1·2·3",
            9.13f
        )
        val tmp2 = CombThreeListData(
            2,
            0f,
            0f,
            "1·2·4",
            11.29f
        )
        val tmp3 = CombThreeListData(
            3,
            0f,
            0f,
            "1·3·4",
            15.66f
        )
        val tmp4 = CombThreeListData(
            4,
            0f,
            0f,
            "2·3·4",
            7.13f
        )
        _onCombinationListener.value = listOf(tmp1, tmp2, tmp3, tmp4)
    }

    fun getSingleData() {
        val tmp1 = CombThreeListData(
            1,
            10.00f,
            91.30f,
            "1",
            9.13f
        )
        val tmp2 = CombThreeListData(
            2,
            10.00f,
            112.90f,
            "2",
            11.29f
        )
        val tmp3 = CombThreeListData(
            3,
            10.00f,
            156.60f,
            "3",
            15.66f
        )
        val tmp4 = CombThreeListData(
            4,
            10.00f,
            71.30f,
            "4",
            7.13f
        )
        _onCombinationListener.value = listOf(tmp1, tmp2, tmp3, tmp4)
    }

    fun getTwoData() {
        val tmp1 = CombThreeListData(
            1,
            10.00f,
            91.30f,
            "1·2",
            9.13f
        )
        val tmp2 = CombThreeListData(
            2,
            10.00f,
            112.90f,
            "1·3",
            11.29f
        )
        val tmp3 = CombThreeListData(
            3,
            10.00f,
            156.60f,
            "1·4",
            15.66f
        )
        val tmp4 = CombThreeListData(
            4,
            10.00f,
            71.30f,
            "2·3",
            7.13f
        )
        val tmp5 = CombThreeListData(
            5,
            10.00f,
            156.60f,
            "2·4",
            15.66f
        )
        val tmp6 = CombThreeListData(
            4,
            10.00f,
            71.30f,
            "3·4",
            7.13f
        )
        _onTwoListener.value = listOf(tmp1, tmp2, tmp3, tmp4, tmp5, tmp6)
    }
}