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
import com.xcjh.app.bean.FootballPlayer
import com.xcjh.app.databinding.ItemDetailLineupBasketballBinding

class BasketballLineupView : LinearLayout {
    private lateinit var layout: LinearLayout

    constructor(context: Context?) : super(context) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int,
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
        //Gson().toJson(list).loge("===")
        layout.removeAllViews()
        //方法1
        list.sortBy { it.data }
        //方法2
     /*   list.sortedWith (Comparator { o1, o2 ->
            if (o2.data.score == o1.data.score) {
                //  o1.name.compareTo(o2.name)
                o2.data.assists.toInt() - o1.data.assists.toInt()
            } else {
                o2.data.score.toInt() - o1.data.score.toInt()
            }
        })
        //方法3
        list.sortWith(compareBy({ it.data.score.toInt() }, { it.data.assists.toInt() }))
        val list = list.reversed() */

        for ((i, it) in list.withIndex()) {
            val binding =
                ItemDetailLineupBasketballBinding.inflate(LayoutInflater.from(context), this, false)

            if (i == list.size-1){
                //binding.root.setBackgroundDrawable(context.getDrawable(R.drawable.shape_bottom_r10))
                binding.root.setBackgroundResource(R.drawable.shape_bottom_r10)
            }
            binding.root.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    if (i%2==0) R.color.c_181819 else R.color.c_1D1D1D
                )
            )
            binding.tvPlayerNum.apply {
                if (it.data.first == "0"){
                    this.setBackgroundResource(R.drawable.ic_basket_team_first)
                }else{
                    this.setBackgroundResource(R.drawable.ic_basket_team_tb)
                }
                text = it.number.toString()
            }
            binding.tvPlayerName.text = it.name
            binding.tvTime.text = it.data.time
            binding.tvScore.text = it.data.score
            binding.tvFloor.text = it.data.rebound
            binding.tvAssist.text = it.data.assists
            binding.tvShot.text = it.data.hitAndShot
            layout.addView(binding.root)
        }
    }
}