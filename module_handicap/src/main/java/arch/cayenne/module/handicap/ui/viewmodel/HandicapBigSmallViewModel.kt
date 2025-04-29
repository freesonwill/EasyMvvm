package arch.cayenne.module.handicap.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.handicap.data.BigSmallBean
import arch.cayenne.module.handicap.data.BigSmallItem

class HandicapBigSmallViewModel : BaseViewModel() {

    private val _bigSmallData = MutableLiveData<List<BigSmallBean>>()
    val bigSmallData: LiveData<List<BigSmallBean>> = _bigSmallData

    fun addLetBallData() {
        val tmp1 = BigSmallItem(
            1,
            "角球-大小",
            3,
            0,
            "大2.5",
            "小2.5",
            "全赢",
            "全输",
            "投注大2.5全赢\n投注小2.5全输"
        )
        val tmp2 = BigSmallItem(
            2,
            "角球-大小",
            3,
            0,
            "大2.5/3",
            "小2.5/3",
            "赢一半",
            "输一半",
            "投注大2.5/3赢一半\n投注小2.5/3输一半"
        )
        val tmp3 = BigSmallItem(
            3,
            "角球-大小",
            3,
            0,
            "大3",
            "小3",
            "赢一半",
            "输一半",
            "投注大3或小3\n均退回本金"
        )
        val tmp4 = BigSmallItem(
            4,
            "角球-大小",
            3,
            0,
            "大3/3.5",
            "小3/3.5",
            "输一半",
            "赢一半",
            "投注大2.5/3赢一半\n投注小2.5/3输一半"
        )
        val tmp5 = BigSmallItem(
            5,
            "角球-大小",
            3,
            0,
            "大3.5",
            "小3.5",
            "全输",
            "全赢",
            "投注大3.5全输 \n投注小3.5全赢"
        )

        val list = listOf(tmp1, tmp2, tmp3, tmp4, tmp5)
        val tmp = BigSmallBean(
            1,
            "大小球", "(全场+伤停补时的进球之和)", list
        )
        val array = listOf(tmp)
        _bigSmallData.value = array
    }
}