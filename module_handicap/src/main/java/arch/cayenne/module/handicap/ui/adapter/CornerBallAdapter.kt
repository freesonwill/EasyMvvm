package arch.cayenne.module.handicap.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.module.handicap.compare.CornerBallCompare
import arch.cayenne.module.handicap.data.CornerBallBean
import arch.cayenne.module.handicap.databinding.ItemCornerLayoutBinding

class CornerBallAdapter : BaseAdapter<CornerBallBean, BaseViewHolder, ItemCornerLayoutBinding>(
    CornerBallCompare()
) {
    @SuppressLint("DefaultLocale")
    override fun convertPlus(
        holder: BaseViewHolder,
        binding: ItemCornerLayoutBinding,
        position: Int
    ) {
        val item = getItem(position)
        binding.tvCornerName.text = item.type
        binding.tvHomeEvent.text = item.homeTip
        binding.tvAwayEvent.text = item.awayTip
        binding.tvHomeType.text = item.homeType
        binding.tvAwayType.text = item.awayType
        binding.tvGameScore.text = String.format("%d-%d", item.homeScore, item.awayScore)
        binding.tvLetBallMsg.text = item.msg
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemCornerLayoutBinding {
        return ItemCornerLayoutBinding.inflate(inflater, parent, false)
    }

    override fun createViewHolder(
        binding: ItemCornerLayoutBinding,
        viewType: Int
    ): BaseViewHolder {
        return BaseViewHolder(binding)
    }
}