package com.raven.claudesemana

import android.content.Context
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class Uso(
    val semana: Double,
    val uso: Double,
    val sesion: Double?,
    val diff: Double,
    val estado: String,
    val reinicioTexto: String,
    val falta: String,
    val consultado: String,
) {
    companion object {
        fun parse(json: String): Uso {
            val o = JSONObject(json)
            fun d(k: String) = if (o.has(k) && !o.isNull(k)) o.optDouble(k) else null
            fun s(k: String) = if (o.has(k) && !o.isNull(k)) o.optString(k) else ""
            val semana = d("semana") ?: 0.0
            val uso = d("uso") ?: 0.0
            return Uso(
                semana = semana,
                uso = uso,
                sesion = d("sesion"),
                diff = d("diff") ?: (uso - semana),
                estado = s("estado").ifEmpty { "ok" },
                reinicioTexto = s("reinicio_texto"),
                falta = s("falta"),
                consultado = s("consultado"),
            )
        }
    }

    /** Hora de consulta tipo "9:15" tomada del ISO. */
    fun horaConsulta(): String {
        val t = consultado.indexOf('T')
        if (t < 0 || consultado.length < t + 6) return ""
        val hh = consultado.substring(t + 1, t + 3).toIntOrNull() ?: return ""
        return "$hh:${consultado.substring(t + 4, t + 6)}"
    }

    /** "vie 06/11 12:00 PM" -> "vie 12:00 PM" */
    fun reinicioCorto(): String = reinicioTexto.replace(Regex("\\s*\\d{1,2}/\\d{1,2}"), "")

    fun diffTexto(): String {
        val r = Math.round(diff).toInt()
        return (if (r >= 0) "+$r" else "$r") + " pts"
    }
}

object UsoRepo {
    private const val PREFS = "claude_semana"
    private const val KEY = "ultimo"

    fun cached(ctx: Context): Uso? {
        val raw = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, null) ?: return null
        return try { Uso.parse(raw) } catch (e: Exception) { null }
    }

    /** Consulta la red (bloqueante: llamar fuera del hilo principal). true si funcionó. */
    fun fetch(ctx: Context): Boolean {
        if (!Ajustes.configurado(ctx)) return false
        return try {
            val c = URL(Ajustes.url(ctx)).openConnection() as HttpURLConnection
            c.connectTimeout = 10_000
            c.readTimeout = 10_000
            c.requestMethod = "GET"
            c.setRequestProperty("Authorization", "Bearer ${Ajustes.token(ctx)}")
            c.setRequestProperty("Accept", "application/json")
            try {
                if (c.responseCode != 200) return false
                val body = c.inputStream.bufferedReader().use { it.readText() }
                Uso.parse(body) // valida
                ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, body).apply()
                true
            } finally {
                c.disconnect()
            }
        } catch (e: Exception) {
            false
        }
    }
}
