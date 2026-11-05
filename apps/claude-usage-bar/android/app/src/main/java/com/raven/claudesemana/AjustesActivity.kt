package com.raven.claudesemana

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Pantalla de ajustes: abre al tocar el icono y al agregar el widget. */
class AjustesActivity : ComponentActivity() {
    private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        widgetId = intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            ?: AppWidgetManager.INVALID_APPWIDGET_ID
        // Si se cierra sin guardar al configurar el widget, se cancela.
        setResult(Activity.RESULT_CANCELED, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId))
        setContent { Pantalla(widgetId) { terminar() } }
    }

    private fun terminar() {
        if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            setResult(Activity.RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId))
            finish()
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}

private val Gray = Color(0xFF8A8A8A)

@Composable
private fun Pantalla(widgetId: Int, alGuardar: () -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var url by remember { mutableStateOf(Ajustes.url(ctx)) }
    var token by remember { mutableStateOf(Ajustes.token(ctx)) }
    var estado by remember { mutableStateOf("") }
    var guardando by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().background(Color.Black).systemBarsPadding().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("AJUSTES", color = Gray, fontSize = 12.sp, letterSpacing = 2.sp)
        Text(
            "Escribe la URL de tu endpoint y tu token. Los montas tú: mira el README del recurso.",
            color = Gray, fontSize = 13.sp
        )
        Campo("URL DEL ENDPOINT", url, "https://tu-endpoint.example.com/uso", KeyboardType.Uri) { url = it }
        Campo("TOKEN", token, "TU_TOKEN", KeyboardType.Password) { token = it }
        if (estado.isNotEmpty()) Text(estado, color = Gray, fontSize = 12.sp)
        Spacer(Modifier.weight(1f))
        Boton(if (guardando) "..." else "GUARDAR", true) {
            if (guardando) return@Boton
            if (url.isBlank() || token.isBlank()) {
                estado = "Faltan la URL o el token."
                return@Boton
            }
            guardando = true
            Ajustes.guardar(ctx, url, token)
            scope.launch {
                val ok = withContext(Dispatchers.IO) { ClaudeWidget.fetchAndRender(ctx.applicationContext) }
                guardando = false
                if (ok) alGuardar() else estado = "Guardado, pero no responde. Revisa URL y token."
            }
        }
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID && Ajustes.configurado(ctx)) {
            Boton("VER USO", false) { alGuardar() }
        }
    }
}

@Composable
private fun Campo(label: String, valor: String, pista: String, tipo: KeyboardType, alCambiar: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, color = Gray, fontSize = 11.sp, letterSpacing = 2.sp)
        Box(
            Modifier.fillMaxWidth().border(1.dp, Color(0xFF3A3A3A), RoundedCornerShape(12.dp)).padding(14.dp)
        ) {
            if (valor.isEmpty()) Text(pista, color = Color(0xFF4A4A4A), fontSize = 14.sp)
            BasicTextField(
                value = valor,
                onValueChange = alCambiar,
                singleLine = true,
                textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                cursorBrush = SolidColor(Color.White),
                keyboardOptions = KeyboardOptions(keyboardType = tipo),
                modifier = Modifier.fillMaxWidth()
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
