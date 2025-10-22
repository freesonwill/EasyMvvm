package com.walisport.module.gamedetail.ui.popup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.TextView
import com.walisport.module.gamedetail.R

class FullNumberPopup(context: Context) {

    private val popupView = LayoutInflater.from(context).inflate(R.layout.layout_ranking_popup, null)
    private val tvFullNumber: TextView = popupView.findViewById(R.id.tv_value)
    private val popupWindow: PopupWindow = PopupWindow(
        popupView,
        android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        true
    ).apply {
        isOutsideTouchable = true
    }

    fun show(anchorView: View, fullText: String) {
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
            return
        }

        tvFullNumber.text = fullText

        popupView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val popupWidth = popupView.measuredWidth

        val xOffset = (anchorView.width - popupWidth) / 2

        popupWindow.showAsDropDown(anchorView, xOffset, -anchorView.height - popupView.measuredHeight)
    }

    fun dismiss() {
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
        }
    }
}