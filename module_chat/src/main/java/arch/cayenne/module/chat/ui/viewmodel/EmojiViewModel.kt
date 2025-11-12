package arch.cayenne.module.chat.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.chat.data.constants.BidEmojiEnum
import arch.cayenne.module.chat.data.constants.EmojiEnum
import arch.cayenne.module.chat.data.model.EmojiData

/**
 * @author: wenxi
 * @date: 23/10/25 17:20
 * @description:
 */
class EmojiViewModel : BaseViewModel() {

    private fun getNormalEmojis(): MutableList<EmojiData> {
        val list = EmojiEnum.getEmojiMap().map {
            EmojiData(it.value, it.key)
        }.toMutableList()
        return list
    }

    private fun getBidEmojis() = BidEmojiEnum.getEmojiMap().map {
        EmojiData(it.value, it.key)
    }.toMutableList()

    fun getNormalList(): List<EmojiData> {
        val list = getNormalEmojis()
        list.add(0,EmojiData(-1, ""))
        return list
    }


    fun getBidList(): List<EmojiData> {
        val list = getBidEmojis()
        list.add(0,EmojiData(-1, ""))
        return  list
    }



}