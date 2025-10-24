package com.walisport.module.hall.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.requireActivity
import com.walisport.module.gamedetail.ui.fragment.GameDetailFragment
import com.walisport.module.hall.data.GameContentData
import com.walisport.module.hall.databinding.ItemGameAllListInnerBinding

//TODO 先暫時用GameContentData，等接api再說
class GameAllListInnerAdapter : BaseAdapter<GameContentData, GameListInnerViewHolder, ItemGameAllListInnerBinding>(GameContentDiff()) {
    override fun convertPlus(
        holder: GameListInnerViewHolder,
        binding: ItemGameAllListInnerBinding,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemGameAllListInnerBinding {
        return ItemGameAllListInnerBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemGameAllListInnerBinding,
        viewType: Int
    ): GameListInnerViewHolder {
        return GameListInnerViewHolder(binding)
    }
}

class GameListInnerViewHolder(val item: ItemGameAllListInnerBinding): BaseViewHolder(item) {
    fun bind(data: GameContentData) {
        item.ivGameCover.setImageResource(data.cover)

        // TODO 暫時串接遊戲詳情
        item.root.clickNoRepeat {
            item.root.requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    arch.cayenne.lib.common.R.anim.slide_in_right,
                    arch.cayenne.lib.common.R.anim.no_anim,
                    arch.cayenne.lib.common.R.anim.no_anim,
                    arch.cayenne.lib.common.R.anim.slide_out_right
                )
                .add(arch.cayenne.lib.base.R.id.nav_host, GameDetailFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}
