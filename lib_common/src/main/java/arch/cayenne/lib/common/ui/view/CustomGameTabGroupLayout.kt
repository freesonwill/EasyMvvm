package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.withStyledAttributes
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.databinding.ItemCustomGameTabBinding
import arch.cayenne.lib.common.databinding.ViewCustomGameTabGroupBinding
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.bumptech.glide.Glide

class CustomGameTabGroupLayout : FrameLayout {
    private lateinit var binding: ViewCustomGameTabGroupBinding
    constructor(context: Context) : super(context) {
        initView(context)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr) {
        initView(context, attrs)
    }

    var defaultIcon = R.drawable.ic_wali_demo
    var tabIconSize = 18.dp2px

    private fun initView(context: Context, attrs: AttributeSet? = null) {
        binding = ViewCustomGameTabGroupBinding.inflate(LayoutInflater.from(context), this, true)
        context.withStyledAttributes(attrs, R.styleable.CustomGameTabGroupLayout) {
            defaultIcon = getResourceId(R.styleable.CustomGameTabGroupLayout_defaultTabIcon, R.drawable.ic_wali_demo)
            tabIconSize = getDimensionPixelSize(R.styleable.CustomGameTabGroupLayout_tabIconSize, 18.dp2px)
        }
    }

    fun setOnShowAllCategoryClick(listener: () -> Unit) {
        binding.llBtnExpand.clickNoRepeat {
            listener.invoke()
        }
    }

    fun submitTabList(list : List<SimpleTabDataModel>) {
        with(binding) {
            list.forEachIndexed { i, data ->
                val tab = tlVendorList.newTab()

                tab.customView = createTabView(data)
                tab.view.setPadding(0, 0, 6f.dp2px, 0)
                if (data.id == 0) {
                    tab.view.minimumWidth = 0
                }
                tlVendorList.addTab(tab)
            }
        }
    }

    private fun createTabView(
        tabDataModel: SimpleTabDataModel
    ): View {
        val context = rootView.context
        val tabBinding =
            ItemCustomGameTabBinding.inflate(LayoutInflater.from(context), null, false)

        tabBinding.apply {
            tvName.text = if (tabDataModel.id == 0) {
                resources.getString(R.string.custom_tab_all)
            }else {
                tabDataModel.simpleName
            }

            if (tabDataModel.id == 0) {
                ivIcon.visibility = View.GONE
                // 設置「全部」tab 的寬度
                root.layoutParams = LinearLayout.LayoutParams(51.dp2px, 32.dp2px)
            } else {
                Glide.with(context)
                    .load(tabDataModel.icon.ifEmpty { R.drawable.ic_wali_demo })
                    .placeholder(R.drawable.ic_wali_demo)
                    .error(R.drawable.ic_wali_demo)
                    .into(ivIcon)
                ivIcon.layoutParams = LinearLayoutCompat.LayoutParams(tabIconSize, tabIconSize)
            }
            root.setBackgroundResource(R.drawable.selector_custom_game_tab_bg)
        }
        return tabBinding.root
    }


}

//TODO 先給一個簡單的
data class SimpleTabDataModel(
    val id: Int,
    val simpleName: String,
    val icon: String,
)