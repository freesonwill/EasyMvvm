package com.walisport.module.live.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
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
                select = R.drawable.soccer_color,
                id = 1
            ),
            KeyBoardTabData(
                normal = R.drawable.volleyball_grey,
                select = R.drawable.volleyball_color,
                id = 2
            ),
            KeyBoardTabData(
                normal = R.drawable.basketball_grey,
                select = R.drawable.basketball_color,
                id = 3
            ),
            KeyBoardTabData(
                normal = R.drawable.badminton_grey,
                select = R.drawable.badminton_color,
                id = 4
            ),
            KeyBoardTabData(
                normal = R.drawable.pingpang_grey,
                select = R.drawable.pingpang_color,
                id = 5
            ),

            KeyBoardTabData(normal = R.drawable.golf_grey, select = R.drawable.golf_color, id = 7),
            KeyBoardTabData(
                normal = R.drawable.snooker_grey,
                select = R.drawable.snooker_color,
                id = 8
            ),
            KeyBoardTabData(
                normal = R.drawable.baseball_grey,
                select = R.drawable.baseball_color,
                id = 9
            ),
            KeyBoardTabData(
                normal = R.drawable.handball_grey,
                select = R.drawable.handball_color,
                id = 10
            ),
            KeyBoardTabData(
                normal = R.drawable.rugby_grey,
                select = R.drawable.rugby_color,
                id = 11
            ),
            KeyBoardTabData(
                normal = R.drawable.beach_volleyball_grey,
                select = R.drawable.beach_volleyball_color,
                id = 12
            ),
            KeyBoardTabData(normal = R.drawable.pool_grey, select = R.drawable.pool_color, id = 13),
        )


}
