package com.walisport.lib_common.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.walisport.lib_common.R
import com.walisport.lib_common.databinding.TittleBarDefaultBinding
import com.walisport.lib_common.databinding.TittleBarSearchBinding

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
            ivBack.setOnClickListener {
                callback()
            }
        }
    }

    /**
     * 通用标题
     * @param titleName 标题名称
     * @param callback 返回
     * @param callbackRight 右边按钮点击回调
     */
    fun loadTitleBarShowRight(titleName: String, callback: () -> Unit, callbackRight: () -> Unit) {
        val binding = TittleBarDefaultBinding.inflate(LayoutInflater.from(context), this, true)
        binding.apply {
            tvTitleName.text = titleName
            ivBack.setOnClickListener { callback() }
            tvTitleRight.setOnClickListener { callbackRight() }
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
            ivBack.setOnClickListener {
                callback()
            }
            tvSearchText.setOnClickListener {
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
     * @param callbackCompetition 下拉切换
     */
    fun loadLiveTitleBar(
        leagueImgUrl: String,
        competitionName: String,
        money: String,
        callback: () -> Unit,
        callbackCompetition: (String) -> Unit
    ) {


    }

}