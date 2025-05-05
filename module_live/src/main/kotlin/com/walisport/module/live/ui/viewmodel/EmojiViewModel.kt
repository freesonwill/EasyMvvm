package com.walisport.module.live.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.live.R
import com.walisport.module.live.data.constants.BidEmojiEnum
import com.walisport.module.live.data.constants.EmojiEnum
import com.walisport.module.live.data.model.EmojiData

class EmojiViewModel : BaseViewModel() {

    fun getNormalEmojis(): List<EmojiData> {
        val list = EmojiEnum.getEmojiMap().map {
            EmojiData(it.value, it.key)
        }.toMutableList()
        list.add(EmojiData(R.drawable.icon_emoji_del, "del"))
        return list
    }

    fun getBidEmojis() = BidEmojiEnum.getEmojiMap().map {
        EmojiData(it.value, it.key)
    }.toList()

}