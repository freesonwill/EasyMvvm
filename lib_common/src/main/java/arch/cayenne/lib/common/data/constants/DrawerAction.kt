package arch.cayenne.lib.common.data.constants

/**
 * @author: ricky.chang
 * @date: 2025/10/16 下午2:10
 * @description:
 */
object DrawerAction {
    // 定義一個唯一的 key 來識別請求
    const val REQUEST_KEY_DRAWER = "request_drawer_control"
    // 定義 bundle 裡的 key 來識別具體操作
    const val KEY_ACTION = "drawer_action"
    const val ACTION_OPEN = "open"
    const val ACTION_CLOSE = "close"
    const val ACTION_INIT = "init"
}