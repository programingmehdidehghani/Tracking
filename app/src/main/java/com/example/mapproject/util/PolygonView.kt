package com.example.mapproject.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View

class PolygonView (context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint()
    private val strokePaint = Paint()
    private var sides = 5
    private var radius = 20f
    private var strokeWidth = 5f

    init {
        paint.style = Paint.Style.FILL
        paint.color = Color.TRANSPARENT
        paint.isAntiAlias = true
        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = strokeWidth
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f
        val angle = Math.PI * 2 / sides
        val points = mutableListOf<Pair<Float, Float>>()
        for (i in 0 until sides) {
            val x = cx + (radius * Math.cos(angle * i)).toFloat()
            val y = cy + (radius * Math.sin(angle * i)).toFloat()
            points.add(Pair(x, y))
        }

        val path = Path()
        path.moveTo(points[0].first, points[0].second)
        for (i in 1 until sides) {
            path.lineTo(points[i].first, points[i].second)
        }
        path.close()
        canvas.drawPath(path, strokePaint)

        val innerPath = Path()
        innerPath.addCircle(cx, cy, radius / 2f, Path.Direction.CCW)
        innerPath.op(path, Path.Op.DIFFERENCE)
        canvas.drawPath(innerPath, paint)
    }



    fun setColor(color: Int) {
        paint.color = color
        strokePaint.color = color
        invalidate()
    }
}