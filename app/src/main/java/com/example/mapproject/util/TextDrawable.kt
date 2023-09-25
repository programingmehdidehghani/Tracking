package com.example.mapproject.util

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.example.mapproject.R

class TextDrawable(context: Context) : Drawable() {
    private var text: String = ""
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textBounds: Rect = Rect()
    private var backgroundDrawable: Drawable? = null

    init {
        paint.color = Color.WHITE
        paint.textSize = 18f
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = ResourcesCompat.getFont(context, R.font.iran_sans)
    }

    fun setText(text: String) {
        this.text = text
        invalidateSelf()
    }


    fun setBackground(backgroundDrawable: Drawable?) {
        this.backgroundDrawable = backgroundDrawable
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        if (backgroundDrawable != null) {
            backgroundDrawable?.bounds = bounds
            backgroundDrawable?.draw(canvas)
        }
        canvas.drawText(text, bounds.centerX().toFloat(), bounds.centerY().toFloat() + textBounds.height() / 2, paint)
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        paint.getTextBounds(text, 0, text.length, textBounds)
    }

    override fun setAlpha(alpha: Int) {}

    override fun setColorFilter(colorFilter: ColorFilter?) {}

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
}