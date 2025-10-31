package arch.cayenne.module.home.data.model

data class CommonFeaturesBean(
    val id:Int,
    val drawableId: Int,
    val titleResId: Int,
    val isShowRectangleText: Boolean = false,
    val rectangleText: String = "",
    val clickListener: ()->Unit
)
