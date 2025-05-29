package com.walisport.module.live.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.constants.SportEnum
import com.walisport.module.live.R
import com.walisport.module.live.data.model.KeyBoardTabData

class LiveSoftKeyboardViewModel : BaseViewModel() {

    fun tabMenus() =
        arrayListOf(
            KeyBoardTabData(
                normal = R.drawable.input_emoji_grey,
                select = R.drawable.input_emoji_color,
                id = 0
            ),
            KeyBoardTabData(
                normal = R.drawable.soccer_grey,
                select = SportEnum.Soccer.resId,
                id = 1
            ),
            KeyBoardTabData(
                normal = R.drawable.volleyball_grey,
                select = SportEnum.VolleyBall.resId,
                id = 2
            ),
            KeyBoardTabData(
                normal = R.drawable.basketball_grey,
                select = SportEnum.BasketBall.resId,
                id = 3
            ),
            KeyBoardTabData(
                normal = R.drawable.badminton_grey,
                select = SportEnum.Badminton.resId,
                id = 4
            ),
            KeyBoardTabData(
                normal = R.drawable.pingpang_grey,
                select = SportEnum.PingPang.resId,
                id = 5
            ),

            KeyBoardTabData(normal = R.drawable.golf_grey, select = SportEnum.Golf.resId, id = 7),
            KeyBoardTabData(
                normal = R.drawable.snooker_grey,
                select = SportEnum.Snooker.resId,
                id = 8
            ),
            KeyBoardTabData(
                normal = R.drawable.baseball_grey,
                select = SportEnum.BaseBall.resId,
                id = 9
            ),
            KeyBoardTabData(
                normal = R.drawable.handball_grey,
                select = SportEnum.HandBall.resId,
                id = 10
            ),
            KeyBoardTabData(
                normal = R.drawable.rugby_grey,
                select = SportEnum.FootBall.resId,
                id = 11
            ),
            KeyBoardTabData(
                normal = R.drawable.beach_volleyball_grey,
                select = SportEnum.BeachVolleyBall.resId,
                id = 12
            ),
            KeyBoardTabData(normal = R.drawable.pool_grey, select = SportEnum.Pool.resId, id = 13),
        )


}
