package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.SportEnum
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.skin.LanguageManager
import com.walisport.module.live.R
import com.walisport.module.live.data.constants.BidEmojiEnum
import com.walisport.module.live.data.constants.EmojiEnum
import com.walisport.module.live.data.constants.EmojiTypeEnum
import com.walisport.module.live.data.constants.KeyBoardType
import com.walisport.module.live.data.model.EmojiData
import com.walisport.module.live.data.model.KeyBoardTabData
import com.walisport.module.live.data.model.SoftData
import com.walisport.module.live.ui.LiveChatFragment
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class LiveSoftKeyboardViewModel : BaseViewModel() {

    val languageManager:LanguageManager by inject { parametersOf(viewModelScope) }

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


   private fun getNormalEmojis(): List<EmojiData> {
        val list = EmojiEnum.getEmojiMap().map {
            EmojiData(it.value, it.key)
        }.toMutableList()
        return list
    }

   private fun getBidEmojis() = BidEmojiEnum.getEmojiMap().map {
        EmojiData(it.value, it.key)
    }.toList()

    fun softData():List<SoftData>{
        return arrayListOf(SoftData(EmojiTypeEnum.NORMAL,getNormalEmojis()),SoftData(EmojiTypeEnum.BID,getBidEmojis()))
    }

    val CHAT_TO_EMOJI:Int = 1
    val CHAT_TO_SOFT:Int = 2
    val SOFT_TO_EMOJI:Int = 3
    val EMOJI_TO_SOFT:Int = 4
    val EMOJI_TO_CHAT:Int = 5
    val SOFT_TO_CHAT:Int = 6
    val CHAT_TO_CHAT:Int = 7

    /**
     * 判断动画类型
     * */
     fun getAnimationType(listenerValue:KeyBoardType,currentValue:KeyBoardType):Int{

        return  when(currentValue){
            KeyBoardType.CHAT ->{
                return when(listenerValue){
                    KeyBoardType.EMOJI -> CHAT_TO_EMOJI
                    KeyBoardType.SOFT_KEYBOARD -> CHAT_TO_SOFT
                    KeyBoardType.CHAT -> CHAT_TO_CHAT
                }
            }
            KeyBoardType.SOFT_KEYBOARD ->{
                return when(listenerValue){
                    KeyBoardType.CHAT -> SOFT_TO_CHAT
                    KeyBoardType.EMOJI -> SOFT_TO_EMOJI
                    KeyBoardType.SOFT_KEYBOARD -> -1
                }
            }
            KeyBoardType.EMOJI ->{
                return when(listenerValue){
                    KeyBoardType.CHAT -> EMOJI_TO_CHAT
                    KeyBoardType.SOFT_KEYBOARD -> EMOJI_TO_SOFT
                    KeyBoardType.EMOJI -> -1
                }
            }
        }
    }


}
