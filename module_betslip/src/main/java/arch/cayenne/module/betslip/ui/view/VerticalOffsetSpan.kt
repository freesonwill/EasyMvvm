package arch.cayenne.module.betslip.ui.view

import android.text.TextPaint
import android.text.style.MetricAffectingSpan

/**
 * @author: wenxi
 * @date: 25/6/25 14:37
 * @description: 特殊字符在基线上对齐
 */
class VerticalOffsetSpan(offsetPx: Int): MetricAffectingSpan() {
    private var mOffsetPx = offsetPx


    override fun updateDrawState(tp: TextPaint) {
        tp.baselineShift += mOffsetPx // 关键代码
    }

    override fun updateMeasureState(tp: TextPaint) {
        tp.baselineShift += mOffsetPx
    }
}