package com.walisport.module.search.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.MonthView
import kotlin.math.min

class SearchCustomMonthView(context: Context?) : MonthView(context) {

    private var mRadius = 0
    private val mCurrentDayPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.TRANSPARENT
    }

    override fun onPreviewHook() {
        mRadius = (min(mItemWidth.toDouble(), mItemHeight.toDouble()) / 5 * 2).toInt()
        mSchemePaint.style = Paint.Style.STROKE
    }

    override fun onDrawSelected(
        canvas: Canvas,
        calendar: Calendar,
        x: Int,
        y: Int,
        hasScheme: Boolean
    ): Boolean {
        canvas.drawCircle(
            x + mItemWidth / 2f,
            y + mItemHeight / 2f,
            mRadius.toFloat(),
            mSelectedPaint
        )
        return false
    }

    override fun onDrawScheme(canvas: Canvas, calendar: Calendar, x: Int, y: Int) {
        canvas.drawCircle(
            x + mItemWidth / 2f,
            y + mItemHeight / 2f,
            mRadius.toFloat(),
            mSchemePaint
        )
    }

    override fun onDrawText(
        canvas: Canvas,
        calendar: Calendar,
        x: Int,
        y: Int,
        hasScheme: Boolean,
        isSelected: Boolean
    ) {
        val baselineY = mTextBaseLine + y
        val cx = x + mItemWidth / 2
        val cy = y + mItemHeight / 2
        val dayText = calendar.day.toString()

        if (calendar.isCurrentDay && !isSelected) {
            canvas.drawCircle(cx.toFloat(), cy.toFloat(), mRadius.toFloat(), mCurrentDayPaint)
        }

        when {
            isSelected -> mSelectTextPaint
            hasScheme -> when {
                calendar.isCurrentMonth -> mSchemeTextPaint
                else -> mOtherMonthTextPaint
            }
            else -> when {
                calendar.isCurrentMonth -> mCurMonthTextPaint
                else -> mOtherMonthTextPaint
            }
        }.let { textPaint ->
            canvas.drawText(dayText, cx.toFloat(), baselineY, textPaint)
        }

    }
}