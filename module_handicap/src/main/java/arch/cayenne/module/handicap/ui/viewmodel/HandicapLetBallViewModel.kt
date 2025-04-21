package arch.cayenne.module.handicap.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.module.handicap.data.LetBallBean
import arch.cayenne.module.handicap.data.LetBallItem

class HandicapLetBallViewModel : BaseViewModel() {

    private val _letBallData = MutableLiveData<List<LetBallBean>>()
    val letBallData: LiveData<List<LetBallBean>> = _letBallData

    fun addLetBallData() {
        val m1 = LetBallItem(
            1,
            "角球-独赢",
            1,
            0,
            "全赢",
            "全输",
            "均不让球，主队全赢"
        )
        val m2 = LetBallItem(
            2,
            "角球-独赢",
            0,
            0,
            "退本金",
            "退本金",
            "打平则退回本金"
        )
        val list0 = listOf(m1, m2)
        val tmp0 = LetBallBean(
            3,
            "0平手盘", "(主队让0球)", list0
        )
        val a1 = LetBallItem(
            1,
            "角球-独赢",
            1,
            0,
            "全赢",
            "全输",
            "主队赢一球以上\n主队全赢、客队全输"
        )
        val a2 = LetBallItem(
            2,
            "角球-独赢",
            0,
            0,
            "输一半",
            "赢一半",
            "两队打平\n主队输一半，客队赢一半"
        )
        val a3 = LetBallItem(
            3,
            "角球-独赢",
            0,
            1,
            "全输",
            "全赢",
            "主队输球\n主队全输、客队全赢"
        )
        val list1 = listOf(a1, a2, a3)
        val tmp1 = LetBallBean(
            2,
            "0/0.5 平手/半球盘", "(主队让0/0.5球)", list1
        )
        val b1 = LetBallItem(
            1,
            "角球-独赢",
            1,
            0,
            "全赢",
            "全输",
            "主队全赢、客队全输"
        )
        val b2 = LetBallItem(
            2,
            "角球-独赢",
            0,
            0,
            "全赢",
            "全输",
            "两队打平或主队输球\n主队全输，客队全赢"
        )
        val list2 = listOf(b1, b2)
        val tmp2 = LetBallBean(
            3,
            "0/0.5半球盘", "(主队让0.5球)", list2
        )
        val c1 = LetBallItem(
            1,
            "角球-独赢",
            1,
            0,
            "赢一半",
            "输一半",
            "主队赢1球\n主队赢一半、客队输一半"
        )
        val c2 = LetBallItem(
            2,
            "角球-独赢",
            2,
            0,
            "全赢",
            "全输",
            "主队赢2球以上\n主队全赢，客队全输"
        )
        val c3 = LetBallItem(
            3,
            "角球-独赢",
            0,
            0,
            "全赢",
            "全输",
            "两队打平或主队输球\n主队全输，客队全赢"
        )
        val list3 = listOf(c1, c2, c3)
        val tmp3 = LetBallBean(
            3,
            "0.5/1半球/一球盘", "(主队让0.5/1球)", list3
        )
        val bean1 = LetBallItem(
            2,
            "角球-独赢",
            1,
            0,
            "全赢",
            "全输",
            "主队赢2球或以上\n主队全赢、客队全输"
        )
        val bean2 = LetBallItem(
            2,
            "角球-独赢",
            1,
            0,
            "退本金",
            "退本金",
            "主队赢1球\n主队和客队均退回本金"
        )
        val bean3 = LetBallItem(
            3,
            "角球-独赢",
            0,
            0,
            "全赢",
            "全输",
            "两队打平或主队输球\n主队全输，客队全赢"
        )
        val list4 = listOf(bean1, bean2, bean3)
        val tmp4 = LetBallBean(
            4,
            "1/一球盘", "(主队让1球)", list4
        )
        val array = listOf(tmp0, tmp1, tmp2, tmp3, tmp4)
        _letBallData.value = array
    }
}