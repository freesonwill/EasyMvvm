package arch.cayenne.module.hall.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.database.entity.BaseGameSupplierData
import arch.cayenne.lib.database.entity.BaseTournamentData
import arch.cayenne.module.hall.data.GameSupplierListItem
import arch.cayenne.module.hall.ui.adapter.GameSupplierHeaderViewHolder
import com.walisport.module.hall.databinding.ItemSupplierHeaderBinding
import com.walisport.module.hall.databinding.ItemSupplierSectionBinding
class GameSupplierSectionAdapter(
    private val onClick: (BaseGameSupplierData) -> Unit,
    private val onSelectionChanged: (() -> Unit)? = null
) : BaseAdapter<GameSupplierListItem, BaseViewHolder, ViewBinding>(GameSupplierSectionCompare()) {

    // 儲存選中的聯賽ID
    private val selectedTournamentIds = mutableSetOf<Int>()

    // 儲存打開彈窗時的初始選中狀態（用於重置和比較）
    private val initialSelectedTournamentIds = mutableSetOf<Int>()
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is GameSupplierListItem.Header -> TYPE_HEADER
            is GameSupplierListItem.GameSupplierItem -> TYPE_ITEM
        }
    }

    fun setInitSelectIds(){

    }
    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return when (viewType) {
            TYPE_HEADER -> ItemSupplierHeaderBinding.inflate(inflater, parent, false)
            TYPE_ITEM -> ItemSupplierSectionBinding.inflate(inflater, parent, false)
            else -> throw IllegalArgumentException("Unknown type")
        }
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): BaseViewHolder {
        return when (viewType) {
            TYPE_HEADER -> GameSupplierHeaderViewHolder(binding as ItemSupplierHeaderBinding)
            TYPE_ITEM -> GameSupplierViewHolder(binding as ItemSupplierSectionBinding)
            else -> throw IllegalArgumentException("Unknown type")
        }
    }

    override fun convertPlus(holder: BaseViewHolder, binding: ViewBinding, position: Int) {
        when (val item = getItem(position)) {
            is GameSupplierListItem.Header -> {
                (holder as GameSupplierHeaderViewHolder).bind(item)
            }

            is GameSupplierListItem.GameSupplierItem -> {
                val isSelected = selectedTournamentIds.contains(item.tournament.id)
                (holder as GameSupplierViewHolder).bind(
                    item = item,
                    isSelected = isSelected,
                    onClick = { tournament ->
                        LogUtils.e("tournament----onClick-----------${tournament.id}")
                        toggleSelection(tournament.id,position)
                        onClick(tournament)
                    }
                )
            }
        }
    }

    // 切換選中狀態
    private fun toggleSelection(tournamentId: Int,pos:Int) {

        if (selectedTournamentIds.contains(tournamentId)) {
            selectedTournamentIds.remove(tournamentId)
        } else {
            selectedTournamentIds.add(tournamentId)
        }
        LogUtils.e("tournament----toggleSelection-----------${selectedTournamentIds}")
        // 通知選中狀態變更
        onSelectionChanged?.invoke()
        notifyItemChanged(pos)
    }

    // 清除所有選中狀態（包括初始狀態）
    fun clearAllSelections() {
        selectedTournamentIds.clear()
        initialSelectedTournamentIds.clear()
        notifyDataSetChanged()
        onSelectionChanged?.invoke()
    }

    // 重置為打開彈窗時的初始狀態
    fun resetToInitialState() {
        selectedTournamentIds.clear()
        selectedTournamentIds.addAll(initialSelectedTournamentIds)
        notifyDataSetChanged()
        onSelectionChanged?.invoke()
    }

    // 保存當前選中狀態為初始狀態（在彈窗打開時調用）
    fun saveCurrentAsInitialState() {
        initialSelectedTournamentIds.clear()
        initialSelectedTournamentIds.addAll(selectedTournamentIds)
    }

    // 檢查當前選中狀態是否與初始狀態相同
    fun isSelectionChanged(): Boolean {
        return selectedTournamentIds != initialSelectedTournamentIds
    }

    // 檢查初始選中的聯賽是否還存在於當前列表中
    fun isInitialSelectionStillValid(): Boolean {
        val currentTournamentIds = currentList
            .filterIsInstance<GameSupplierListItem.GameSupplierItem>()
            .map { it.tournament.id }
            .toSet()
        return initialSelectedTournamentIds.all { it in currentTournamentIds }
    }

    // 獲取選中的supplierID列表
    fun getSelectedTournamentIds(): List<Int> {
        return selectedTournamentIds.toList()
    }

    // 獲取選中的supplier數據列表
    fun getSelectedTournaments(): List<BaseGameSupplierData> {
        return currentList.filterIsInstance<GameSupplierListItem.GameSupplierItem>()
            .filter { selectedTournamentIds.contains(it.tournament.id) }
            .map { it.tournament }
    }

    // 設置選中的supplierID列表
    fun setSelectedIds(ids: List<Int>) {
        LogUtils.e("setSelectedIds---a-------->${ids}")
        selectedTournamentIds.clear()
        selectedTournamentIds.addAll(ids)
        notifyDataSetChanged()
        onSelectionChanged?.invoke()
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }
}

class GameSupplierSectionCompare : DiffUtil.ItemCallback<GameSupplierListItem>() {
    override fun areItemsTheSame(
        oldItem: GameSupplierListItem,
        newItem: GameSupplierListItem
    ): Boolean = oldItem == newItem

    override fun areContentsTheSame(
        oldItem: GameSupplierListItem,
        newItem: GameSupplierListItem
    ): Boolean = oldItem == newItem

}