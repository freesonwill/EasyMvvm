package com.walisport.module.live.ui.viewmodel

import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import com.walisport.module.live.R
import com.walisport.module.live.data.BidEmojiEnum
import com.walisport.module.live.data.EmojiEnum
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