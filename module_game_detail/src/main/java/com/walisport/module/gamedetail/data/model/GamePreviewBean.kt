package com.walisport.module.gamedetail.data.model

import android.os.Parcelable
import com.walisport.module.gamedetail.ui.fragment.GamePreviewImageFragment
import kotlinx.parcelize.Parcelize

@Parcelize
data class GamePreviewBean(
    val type: PreviewType,
    val url: String
) : Parcelable

enum class PreviewType { VIDEO, IMAGE }