package arch.cayenne.module.handicap.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.handicap.data.CornerBallBean

class HandicapCornerViewModel : BaseViewModel() {

    private val _cornerBallData = MutableLiveData<List<CornerBallBean>>()
    val cornerBallData: LiveData<List<CornerBallBean>> = _cornerBallData

    fun addCornerBallData() {
        val tmp1 = CornerBallBean(
            1,
            "角球-让球",
            8,
            5,
            "-2",
            "+2",
            "全赢",
            "全输",
            "投注-2全赢\n投注+2全输"
        )
        val tmp2 = CornerBallBean(
            2,
            "角球-大小",
            8,
            5,
            "大12.5",
            "小12.5",
            "全赢",
            "全输",
            "投注大12.5全赢\n投注小12.5全输"
        )
        val tmp3 = CornerBallBean(
            3,
            "角球-单双",
            8,
            5,
            "单",
            "双",
            "全赢",
            "全输",
            "投注单全赢\n投注双全输"
        )
        val tmp4 = CornerBallBean(
            4,
            "角球-独赢",
            8,
            5,
            "-主胜",
            "客胜",
            "全赢",
            "全输",
            "投注主胜全赢\n投注和局、客胜双全输"
        )
        val list = listOf(tmp1, tmp2, tmp3, tmp4)
        _cornerBallData.value = list
    }
}