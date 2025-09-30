package arch.cayenne.module.chat.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.chat.data.constants.BidEmojiEnum
import arch.cayenne.module.chat.data.constants.EmojiEnum
import arch.cayenne.module.chat.data.model.EmojiData

class EmojiViewModel : BaseViewModel() {

    fun getNormalEmojis(): List<EmojiData> {
        val list = EmojiEnum.getEmojiMap().map {
            EmojiData(it.value, it.key)
        }.toMutableList()
        return list
    }

    fun getBidEmojis() = BidEmojiEnum.getEmojiMap().map {
        EmojiData(it.value, it.key)
    }.toList()

}