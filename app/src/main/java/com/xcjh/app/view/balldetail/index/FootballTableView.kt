package com.xcjh.app.view.balldetail.index

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.xcjh.app.R
import com.xcjh.app.bean.OddsDetailBean
import com.xcjh.app.databinding.ItemDetailTableBinding
import com.xcjh.app.databinding.LayoutEmptyBinding

class FootballTableView : LinearLayout {
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

    @SuppressLint("MissingInflatedId")
    fun initView(context: Context?) {
        val v = LayoutInflater.from(getContext()).inflate(R.layout.view_detail_game_table, this)
        layout = v.findViewById(R.id.layout_table)
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
            val binding = ItemDetailTableBinding.inflate(LayoutInflater.from(context), null, false)
           // binding.tvCompany.text = it.companyName
            if (it.companyName.isNotEmpty()) {
                if (it.companyName.length > 2) {
                    binding.tvCompany.text = it.companyName.substring(0 until 2) + "*"
                } else {
                    binding.tvCompany.text = it.companyName.substring(0 until 1) + "*"
                }
            }
            //初盘数据
            binding.tvChuZ.text = it.firstHomeWin
            binding.tvChuP.text = it.firstDraw
            binding.tvChuK.text = it.firstAwayWin

            //即盘数据
            if (it.close == 1) {
                binding.tvJiZ.text = context.getString(R.string.close_win_p)
                binding.tvJiP.text = context.getString(R.string.close_win_p)
                binding.tvJiK.text = context.getString(R.string.close_win_p)
            } else {
                binding.tvJiZ.text = it.currentHomeWin
                binding.tvJiP.text = it.currentDraw
                binding.tvJiK.text = it.currentAwayWin
                setColor(binding.tvJiZ, it.firstHomeWin.toFloat(), it.currentHomeWin.toFloat())
                setColor(binding.tvJiP, it.firstDraw.toFloat(), it.currentDraw.toFloat())
                setColor(binding.tvJiK, it.firstAwayWin.toFloat(), it.currentAwayWin.toFloat())
            }

            layout?.addView(binding.root)
        }
    }

    private fun setColor(text: TextView, a: Float, b: Float) {
        if (a < b) {
            text.setTextColor(context.getColor(R.color.c_pb_bar))
        } else if (a > b) {
            text.setTextColor(context.getColor(R.color.c_48fe4f))
        } else {
            text.setTextColor(context.getColor(R.color.c_F5F5F5))
        }
    }
}