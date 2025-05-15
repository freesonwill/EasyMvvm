package arch.cayenne.lib.common.ui.view

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import arch.cayenne.lib.common.databinding.TitleBarDefaultBinding
import arch.cayenne.lib.common.databinding.TitleBarDynamicsBinding
import arch.cayenne.lib.common.databinding.TitleBarSearchBinding
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.requireActivity

class TitleBarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Toolbar(context, attrs, defStyleAttr) {
    //默认返回
    private  val defaultOnBack by lazy {
        { requireActivity().onBackPressedDispatcher.onBackPressed() }
    }
    /**
     * 通用标题
     * @param title 标题名称
     * @param onBack 返回
     */
    fun loadGeneralTitleBar(
        title: String?,
        onBack: () -> Unit = defaultOnBack,
        onRight: (() -> Unit)? = null,
        rightName: String? = null
    ) {
        val binding = TitleBarDefaultBinding.inflate(LayoutInflater.from(context), this, true)
        binding.apply {
            tvTitleName.text = title
            ivBack.clickNoRepeat {
                onBack()
            }
            if (onRight != null) {
                tvTitleRight.visibility = VISIBLE
                tvTitleRight.text = rightName ?: ""
                tvTitleRight.clickNoRepeat {
                    onRight()
                }
            }
        }
    }

    /**
     * 搜索标题
     * @param hint 搜索框提示
     * @param onBack 返回
     * @param onSearch 搜索
     */
    fun loadSearchTitleBar(
        hint: String,
        onBack: () -> Unit = defaultOnBack,
        beforeTextChanged: (text: CharSequence?, start: Int, count: Int, after: Int,binding:TitleBarSearchBinding) -> Unit = { _, _, _, _,_ -> },
        onTextChanged: (text: CharSequence?, start: Int, before: Int, count: Int,binding:TitleBarSearchBinding) -> Unit = { _, _, _, _,_ -> },
        afterTextChanged: (text: Editable?,binding:TitleBarSearchBinding) -> Unit = {_,_->},
        onSearch: ((String,binding:TitleBarSearchBinding) -> Unit) = {_,_->}
    ) {
        val binding = TitleBarSearchBinding.inflate(LayoutInflater.from(context), this, true)
        binding.apply {
            ceSearch.hint = hint
            ivBack.clickNoRepeat { onBack() }
            ceSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    beforeTextChanged(s,start,count,after,binding)
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    onTextChanged(s,start,before,count,binding)
                }
                override fun afterTextChanged(s: Editable?) {
                    afterTextChanged(s,binding)
                }
            })
            tvSearchText.clickNoRepeat {
                //如果输入内容为空，传入hint内容
                onSearch.invoke(ceSearch.text?.trim().toString().ifEmpty { hint },this)
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
     * @param onBack 返回 不传入Unit 默认不显示ivBack
     */
    fun loadDynamicsTitleBar(view: ViewGroup,
                             onBack: (() -> Unit)? = defaultOnBack
    ) {
        val binding = TitleBarDynamicsBinding.inflate(LayoutInflater.from(context), this, true)
        if (onBack != null) {
            binding.ivBack.visibility = VISIBLE
            binding.ivBack.clickNoRepeat {
                onBack()
            }
        }
        val parent = view.parent as ViewGroup?
        parent?.removeView(view)
        binding.clDynamics.addView(view)
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
//    val binding = TitleBarLiveBinding.inflate(LayoutInflater.from(context), this, true)
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