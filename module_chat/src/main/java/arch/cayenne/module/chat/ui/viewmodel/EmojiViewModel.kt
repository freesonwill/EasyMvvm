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

    private fun getNormalEmojis(): List<EmojiData> {
        val list = EmojiEnum.getEmojiMap().map {
            EmojiData(it.value, it.key)
        }.toMutableList()
        return list
    }

    private fun getBidEmojis() = BidEmojiEnum.getEmojiMap().map {
        EmojiData(it.value, it.key)
    }.toList()

    fun getNormalList(): List<List<EmojiData>> {
        val normalRecentList = getNormalEmojis().subList(0, 8).toList()
        return arrayListOf(normalRecentList, getNormalEmojis())
    }


    fun getBidList(): List<List<EmojiData>> {
        val bidRecentList = getBidEmojis().subList(0, 4).toList()
        return arrayListOf(bidRecentList, getBidEmojis())
    }

}