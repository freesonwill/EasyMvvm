package com.xcjh.app.view.balldetail.liveup

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.xcjh.app.R
import com.xcjh.app.bean.BasketballTeamMemberBean
import com.xcjh.app.databinding.ItemDetailLineupBasketballBinding

class BasketballLineupView : LinearLayout {
    private lateinit var layout: LinearLayout

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

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(context)
    }

    @SuppressLint("MissingInflatedId")
    fun initView(context: Context?) {
        val v = LayoutInflater.from(getContext()).inflate(R.layout.view_tab_basketball_table, this)
        layout = v.findViewById(R.id.layout_table)
    }

    @SuppressLint("MissingInflatedId")
    fun setData(list: ArrayList<BasketballTeamMemberBean>?) {
        if (list == null || list.size == 0) {
            return
        }
        layout.removeAllViews()
        list.forEach {
            val binding = ItemDetailLineupBasketballBinding.inflate(LayoutInflater.from(context), this, false)
            binding.tvPlayerNum.apply {
                backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, if (it.data.first=="0") R.color.c_fe4848 else R.color.c_8a91a0)
                )
                text = it.number.toString()
            }
            binding.tvPlayerName.text= it.name
            binding.tvTime.text= it.data.time
            binding.tvScore.text= it.data.score
            binding.tvFloor.text= it.data.rebound
            binding.tvAssist.text= it.data.assists
            binding.tvShot.text= it.data.hitAndShot
            layout.addView(binding.root)
        }

    }
}