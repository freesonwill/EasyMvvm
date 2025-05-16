package arch.cayenne.module.betslip.utisl

interface RecyclerItemListener<T> {
    fun  onItemClick(item:T?,position:Int)
}