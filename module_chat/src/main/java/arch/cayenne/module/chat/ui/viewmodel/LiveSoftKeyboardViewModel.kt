package arch.cayenne.module.chat.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.constants.SportEnum
import arch.cayenne.lib.skin.LanguageManager
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.dao.ChatConfigDao
import arch.cayenne.module.chat.data.constants.BidEmojiEnum
import arch.cayenne.module.chat.data.constants.EmojiEnum
import arch.cayenne.module.chat.data.constants.EmojiTypeEnum
import arch.cayenne.module.chat.data.model.EmojiData
import arch.cayenne.module.chat.data.model.KeyBoardTabData
import arch.cayenne.module.chat.data.model.SoftData

class LiveSoftKeyboardViewModel : BaseViewModel() {

    val languageManager: LanguageManager by inject { parametersOf(viewModelScope) }
    val userDataManager: UserDataManager by inject()
    //聊天设置
     val chatConfigDao: ChatConfigDao by inject()
    fun tabMenus() =
        arrayListOf(
           KeyBoardTabData(
                normal = arch.cayenne.module.chat.R.drawable.input_emoji_grey,
                select = arch.cayenne.module.chat.R.drawable.input_emoji_color,
                id = 0
            ),
           KeyBoardTabData(
                normal = R.drawable.ic_soccer_inactive,
                select = SportEnum.Soccer.resId,
                id = 1
            ),
           KeyBoardTabData(
                normal = R.drawable.ic_volleyball_inactive,
                select = SportEnum.VolleyBall.resId,
                id = 2
            ),
           KeyBoardTabData(
                normal = R.drawable.ic_basketball_inactive,
                select = SportEnum.BasketBall.resId,
                id = 3
            ),
           KeyBoardTabData(
                normal = R.drawable.ic_badminton_inactive,
                select = SportEnum.Badminton.resId,
                id = 4
            ),
           KeyBoardTabData(
                normal = R.drawable.ic_pingpong_inactive,
                select = SportEnum.PingPang.resId,
                id = 5
            ),

           KeyBoardTabData(
                normal = R.drawable.ic_golf_inactive,
                select = SportEnum.Golf.resId,
                id = 7
            ),
           KeyBoardTabData(
                normal = R.drawable.ic_snooker_inactive,
                select = SportEnum.Snooker.resId,
                id = 8
            ),
           KeyBoardTabData(
                normal = R.drawable.ic_baseball_inactive,
                select = SportEnum.BaseBall.resId,
                id = 9
            ),
           KeyBoardTabData(
                normal = R.drawable.ic_handball_inactive,
                select = SportEnum.HandBall.resId,
                id = 10
            ),
           KeyBoardTabData(
                normal = R.drawable.ic_rugby_inactive,
                select = SportEnum.Rugby.resId,
                id = 11
            ),
           KeyBoardTabData(
                normal = R.drawable.ic_beach_volleyball_inactive,
                select = SportEnum.BeachVolleyBall.resId,
                id = 12
            ),
           KeyBoardTabData(
                normal = R.drawable.ic_pool_inactive,
                select = SportEnum.Pool.resId,
                id = 13
            ),
        )


    private fun getNormalEmojis(): List<EmojiData> {
        val list = EmojiEnum.getEmojiMap().map {
           EmojiData(it.value, it.key)
        }.toMutableList()
        return list
    }

    private fun getBidEmojis() = BidEmojiEnum.getEmojiMap().map {
       EmojiData(it.value, it.key)
    }.toList()

    fun softData(): List<arch.cayenne.module.chat.data.model.SoftData> {
        val list: MutableList<arch.cayenne.module.chat.data.model.SoftData> = mutableListOf()
        for (i in 0..<tabMenus().size) {
            if (i == 0) list.add(
               SoftData(
                    EmojiTypeEnum.NORMAL,
                    getNormalEmojis()
                )
            ) else list.add(
               SoftData(
                    EmojiTypeEnum.BID,
                    getBidEmojis()
                )
            )
        }
        return list
    }



}
