package arch.cayenne.module.home.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import arch.cayenne.lib.common.utils.ext.DimensionExt.px2sp
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.ViewDrawerItemBinding

/**
 * 自訂 View 類別，封裝了 item_sport.xml 佈局並使用 View Binding。
 *
 * 這個 View 繼承自 LinearLayout，因為 XML 的根元素是 LinearLayout。
 * 它在初始化時加載 (inflate) item_sport.xml 佈局，
 * 並提供方法來設置圖標和標題。
 */
class DrawerItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    // 持有 View Binding 生成的綁定類別實例
    // lateinit var 或 private val + init block 都可以，這裡使用後者
    private val binding: ViewDrawerItemBinding
    private var textSize: Float =13.px2sp
    init {
        val inflater = LayoutInflater.from(context)

        binding = ViewDrawerItemBinding.inflate(inflater, this, true)

        orientation = VERTICAL
        gravity = android.view.Gravity.CENTER

        attrs?.let {
            // 獲取在 attrs.xml 中定義的 SportItemView 屬性集合
            val typedArray = context.obtainStyledAttributes(
                it, // AttributeSet
                R.styleable.DrawerItemView, // 要讀取的 styleable 資源 ID
                defStyleAttr, // 預設樣式屬性 (通常是 0)
                0 // 預設樣式資源 (通常是 0)
            )

            // 使用 try-finally 確保 typedArray 一定會被回收
            try {
                // 讀取 sportIcon 屬性 (Drawable 資源 ID)
                val iconResId = typedArray.getResourceId(R.styleable.DrawerItemView_drawerIcon, 0) // 0 是預設值
                if (iconResId != 0) { // 檢查是否提供了有效的資源 ID
                    binding.tvSportIcon.setImageResource(iconResId)
                }

                // 讀取 sportTitle 屬性 (String)
                val titleText = typedArray.getString(R.styleable.DrawerItemView_drawerTitle)
                // 如果 XML 提供了文字，就設定它；否則使用 XML 佈局 (item_sport.xml) 中的預設文字
                if (titleText != null) {
                    binding.tvSportTitle.text = titleText
                }
                // 你也可以提供一個程式碼中的預設值，例如：
                // binding.tvSportTitle.text = titleText ?: "預設標題"
                val drawTxtSize = typedArray.getDimension(R.styleable.DrawerItemView_item_text_size, textSize)
                if (drawTxtSize != null && drawTxtSize > 0) {
                    textSize = px2sp(drawTxtSize)
                }
                binding.tvSportTitle.textSize = textSize
                val drawerIconWidth = typedArray.getDimensionPixelSize(R.styleable.DrawerItemView_drawerIconWidth, 0)
                if (drawerIconWidth != null && drawerIconWidth > 0) {
                    binding.tvSportIcon.layoutParams.width = drawerIconWidth
                }
                val drawerIconHeight = typedArray.getDimensionPixelSize(R.styleable.DrawerItemView_drawerIconHeight, 0)
                if (drawerIconHeight != null && drawerIconHeight > 0) {
                    binding.tvSportIcon.layoutParams.height = drawerIconHeight
                }
                val drawerTitleHeight = typedArray.getDimensionPixelSize(R.styleable.DrawerItemView_drawerTitleHeight, 0)
                if (drawerTitleHeight != null && drawerTitleHeight > 0) {
                    binding.tvSportTitle.layoutParams.height = drawerTitleHeight
                }
            } finally {
                // 非常重要：讀取完畢後必須回收 TypedArray 以避免資源洩漏
                typedArray.recycle()
            }
        }
        // --- 自訂屬性處理結束 ---
    }

    private fun px2sp(px: Float): Float {
        val scale = resources.displayMetrics.scaledDensity
        return px / scale
    }
}