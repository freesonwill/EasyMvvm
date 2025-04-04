package com.walisport.lib.common.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.walisport.lib.common.R
import com.walisport.lib.common.databinding.TittleBarBackgroundBinding
import com.walisport.lib.common.databinding.TittleBarDefaultBinding
import com.walisport.lib.common.databinding.TittleBarLiveBinding
import com.walisport.lib.common.databinding.TittleBarSearchBinding
import com.walisport.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.lib.common.utils.ext.clickNoRepeat

class TitleBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Toolbar(context, attrs, defStyleAttr) {

    /**
     * 通用标题
     * @param titleName 标题名称
     * @param callback 返回
     */
    fun loadGeneralTitleBar(titleName: String, callback: () -> Unit) {
        val binding = TittleBarDefaultBinding.inflate(LayoutInflater.from(context), this, true)
        binding.apply {
            tvTitleName.text = titleName
            ivBack.clickNoRepeat {
                callback()
            }
        }
    }

    /**
     * 背景设置标题
     * @param titleName 标题名称
     * @param callback 左边点击回调
     * @param callbackConfirm 右边点击回调
     * @param leftName 左边按钮字体
     * @param rightsName 右边按钮字体
     */
    fun loadBackgroundTitleBar(titleName: String, leftName:String,rightsName:String,callback: () -> Unit,callbackConfirm: () -> Unit) {
        val binding = TittleBarBackgroundBinding.inflate(LayoutInflater.from(context), this, true)
        binding.apply {
            tvTitleName.text = titleName
            tvBack.text = leftName
            tvTitleRight.text = rightsName
            tvBack.clickNoRepeat {
                callback()
            }
            tvTitleRight.clickNoRepeat {
                callbackConfirm()
            }
        }
    }

    /**
     * 通用标题2
     * @param titleName 标题名称
     * @param callback 返回
     * @param callbackRight 右边按钮点击回调
     */
    fun loadTitleBarShowRight(titleName: String, callback: () -> Unit, callbackRight: () -> Unit) {
        val binding = TittleBarDefaultBinding.inflate(LayoutInflater.from(context), this, true)
        binding.apply {
            tvTitleName.text = titleName
            ivBack.clickNoRepeat { callback() }
            tvTitleRight.visibility = VISIBLE
            tvTitleRight.clickNoRepeat { callbackRight() }
        }
    }

    /**
     * 搜索标题
     * @param hintText 搜索框提示
     * @param callback 返回
     * @param callbackSearch 搜索
     */
    fun loadSearchTitleBar(
        hintText: String,
        callback: () -> Unit,
        callbackSearch: (String) -> Unit
    ) {
        val binding = TittleBarSearchBinding.inflate(LayoutInflater.from(context), this, true)
        binding.apply {
            ceSearch.hint = hintText
            ivBack.clickNoRepeat {
                callback()
            }
            tvSearchText.clickNoRepeat {
                //hint text 为空 提示请输入搜索内容
                if (hintText.isEmpty() && ceSearch.text.toString().isEmpty()) {

                } else {
                    //如果输入内容为空，传入hint内容
                    callbackSearch(ceSearch.text.toString().ifEmpty { hintText })
                }

            }

        }
    }

    /**
     * 直播标题
     * @param leagueImgUrl 联赛图片
     * @param competitionName 联赛对站 A VS B
     * @param money 剩余总金额
     * @param callback 返回
     * @param expand 展开还是收起
     * @param callbackCompetition 下拉切换 boolean 单前状态展开还是收起
     */
    @SuppressLint("SetTextI18n")
    fun loadLiveTitleBar(
        leagueImgUrl: String,
        competitionName: String,
        expand:Boolean,
        money: String,
        callback: () -> Unit,
        callbackCompetition: (Boolean) -> Unit,
        callLeagueBack: () -> Unit,
    ) {
        val binding = TittleBarLiveBinding.inflate(LayoutInflater.from(context), this, true)
        Glide.with(context).load(leagueImgUrl).override(96.dp2px,22.dp2px)
            .error(R.drawable.title_league_icon)           // 加载失败时的占位符
            .into(binding.ivLandscapeLeagueIcon)
        binding.apply {
            tvCompetitionName.text = competitionName
            tvMoney.text= "¥ $money"
            ivBack.clickNoRepeat {
                callback()
            }
            tvCompetitionName.clickNoRepeat {
                callbackCompetition(!expand)
            }
            ivLandscapeLeagueIcon.clickNoRepeat {
                callLeagueBack()
            }
        }
    }

}