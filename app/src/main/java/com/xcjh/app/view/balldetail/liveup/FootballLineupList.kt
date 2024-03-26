package com.xcjh.app.view.balldetail.liveup

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.divider
import com.drake.brv.utils.grid
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.xcjh.app.R
import com.xcjh.app.bean.FootballLineupBean
import com.xcjh.app.bean.FootballPlayer
import com.xcjh.app.bean.MatchDetailBean
import com.xcjh.app.bean.MatchTeam
import com.xcjh.app.databinding.ItemDetailGameSubstituteBinding
import com.xcjh.app.databinding.ItemDetailGameSubstituteTopBinding
import com.xcjh.app.databinding.ViewDetailGameLiveupBinding
import com.xcjh.app.databinding.ViewDetailGameSubstituteBinding
import com.xcjh.base_lib.utils.view.visibleOrInvisible

/**
 * 首发、替补阵容
 */
class FootballLineupList (context: Context, attrs: AttributeSet?) : LinearLayout(context,attrs) {

    private var binding: ViewDetailGameSubstituteBinding

    init {
        binding= ViewDetailGameSubstituteBinding.inflate(LayoutInflater.from(context),this,true)
        binding.rcvPlayer.grid(2).divider {
           // setDrawable(R.drawable.divider_horizontal)
            setDivider(1, true)
            setColor("#27272A")
            orientation = DividerOrientation.GRID
        }.setup {
            addType<MatchTeam> {
                R.layout.item_detail_game_substitute_top
            }
            addType<FootballPlayer> {
                R.layout.item_detail_game_substitute
            }
            onBind {
                when (val item = _data) {
                    is MatchTeam -> {
                        val binding = getBinding<ItemDetailGameSubstituteTopBinding>()

                        binding.rltItem.setBackgroundColor(context.getColor(if(this.modelPosition%2==0) R.color.c_21152a else R.color.c_18152A ))
                        binding.tvName.text = item.name ?: ""
                    }
                    is FootballPlayer -> {
                        val binding = getBinding<ItemDetailGameSubstituteBinding>()
                        binding.ivIcon.visibleOrInvisible(item.name.isNotEmpty())
                        binding.tvPlayerNum.visibleOrInvisible(item.name.isNotEmpty())
                        binding.tvPosition.visibleOrInvisible(item.name.isNotEmpty())

//                        if(this.modelPosition%2==0){
//                            Glide.with(context).load(R.drawable.icon_team_red).into(binding.ivIcon)
//                        }else{
//                            Glide.with(context).load(R.drawable.iteam_blue_new).into(binding.ivIcon)
//                        }
//                        Glide.with(context).load(R.drawable.iteam_blue_new).into(binding.ivIcon)
//                         Glide.with(context).load(R.drawable.ic_basket_team_tb).into(binding.ivIcon)
//                        Glide.with(context).load(if(this.modelPosition%2==0) R.drawable.icon_team_red else R.drawable.icon_team_blue).into(binding.ivIcon)
                        Glide.with(context).load(if(this.modelPosition%2==0) R.drawable.icon_team_red else R.drawable.iteam_blue_new).into(binding.ivIcon)
//                         binding.ctlItem.setBackgroundColor(context.getColor(if(this.modelPosition%2==0) R.color.c_21152a else R.color.c_18152A ))
                        binding.tvPlayerNum.text = item.shirtNumber.toString()
                        binding.tvName.text = item.name
                        binding.tvPosition.text = getPlayerPos(item.position)
                    }
                }
            }
        }

    }

    private fun getPlayerPos(position: String): CharSequence? {

        var pos= context.getString(R.string.wz)
        when(position){
            ////球员位置，F前锋、M中场、D后卫、G守门员、其他为未知
            "F"->{
                pos= context.getString(R.string.qf)
            }
            "M"->{
                pos=context.getString(R.string.zc)
            }
            "D"->{
                pos=context.getString(R.string.hw)
            }
            "G"->{
                pos=context.getString(R.string.smy)
            }
        }
        return pos
    }

    val list = arrayListOf<Any>()
    fun setData(data: FootballLineupBean, first:Int=0) {
        list.clear()
        Glide.with(context).load(data.homeLogo).placeholder(R.drawable.def_football).into( binding.ivHomeIcon)
        Glide.with(context).load(data.awayLogo).placeholder(R.drawable.def_football).into( binding.ivAwayIcon)
        if ( first==1){
            binding.tvTitle.text= context.getString(R.string.first_z)
        }else{
            binding.tvTitle.text= context.getString(R.string.ti)
        }
        val home = data.home.filter {
            it.first == first
        }
        val away = data.away.filter {
            it.first == first
        }
        if (home.size == away.size) {
            //替补球员数量相同
            for ((i, item) in home.withIndex()) {
                list.add(item)
                list.add(away[i])
            }
        } else {
            if (home.size > away.size) {
                //替补球员数量主队多
                for ((i, item) in home.withIndex()) {
                    list.add(item)
                    try {
                        list.add(away[i])
                    }catch (_:Exception){
                        list.add(FootballPlayer())
                    }
                }
            } else {
                for ((i, item) in away.withIndex()) {
                    try {
                        list.add(home[i])
                    }catch (_:Exception){
                        list.add(FootballPlayer())
                    }
                    list.add(item)
                }
            }
        }
        binding.rcvPlayer.models = list
    }
}