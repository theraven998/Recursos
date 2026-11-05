package com.raven.claudesemana

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import kotlin.concurrent.thread

class ClaudeWidget : AppWidgetProvider() {

    companion object {
        const val ACTION_REFRESH = "com.raven.claudesemana.REFRESH"

        private fun ids(ctx: Context): IntArray =
            AppWidgetManager.getInstance(ctx).getAppWidgetIds(ComponentName(ctx, ClaudeWidget::class.java))

        /** Pinta todos los widgets con lo guardado. */
        fun renderAll(ctx: Context, offline: Boolean = false, loading: Boolean = false) {
            val mgr = AppWidgetManager.getInstance(ctx)
            for (id in ids(ctx)) render(ctx, mgr, id, offline, loading)
        }

        /** Consulta la red y repinta. Bloqueante. */
        fun fetchAndRender(ctx: Context): Boolean {
            val ok = UsoRepo.fetch(ctx)
            renderAll(ctx, offline = !ok)
            return ok
        }

        private fun refreshIntent(ctx: Context): PendingIntent {
            val i = Intent(ctx, ClaudeWidget::class.java).setAction(ACTION_REFRESH)
            return PendingIntent.getBroadcast(
                ctx, 0, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        private fun ajustesIntent(ctx: Context): PendingIntent =
            PendingIntent.getActivity(
                ctx, 1, Intent(ctx, AjustesActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        fun render(ctx: Context, mgr: AppWidgetManager, id: Int, offline: Boolean, loading: Boolean) {
            val density = ctx.resources.displayMetrics.density
            val opts = mgr.getAppWidgetOptions(id)
            val minW = opts.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 250).let { if (it <= 0) 250 else it }
            val minH = opts.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 60)
            val barW = ((minW - 160).coerceAtLeast(40) * density).toInt()
            val barH = (12 * density).toInt()

            val uso = UsoRepo.cached(ctx)
            val v = RemoteViews(ctx.packageName, R.layout.widget_layout)
            val configurado = Ajustes.configurado(ctx)
            v.setOnClickPendingIntent(R.id.root, if (configurado) refreshIntent(ctx) else ajustesIntent(ctx))

            val tall = minH >= 100 && uso?.sesion != null
            v.setViewVisibility(R.id.row_sesion, if (tall) android.view.View.VISIBLE else android.view.View.GONE)

            if (uso == null) {
                v.setImageViewBitmap(R.id.bar_semana, Bars.dots(barW, barH, 0.0, Palette.WHITE, density))
                v.setImageViewBitmap(R.id.bar_claude, Bars.dots(barW, barH, 0.0, Palette.WHITE, density))
                v.setTextViewText(R.id.pct_semana, "--")
                v.setTextViewText(R.id.pct_claude, "--")
                v.setTextViewText(R.id.line, if (!configurado) "Toca para configurar" else if (loading) "…" else if (offline) "sin conexión" else "toca para actualizar")
                v.setTextViewText(R.id.act, if (loading) "…" else "")
            } else {
                val col = Palette.estado(uso.estado)
                v.setImageViewBitmap(R.id.bar_semana, Bars.dots(barW, barH, uso.semana, Palette.WHITE, density))
                v.setImageViewBitmap(R.id.bar_claude, Bars.dots(barW, barH, uso.uso, col, density))
                v.setTextViewText(R.id.pct_semana, "${Math.round(uso.semana)}%")
                v.setTextViewText(R.id.pct_claude, "${Math.round(uso.uso)}%")
                v.setTextColor(R.id.pct_claude, col)
                if (uso.sesion != null) {
                    v.setImageViewBitmap(R.id.bar_sesion, Bars.dots(barW, barH, uso.sesion, Palette.GRAY, density))
                    v.setTextViewText(R.id.pct_sesion, "${Math.round(uso.sesion)}%")
                }
                val partes = mutableListOf(uso.diffTexto())
                if (uso.reinicioTexto.isNotEmpty()) partes += "reinicia ${uso.reinicioCorto()}"
                if (uso.falta.isNotEmpty()) partes += "en ${uso.falta}"
                var linea = partes.joinToString(" · ")
                if (offline) linea = "sin conexión · $linea"
                v.setTextViewText(R.id.line, linea)
                val hora = uso.horaConsulta()
                v.setTextViewText(R.id.act, if (loading) "…" else if (hora.isEmpty()) "" else "act. $hora")
            }
            mgr.updateAppWidget(id, v)
        }
    }

    private fun refresh(ctx: Context, pending: PendingResult?) {
        renderAll(ctx, loading = true)
        val app = ctx.applicationContext
        thread {
            try {
                fetchAndRender(app)
            } finally {
                pending?.finish()
            }
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        refresh(context, goAsync())
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_REFRESH) {
            refresh(context, goAsync())
        } else {
            super.onReceive(context, intent)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle
    ) {
        render(context, appWidgetManager, appWidgetId, offline = false, loading = false)
    }
}
