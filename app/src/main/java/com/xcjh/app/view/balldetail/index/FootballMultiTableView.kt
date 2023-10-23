package com.xcjh.app.view.balldetail.index

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.xcjh.app.R
import com.xcjh.app.bean.OddsDetailBean
import com.xcjh.app.databinding.ItemDetailMultiTableBinding
import com.xcjh.app.databinding.LayoutEmptyBinding

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
            val binding = LayoutEmptyBinding.inflate(LayoutInflater.from(context), this, false)
            layout?.addView(binding.root)
            return
        }
        list.forEach {
            val binding =
                ItemDetailMultiTableBinding.inflate(LayoutInflater.from(context), this, false)
            binding.tvCompany.text = it.companyName
            //初盘数据
            binding.tvChuHome.text = it.firstHomeWin//主胜
            binding.tvChuPin.text = it.firstDraw//平
            binding.tvChuKe.text = it.firstAwayWin

            binding.tvChuPf.text = it.firstLossRatio//赔付率
            //即盘数据
            binding.tvJiHome.text = it.currentHomeWin //主胜
            binding.tvJiPin.text = it.currentDraw//平
            binding.tvJiAway.text = it.currentAwayWin//客胜

            setColor(binding.tvJiHome, it.firstHomeWin.toFloat(), it.currentHomeWin.toFloat())
            setColor(binding.tvJiPin, it.firstDraw.toFloat(), it.currentDraw.toFloat())
            setColor(binding.tvJiAway, it.firstAwayWin.toFloat(), it.currentAwayWin.toFloat())

            binding.tvJiPf.text = it.currentLossRatio //赔付率

            layout?.addView(binding.root)
        }
    }

    private fun setColor(text: TextView, a: Float, b: Float) {
        if (a < b) {
            text.setTextColor(context.getColor(R.color.c_fe4848))
        } else if (a > b) {
            text.setTextColor(context.getColor(R.color.c_48fe4f))
        } else {
            text.setTextColor(context.getColor(R.color.c_F5F5F5))
        }
    }
}