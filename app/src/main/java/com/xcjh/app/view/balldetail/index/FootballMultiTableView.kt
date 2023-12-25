package com.xcjh.app.view.balldetail.index

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.xcjh.app.R
import com.xcjh.app.bean.OddsDetailBean
import com.xcjh.app.databinding.ItemDetailMultiTableBinding
import com.xcjh.app.databinding.LayoutEmptyBinding
import com.xcjh.app.utils.setEmpty
import com.xcjh.base_lib.utils.view.visibleOrGone

class FootballMultiTableView : LinearLayout {
    private var layout: LinearLayout? = null

    constructor(context: Context?) : super(context) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context,
        attrs,
        defStyleAttr) {
        initView(context)
    }

    fun initView(context: Context?) {
        val v = LayoutInflater.from(context).inflate(R.layout.view_detail_multi_table, this)
        layout = v.findViewById(R.id.layout_multi_table)
    }

    @SuppressLint("MissingInflatedId")
    fun setData(list: ArrayList<OddsDetailBean>?) {
        layout!!.removeAllViews()
        if (list == null || list.size == 0) {
            layout?.addView(setEmpty(context, isCenter = false, marginT = 32, marginB = 52))
            return
        }
        for ((i, it) in list.withIndex()) {
            val binding =
                ItemDetailMultiTableBinding.inflate(LayoutInflater.from(context), this, false)
            if (i == 0){
                //binding.root.setBackgroundDrawable(context.getDrawable(R.drawable.shape_bottom_r10))
                binding.vLine.visibleOrGone(false)
            }
            if (i == list.size-1){
                binding.lltContent.setBackgroundResource(R.drawable.shape_bottom_r10)
            }
            binding.lltContent.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(context, if (i%2==0) R.color.c_181819 else R.color.c_1D1D1D)
            )
            if (it.companyName.isNotEmpty()) {
                if (it.companyName.length > 2) {
                    binding.tvCompany.text = it.companyName.substring(0 until 2) + "*"
                } else {
                    binding.tvCompany.text = it.companyName.substring(0 until 1) + "*"
                }
            }

            //初盘数据
            binding.tvChuHome.text = it.firstHomeWin//主胜
            binding.tvChuPin.text = it.firstDraw//平
            binding.tvChuKe.text = it.firstAwayWin
            binding.tvChuPf.text = it.firstLossRatio//赔付率
            if (it.close == 1) {
                //即盘数据 封
                binding.tvJiHome.text = context.getString(R.string.close_win_p) //主胜
                binding.tvJiPin.text = context.getString(R.string.close_win_p)
                binding.tvJiAway.text = context.getString(R.string.close_win_p)
                binding.tvJiPf.text = context.getString(R.string.close_win_p)
            } else {
                //即盘数据
                binding.tvJiHome.text = it.currentHomeWin //主胜
                binding.tvJiPin.text = it.currentDraw//平
                binding.tvJiAway.text = it.currentAwayWin//客胜

                setColor(binding.tvJiHome, it.firstHomeWin.toFloat(), it.currentHomeWin.toFloat())
                setColor(binding.tvJiPin, it.firstDraw.toFloat(), it.currentDraw.toFloat())
                setColor(binding.tvJiAway, it.firstAwayWin.toFloat(), it.currentAwayWin.toFloat())

                binding.tvJiPf.text = it.currentLossRatio //赔付率
            }

            layout?.addView(binding.root)
        }
    }

    private fun setColor(text: TextView, a: Float, b: Float) {
        if (a < b) {
            text.setTextColor(context.getColor(R.color.c_D63823))
        } else if (a > b) {
            text.setTextColor(context.getColor(R.color.c_pb_bar))
        } else {
            text.setTextColor(context.getColor(R.color.c_d7d7e7))
        }
    }
}