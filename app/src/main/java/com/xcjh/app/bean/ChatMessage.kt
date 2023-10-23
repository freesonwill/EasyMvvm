package com.xcjh.app.bean


private const val currentUserId = 0 // 假设这是当前用户ID

data class ChatMessage(
    val content: CharSequence,
    val userId: Int,
    val avatar: String = "https://avatars.githubusercontent.com/u/21078112?v=4"
) {

    /** 渲染过后的消息 */
    fun getRichMessage(): CharSequence {
        var result: CharSequence = content
        val urlRule = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]"

        return result
    }

    fun isSelf(): Boolean {
        return currentUserId == userId
    }
}
