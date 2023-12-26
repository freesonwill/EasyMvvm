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
import com.xcjh.app.databinding.ItemDetailTableBinding
import com.xcjh.app.databinding.LayoutEmptyBinding
import com.xcjh.app.utils.setEmpty
import com.xcjh.base_lib.utils.view.visibleOrGone

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
            layout?.addView(setEmpty(context, isCenter = false, marginT = 32, marginB = 52))
            return
        }
        for ((i, it) in list.withIndex()) {
            val binding = ItemDetailTableBinding.inflate(LayoutInflater.from(context), null, false)
            // binding.tvCompany.text = it.companyName
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
                when (it.companyName.length) {
                    in 3..6 -> {
                        binding.tvCompany.text = it.companyName.substring(0 until 2) + "*".repeat(it.companyName.length-2)
                    }
                    2 -> {
                        binding.tvCompany.text = it.companyName.substring(0 until 1) + "*"
                    }
                    1 -> {
                        binding.tvCompany.text = it.companyName
                    }
                    else -> {
                        //大于6个字
                        binding.tvCompany.text = it.companyName.substring(0 until 2) + "****"
                    }
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
            text.setTextColor(context.getColor(R.color.c_D63823))
        } else if (a > b) {
            text.setTextColor(context.getColor(R.color.c_pb_bar))
        } else {
            text.setTextColor(context.getColor(R.color.c_d7d7e7))
        }
    }
}