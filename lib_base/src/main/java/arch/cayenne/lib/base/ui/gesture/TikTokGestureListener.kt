package arch.cayenne.lib.base.ui.gesture

interface TikTokGestureListener {
    fun onHorizontalFling()
    fun onHorizontalScroll(offsetX: Float)
    fun onActionUp()
}