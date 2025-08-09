package arch.cayenne.lib.common.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.fragment.app.Fragment


/**
 *
 * @date: 2025/8/9 15:14
 * @description:
 */
fun Fragment.copyToClipboard(textToCopy: String?, onSucceeded: () -> Unit) {
    // 获取 ClipboardManager
    val clipboard: ClipboardManager =
        requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager


    // 创建 ClipData 对象
    val clip = ClipData.newPlainText("Copied Text", textToCopy)

    // 将 ClipData 设置到剪贴板
    clipboard.setPrimaryClip(clip)

    onSucceeded.invoke()
}