package arch.cayenne.lib.skin.data

/**
 * @author: wenxi
 * @date: 4/6/25 15:05
 * @description: 用于区分更新时是由skinflow发出的通知，还是自己调用
 */
enum class SkinMsgType {
    FLOW,
    SELF
}