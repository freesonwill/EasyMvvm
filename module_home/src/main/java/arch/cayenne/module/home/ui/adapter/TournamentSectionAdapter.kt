package arch.cayenne.module.home.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import arch.cayenne.lib.base.ui.adapter.BaseAdapter
import arch.cayenne.lib.base.ui.adapter.BaseViewHolder
import arch.cayenne.lib.database.entity.BaseTournamentData
import arch.cayenne.module.home.data.TournamentListItem
import arch.cayenne.module.home.data.constants.TournamentListType
import arch.cayenne.module.home.databinding.ItemTournamentHeaderBinding
import arch.cayenne.module.home.databinding.ItemTournamentSectionBinding
import arch.cayenne.module.home.ui.compare.TournamentSectionCompare

class TournamentSectionAdapter(
    private val tournamentListType: TournamentListType,
    private val onTournamentClick: (BaseTournamentData) -> Unit,
    private val onSelectionChanged: (() -> Unit)? = null
) : BaseAdapter<TournamentListItem, BaseViewHolder, ViewBinding>(TournamentSectionCompare()) {

    // 儲存選中的聯賽ID
    private val selectedTournamentIds = mutableSetOf<Int>()

    // 儲存打開彈窗時的初始選中狀態（用於重置和比較）
    private val initialSelectedTournamentIds = mutableSetOf<Int>()
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TournamentListItem.Header -> TYPE_HEADER
            is TournamentListItem.TournamentItem -> TYPE_ITEM
        }
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding {
        return when (viewType) {
            TYPE_HEADER -> ItemTournamentHeaderBinding.inflate(inflater, parent, false)
            TYPE_ITEM -> ItemTournamentSectionBinding.inflate(inflater, parent, false)
            else -> throw IllegalArgumentException("Unknown type")
        }
    }

    override fun createViewHolder(binding: ViewBinding, viewType: Int): BaseViewHolder {
        return when (viewType) {
            TYPE_HEADER -> TournamentHeaderViewHolder(binding as ItemTournamentHeaderBinding)
            TYPE_ITEM -> TournamentItemViewHolder(binding as ItemTournamentSectionBinding)
            else -> throw IllegalArgumentException("Unknown type")
        }
    }

    override fun convertPlus(holder: BaseViewHolder, binding: ViewBinding, position: Int) {
        when (val item = getItem(position)) {
            is TournamentListItem.Header -> {
                (holder as TournamentHeaderViewHolder).bind(item)
            }

            is TournamentListItem.TournamentItem -> {
                val isSelected = selectedTournamentIds.contains(item.tournament.id)
                (holder as TournamentItemViewHolder).bind(
                    item = item,
                    tournamentListType = tournamentListType,
                    isSelected = isSelected,
                    onClick = { tournament ->
                        toggleSelection(tournament.id)
                        notifyItemChanged(position)
                        onTournamentClick(tournament)
                    }
                )
            }
        }
    }

    // 切換選中狀態
    private fun toggleSelection(tournamentId: Int) {
        if (selectedTournamentIds.contains(tournamentId)) {
            selectedTournamentIds.remove(tournamentId)
        } else {
            selectedTournamentIds.add(tournamentId)
        }
        // 通知選中狀態變更
        onSelectionChanged?.invoke()
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
            .filterIsInstance<TournamentListItem.TournamentItem>()
            .map { it.tournament.id }
            .toSet()
        return initialSelectedTournamentIds.all { it in currentTournamentIds }
    }

    // 獲取選中的聯賽ID列表
    fun getSelectedTournamentIds(): List<Int> {
        return selectedTournamentIds.toList()
    }

    // 獲取選中的聯賽數據列表
    fun getSelectedTournaments(): List<BaseTournamentData> {
        return currentList.filterIsInstance<TournamentListItem.TournamentItem>()
            .filter { selectedTournamentIds.contains(it.tournament.id) }
            .map { it.tournament }
    }

    // 設置選中的聯賽ID列表
    fun setSelectedTournamentIds(ids: List<Int>) {
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