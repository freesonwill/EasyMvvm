package com.xcjh.app.view.balldetail.liveup

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.xcjh.app.R
import com.xcjh.app.bean.BasketballTeamMemberBean
import com.xcjh.app.databinding.ItemDetailLineupBasketballBinding
import com.xcjh.app.databinding.ViewTabBasketballTableBinding

class BasketballLineupView(context: Context?, attrs: AttributeSet?) : LinearLayout(context,attrs) {
    private var binding: ViewTabBasketballTableBinding
    init {
        binding= ViewTabBasketballTableBinding.inflate(LayoutInflater.from(context),this,true)
        binding.rcvPlayer.linear().setup {
            addType<BasketballTeamMemberBean> {
                R.layout.item_detail_lineup_basketball
            }
            onBind {
                when (val item = _data) {
                    is BasketballTeamMemberBean -> {
                        val binding = getBinding<ItemDetailLineupBasketballBinding>()

                        if (modelPosition+1 == models?.size){
                            //binding.root.setBackgroundDrawable(context.getDrawable(R.drawable.shape_bottom_r10))
                            binding.root.setBackgroundResource(R.drawable.shape_bottom_r10)
                        }
                        binding.root.backgroundTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(context!!, if (modelPosition%2==0) R.color.c_181819 else R.color.c_1D1D1D)
                        )
                        binding.tvPlayerNum.apply {
                            if (item.data.first == "0"){
                                this.setBackgroundResource(R.drawable.ic_basket_team_first)
                            }else{
                                this.setBackgroundResource(R.drawable.ic_basket_team_tb)
                            }
                            text = item.number.toString()
                        }
                        binding.tvPlayerName.text = item.name
                        binding.tvTime.text = item.data.time+"'"
                        binding.tvScore.text = item.data.score
                        binding.tvFloor.text = item.data.rebound
                        binding.tvAssist.text = item.data.assists
                        binding.tvShot.text = item.data.hitAndShot

                    }
                }
            }
        }
    }

    fun setData(list: ArrayList<BasketballTeamMemberBean>?) {
        if (list == null || list.size == 0) {
            return
        }
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

        binding.rcvPlayer.models = list
        //requestLayout()
    }
}