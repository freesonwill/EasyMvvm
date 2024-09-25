package com.cn.game.sdk2.utils.ext

import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.clearAllItemDecorations() {
    while (this.itemDecorationCount > 0) {
        this.removeItemDecorationAt(0)
    }
}