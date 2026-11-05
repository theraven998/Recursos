package com.raven.claudesemana

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

object Palette {
    const val WHITE = 0xFFFFFFFF.toInt()
    const val AMBER = 0xFFFFB000.toInt()
    const val RED = 0xFFD71921.toInt()
    const val OFF = 0xFF2A2A2A.toInt()
    const val GRAY = 0xFF8A8A8A.toInt()

    fun estado(e: String): Int = when (e) {
        "warning" -> AMBER
        "critical" -> RED
        else -> WHITE
    }
}

object Bars {
    /** Barra de puntos (dot-matrix). widthPx es el ancho aproximado deseado. */
    fun dots(widthPx: Int, heightPx: Int, pct: Double, color: Int, density: Float): Bitmap {
        val pitch = 7f * density
        val dia = 5f * density
        val n = (widthPx / pitch).toInt().coerceAtLeast(8)
        val w = (n * pitch).toInt()
        val bmp = Bitmap.createBitmap(w, heightPx, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        val filled = Math.round(pct.coerceIn(0.0, 100.0) / 100.0 * n).toInt()
        val cy = heightPx / 2f
        for (i in 0 until n) {
            p.color = if (i < filled) color else Palette.OFF
            c.drawCircle(i * pitch + pitch / 2f, cy, dia / 2f, p)
        }
        return bmp
    }
}
