package com.walisport.module.me.ui.view

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.text.style.ReplacementSpan
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px

class WLLinearGradientFontSpan : ReplacementSpan {

    private val mBottom = 35.dp2px.toFloat()
    private val pos = floatArrayOf(0.7f, 1.0f)
    private var startColor = Color.BLUE
    private var endColor = Color.RED

    constructor()

    constructor(startColor: Int, endColor: Int) {
        this.startColor = startColor
        this.endColor = endColor
    }

    override fun getSize(
        paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?
    ): Int {
        return paint.measureText(text, start, end).toInt()
    }

    override fun draw(
        canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int,
        bottom: Int, paint: Paint
    ) {
        val col = intArrayOf(startColor, endColor)
        val shader = LinearGradient(
            0f, 0f, 0f, mBottom,
            col,
            pos,
            Shader.TileMode.CLAMP
        )
        paint.setShader(shader)
        canvas.drawText(text, start, end, x, y.toFloat(), paint)
    }
}