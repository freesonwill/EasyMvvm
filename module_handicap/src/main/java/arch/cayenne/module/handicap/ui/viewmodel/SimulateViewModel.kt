package arch.cayenne.module.handicap.ui.viewmodel

import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.module.handicap.data.SimulateBean

class SimulateViewModel : BaseViewModel() {

    fun getFlipperData(): List<SimulateBean> {
        val tmp1 =
            SimulateBean(
                1,
                "让球",
                "1、如果出现该比赛结果，哪个选项主队能赢一半？",
                "1-0",
                "皇马",
                "利物浦",
                "-0.5/1",
                "-1",
                "“-”表示让球，“+”表示受让，根据以上赛果，利物浦获得受让分后，让分结果为：1-0.5或1-1；1-0.5时，皇马获胜；1-1时为和局；一半赛果为皇马获胜，故投注皇马（-0.5/1）为赢一半",
                "“-”表示让球，“+”表示受让，根据以上赛果，若进球数为1时，双方结果则为1-1，故为和局；故投注皇马（-1）为退回本金",
                "下一题（1/3）"
            )
        val tmp2 =
            SimulateBean(
                2,
                "大小球",
                "2、如果出现该比赛结果，哪个选项能全赢？",
                "2-1",
                "皇马",
                "利物浦",
                "大2.5",
                "小2.5",
                "根据以上赛果，进球数之后3，大于2.5，故投注（大于2.5）能全赢",
                "根据以上赛果，进球数之后3，大于2.5，故投注（小于2.5）能全赢",
                "下一题（2/3）"
            )
        val tmp3 =
            SimulateBean(
                3,
                "角球-大小",
                "3、如果出现该比赛结果，哪个选项输一半？",
                "8-5",
                "皇马",
                "利物浦",
                "大13/13.5",
                "大13.5",
                "根据以上赛果，角球数之和13，大于13时则和局，大于13.5时全输，一半赛果为输一半，故投注（大于13/13.5）为输一半",
                "根据以上赛果，角球数之和13，大于13时则和局；大于13.5时全输；一半赛果为输一半，故投注（大于13）为全输；投注（大于13/13.5）为输一半",
                "去投注"
            )
        return listOf(tmp1, tmp2, tmp3)
    }
}