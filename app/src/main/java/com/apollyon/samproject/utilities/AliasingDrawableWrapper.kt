package com.apollyon.samproject.utilities

import android.graphics.Canvas
import android.graphics.DrawFilter
import android.graphics.Paint
import android.graphics.PaintFlagsDrawFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.DrawableWrapper
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Classe utilita per disattivare il filtro quando disegnoin pixel art
 */
@RequiresApi(Build.VERSION_CODES.M)
class AliasingDrawableWrapper(wrapped : Drawable) : DrawableWrapper(wrapped) {

    companion object{
        val DRAW_FILTER = PaintFlagsDrawFilter(Paint.FILTER_BITMAP_FLAG, 0)
    }

    override fun draw(canvas: Canvas) {
        val oldDrawFilter = canvas.drawFilter
        canvas.drawFilter = DRAW_FILTER
        super.draw(canvas)
        canvas.drawFilter = oldDrawFilter
    }
}