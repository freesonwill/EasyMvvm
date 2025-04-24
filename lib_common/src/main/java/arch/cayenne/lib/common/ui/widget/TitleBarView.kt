package arch.cayenne.lib.common.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.contains
import arch.cayenne.lib.common.databinding.TittleBarDefaultBinding
import arch.cayenne.lib.common.databinding.TittleBarDynamicsBinding
import arch.cayenne.lib.common.databinding.TittleBarSearchBinding
import arch.cayenne.lib.common.utils.ext.clickNoRepeat

class TitleBarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Toolbar(context, attrs, defStyleAttr) {

    /**
     * 通用标题
     * @param titleName 标题名称
     * @param callback 返回
     */
    fun loadGeneralTitleBar(
        titleName: String,
        callback: () -> Unit,
        callbackRight: (() -> Unit)? = null,
        rightName: String? = null
    ) {
        val binding = TittleBarDefaultBinding.inflate(LayoutInflater.from(context), this, true)
        binding.apply {
            tvTitleName.text = titleName
            ivBack.clickNoRepeat {
                callback()
            }
            if (callbackRight != null) {
                tvTitleRight.visibility = VISIBLE
                tvTitleRight.text = rightName ?: ""
                tvTitleRight.clickNoRepeat {
                    callbackRight()
                }
            }
        }
    }


    /**
     * 搜索标题
     * @param hintText 搜索框提示
     * @param callback 返回
     * @param callbackSearch 搜索
     */
    fun loadSearchTitleBar(
        hintText: String, callback: () -> Unit, callbackSearch: (String) -> Unit
    ) {
        val binding = TittleBarSearchBinding.inflate(LayoutInflater.from(context), this, true)
        binding.apply {
            ceSearch.hint = hintText
            ivBack.clickNoRepeat {
                callback()
            }
            tvSearchText.clickNoRepeat {
                //如果输入内容为空，传入hint内容
                callbackSearch(ceSearch.text.toString().ifEmpty { hintText })
                //hint text 为空 提示请输入搜索内容
//                if (hintText.isEmpty() && ceSearch.text.toString().isEmpty()) {
//                    callbackSearch( ceSearch.text.toString())
//                } else {
//                }
            }
        }
    }

    /**
     * 动态标题
     * @param view 传入布局view
     * @param callback 返回 不传入Unit 默认不显示ivBack
     */
    fun loadDynamicsTitleBar(view: ViewGroup, callback: (() -> Unit)? = null) {
        val binding = TittleBarDynamicsBinding.inflate(LayoutInflater.from(context), this, true)
        if (callback != null) {
            binding.ivBack.visibility = VISIBLE
            binding.ivBack.clickNoRepeat {
                callback()
            }
        }
        if (!binding.clDynamics.contains(view)){
            binding.clDynamics.addView(view)
        }
    }
}


///**
// * 直播标题
// * @param leagueImgUrl 联赛图片
// * @param competitionName 联赛对站 A VS B
// * @param money 剩余总金额
// * @param callback 返回
// * @param expand 展开还是收起
// * @param callbackCompetition 下拉切换 boolean 单前状态展开还是收起
// */
//@SuppressLint("SetTextI18n")
//fun loadLiveTitleBar(
//    leagueImgUrl: String,
//    competitionName: String,
//    expand: Boolean,
//    money: String,
//    callback: () -> Unit,
//    callbackCompetition: (Boolean) -> Unit
//) {
//    val binding = TittleBarLiveBinding.inflate(LayoutInflater.from(context), this, true)
//    Glide.with(context).load(leagueImgUrl).override(96.dp2px, 22.dp2px)
//        .error(R.drawable.title_league_icon)           // 加载失败时的占位符
//        .into(binding.ivLandscapeLeagueIcon)
//    binding.apply {
//        tvCompetitionName.text = competitionName
//        tvMoney.text = "¥ $money"
//        ivBack.clickNoRepeat {
//            callback()
//        }
//        tvCompetitionName.clickNoRepeat {
//            callbackCompetition(!expand)
//        }
//    }
//}

//}