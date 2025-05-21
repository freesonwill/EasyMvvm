package arch.cayenne.lib.common.ui.adapter

interface RecyclerItemListener<T> {
    fun  onItemClick(item:T?,position:Int)
}