package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.withStyledAttributes
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.databinding.ItemCustomGameTabBinding
import arch.cayenne.lib.common.databinding.ViewCustomGameTabGroupBinding
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.TabLayoutExt
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.addOnTabSelectedListener2

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

    private var tabClickListener: CustomGameTabClickListener? = null


    private fun initView(context: Context, attrs: AttributeSet? = null) {
        binding = ViewCustomGameTabGroupBinding.inflate(LayoutInflater.from(context), this, true)
        context.withStyledAttributes(attrs, R.styleable.CustomGameTabGroupLayout) {
            defaultIcon = getResourceId(
                R.styleable.CustomGameTabGroupLayout_defaultTabIcon,
                R.drawable.ic_wali_demo
            )
            tabIconSize =
                getDimensionPixelSize(R.styleable.CustomGameTabGroupLayout_tabIconSize, 18.dp2px)
        }
    }

    fun setOnShowAllCategoryClick(listener: () -> Unit, listener2: () -> Unit) {

        binding.llBtnExpand.apply { addScaleOnTouchAnimation() }
            .clickNoRepeat {
                // toggleTournamentMoreSection(true, TournamentListType.MORE)

                listener2.invoke()
            }
        binding.tlVendorList.addOnTabSelectedListener2(object :
            TabLayoutExt.OnTabSelectedListener2 {

            override fun onTabSelected(tab: TabLayout.Tab, isTabClick: Boolean) {
                updateTournamentButtonStyle(false)
                tabClickListener?.onTabClicked(tab.id)
            }

            override fun onTabUnselected(tab: TabLayout.Tab, isTabClick: Boolean) {

            }

            override fun onTabReselected(tab: TabLayout.Tab, isTabClick: Boolean) {
                // Handle reselect if needed
            }
        })
    }

    fun setOnSortBtnClick(listener: () -> Unit) {
        binding.llBtnSort.clickNoRepeat {
            listener.invoke()
        }
    }

    fun setSortBtnSrc(resId: Int) {
        binding.ivBtnSort.setImageResource(resId)
    }

    fun setSortBtnTextColor(color: Int) {
        binding.tvBtnSort.setTextColor(color)
    }

    fun setSortBtnText(text: String) {
        binding.tvBtnSort.text = text
    }

    /**
     * 更新聯賽按鈕樣式
     * @param hasSelection true: 有選中的聯賽，false: 沒有選中的聯賽
     */
    fun updateTournamentButtonStyle(hasSelection: Boolean) {
        LogUtils.e("updateTournamentButtonStyle--------->${hasSelection}")
        with(binding) {
            if (hasSelection) {
                // 有選中狀態：高亮顯示
                llBtnExpand.setBackgroundResource(R.drawable.selector_league_tab_bg_supplier)
                llBtnExpand.isSelected = true
                tvBtnExpand.setTextColor(
                    SkinnableResourceManager.getColor(
                        binding.root.context,
                        R.color.league_tab_tint_select
                    )
                )
                ivBtnIcon.setImageResource(R.drawable.ic_tournament_list_selected)
            } else {
                // 無選中狀態：默認樣式
                llBtnExpand.setBackgroundResource(R.drawable.shape_tournament_more_bg)
                llBtnExpand.isSelected = false
                tvBtnExpand.setTextColor(
                    SkinnableResourceManager.getColor(
                        binding.root.context,
                        arch.cayenne.lib.common.R.color.color_C0C0C0
                    )
                )
                ivBtnIcon.setImageResource(R.drawable.ic_tournament_list)
            }
        }
    }

    fun submitTabList(list: List<SimpleTabDataModel>) {
        with(binding) {
            list.forEachIndexed { i, data ->
                val tab = tlVendorList.newTab()
                tab.id = data.id

                tab.customView = createTabView(data)
                tab.view.setPadding(0, 0, 6f.dp2px, 0)
                if (data.id == 0) {
                    tab.view.minimumWidth = 0
                }
                tlVendorList.addTab(tab)
            }
        }
    }

    fun clearTabList() {
        binding.tlVendorList.removeAllTabs()
    }


    /**
     * 清除 tlLeagueList 的選中狀態（需求2）
     */
    fun clearLeagueListSelection() {
        with(binding) {
            // 清除所有 tab 的選中狀態
            val tabLayout = tlVendorList
            for (i in 0 until tabLayout.tabCount) {
                tabLayout.getTabAt(i)?.let { tab ->
                    tab.customView?.isSelected = false
                }
            }
            // 不設置任何 tab 為選中
            tabLayout.selectTab(null)
        }
    }

    fun select(pos: Int) {
        binding.tlVendorList.getTabAt(pos)?.select()
    }

    fun selectById(id: Int) {
        var isSelect: Boolean = true
        with(binding) {
            // 清除所有 tab 的選中狀態
            val tabLayout = tlVendorList
            for (i in 0 until tabLayout.tabCount) {
                tabLayout.getTabAt(i)?.let { tab ->
                    if (tab.id==id){
                        isSelect = false
                        tabLayout.selectTab(tab)
                    }
                }
            }
        }
        updateTournamentButtonStyle(isSelect)
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
            } else {
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


    fun setTabClickListener(listener: CustomGameTabClickListener) {
        this.tabClickListener = listener
    }

}


interface CustomGameTabClickListener {
    fun onTabClicked(id: Int)
}

//TODO 先給一個簡單的
data class SimpleTabDataModel(
    val id: Int,
    val simpleName: String,
    val icon: String,
)