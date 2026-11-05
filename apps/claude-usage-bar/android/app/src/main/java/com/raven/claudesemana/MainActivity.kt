package com.raven.claudesemana

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)
private val Doto = FontFamily(Font(R.font.doto_var, variationSettings = FontVariation.Settings(FontVariation.weight(700), FontVariation.Setting("ROND", 100f))))
private val Gray = Color(0xFF8A8A8A)
private val Off = Color(0xFF2A2A2A)
private val NothingRed = Color(0xFFD71921)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Screen() }
    }
}

@Composable
private fun Screen() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var uso by remember { mutableStateOf(UsoRepo.cached(ctx)) }
    var loading by remember { mutableStateOf(false) }
    var offline by remember { mutableStateOf(false) }

    fun actualizar() {
        if (loading) return
        loading = true
        scope.launch {
            val ok = withContext(Dispatchers.IO) { ClaudeWidget.fetchAndRender(ctx.applicationContext) }
            offline = !ok
            uso = UsoRepo.cached(ctx)
            loading = false
        }
    }
    LaunchedEffect(Unit) {
        if (Ajustes.configurado(ctx)) actualizar()
        else ctx.startActivity(android.content.Intent(ctx, AjustesActivity::class.java))
    }

    val estadoColor = Color(Palette.estado(uso?.estado ?: "ok"))

    Column(
        Modifier.fillMaxSize().background(Color.Black).systemBarsPadding().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).background(NothingRed, RoundedCornerShape(50)))
            Spacer(Modifier.width(10.dp))
            Text("CLAUDE SEMANA", color = Gray, fontSize = 12.sp, letterSpacing = 2.sp)
        }
        Spacer(Modifier.height(8.dp))
        Bloque("SEMANA", uso?.semana, Color.White)
        Bloque("CLAUDE", uso?.uso, estadoColor)
        uso?.sesion?.let { Bloque("SESIÓN", it, Gray) }

        val u = uso
        if (u != null) {
            val partes = mutableListOf(u.diffTexto())
            if (u.reinicioTexto.isNotEmpty()) partes += "reinicia ${u.reinicioCorto()}"
            if (u.falta.isNotEmpty()) partes += "en ${u.falta}"
            Text(partes.joinToString(" · "), color = Gray, fontSize = 13.sp)
        }
        val hora = uso?.horaConsulta().orEmpty()
        Text(
            when {
                loading -> "…"
                offline -> "sin conexión" + if (hora.isNotEmpty()) " · último $hora" else ""
                hora.isNotEmpty() -> "act. $hora"
                else -> ""
            },
            color = Color(0xFF5C5C5C), fontSize = 12.sp
        )
        Spacer(Modifier.weight(1f))
        Boton("ACTUALIZAR", filled = true) { actualizar() }
        Boton("AJUSTES", filled = false) {
            ctx.startActivity(android.content.Intent(ctx, AjustesActivity::class.java))
        }
        Boton("AGREGAR WIDGET", filled = false) {
            val mgr = AppWidgetManager.getInstance(ctx)
            if (mgr.isRequestPinAppWidgetSupported) {
                mgr.requestPinAppWidget(ComponentName(ctx, ClaudeWidget::class.java), null, null)
            }
        }
    }
}

@Composable
private fun Bloque(label: String, pct: Double?, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(label, color = Gray, fontSize = 12.sp, letterSpacing = 2.sp, modifier = Modifier.weight(1f))
            Text(
                if (pct == null) "--" else "${Math.round(pct)}%",
                color = color,
                style = TextStyle(fontFamily = Doto, fontSize = 56.sp)
            )
        }
        DotBar(pct ?: 0.0, color)
    }
}

@Composable
private fun DotBar(pct: Double, color: Color) {
    Canvas(Modifier.fillMaxWidth().height(14.dp)) {
        val pitch = 9.dp.toPx()
        val n = (size.width / pitch).toInt().coerceAtLeast(8)
        val offset = (size.width - n * pitch) / 2f
        val filled = Math.round(pct.coerceIn(0.0, 100.0) / 100.0 * n).toInt()
        for (i in 0 until n) {
            drawCircle(
                if (i < filled) color else Off,
                radius = 3.2.dp.toPx(),
                center = Offset(offset + i * pitch + pitch / 2f, size.height / 2f)
            )
        }
    }
}

@Composable
private fun Boton(texto: String, filled: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    Box(
        Modifier.fillMaxWidth().height(52.dp)
            .then(if (filled) Modifier.background(Color.White, shape) else Modifier.border(1.dp, Color(0xFF3A3A3A), shape))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(texto, color = if (filled) Color.Black else Color.White, fontSize = 13.sp, letterSpacing = 2.sp)
    }
}
