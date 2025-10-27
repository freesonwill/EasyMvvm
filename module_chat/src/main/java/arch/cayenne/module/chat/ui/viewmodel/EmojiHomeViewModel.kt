package arch.cayenne.module.chat.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.chat.R
import arch.cayenne.module.chat.data.constants.EmojiEnum
import arch.cayenne.module.chat.data.model.EmojiData


/**
 * @author: wenxi
 * @date: 22/10/25 11:34
 * @description:
 */
class EmojiHomeViewModel : BaseViewModel() {


    fun getHotRecycler(): List<EmojiData> {
        return arrayOf(
            EmojiEnum.Gin,
            EmojiEnum.Smile,
            EmojiEnum.Boring,
            EmojiEnum.Scrowl,
            EmojiEnum.Dizzy
        ).map {
            EmojiData(it.resId, it.key)
        }.toList()
    }

}