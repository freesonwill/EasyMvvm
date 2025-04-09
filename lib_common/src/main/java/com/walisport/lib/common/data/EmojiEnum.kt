package com.walisport.lib.common.data


enum class EmojiEnum(val key: String, val resId: Int) {
//TODO 服务器还没定好，emoji对应id，暂时不添加表情
    Boring("[boring]",-1),
    Dizzy("[dizzy]", -1),
    Duh("[duh]", -1),
    Grin("[grin]", -1),
    Scrolw("[scrolw]",-1),
    Smile("[smile]",-1);

    companion object {
        private val _map: MutableMap<String, Int> = mutableMapOf()
        fun getEmojiMap(): Map<String, Int> {
            entries.forEach {
                _map[it.key] = it.resId
            }
            return _map
        }
    }

}