package com.walisport.module.business.common.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GamePreviewBean(
    val type: PreviewType,
    val url: String
) : Parcelable

enum class PreviewType { VIDEO, IMAGE }