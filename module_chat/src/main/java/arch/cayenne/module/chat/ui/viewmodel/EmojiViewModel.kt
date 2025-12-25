package arch.cayenne.module.chat.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.chat.data.constants.BidEmojiEnum
import arch.cayenne.module.chat.data.constants.EmojiEnum
import arch.cayenne.module.chat.data.model.EmojiModel

/**
 * @author: wenxi
 * @date: 23/10/25 17:20
 * @description:
 */
class EmojiViewModel : BaseViewModel() {

    private fun getNormalEmojis(): MutableList<EmojiModel> {
        val list = EmojiEnum.getEmojiMap().map {
            EmojiModel(it.value, it.key)
        }.toMutableList()
        return list
    }

    private fun getBidEmojis() = BidEmojiEnum.getEmojiMap().map {
        EmojiModel(it.value, it.key)
    }.toMutableList()

    fun getNormalList(): List<EmojiModel> {
        val list = getNormalEmojis()
        list.add(0,EmojiModel(-1, ""))
        list.add(EmojiModel(-2, ""))
        list.add(EmojiModel(-2, ""))
        list.add(EmojiModel(-2, ""))
        list.add(EmojiModel(-2, ""))
        list.add(EmojiModel(-2, ""))
        list.add(EmojiModel(-2, ""))

        return list
    }


    fun getBidList(): List<EmojiModel> {
        val list = getBidEmojis()
        list.add(0,EmojiModel(-1, ""))
        list.add(EmojiModel(-2, ""))
        list.add(EmojiModel(-2, ""))
        list.add(EmojiModel(-2, ""))
        return  list
    }



}