package com.raven.claudesemana

import android.content.Context

/** URL del endpoint y token, guardados en SharedPreferences. */
object Ajustes {
    private const val PREFS = "ajustes"

    fun url(ctx: Context): String =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("url", "").orEmpty()

    fun token(ctx: Context): String =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("token", "").orEmpty()

    fun configurado(ctx: Context): Boolean = url(ctx).isNotBlank() && token(ctx).isNotBlank()

    fun guardar(ctx: Context, url: String, token: String) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString("url", url.trim())
            .putString("token", token.trim())
            .apply()
    }
}
