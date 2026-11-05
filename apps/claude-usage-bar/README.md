# Claude Usage Bar

Una barra que te dice si vas bien o mal con tu límite semanal de Claude: compara **cuánto de la semana ha pasado** contra **cuánto de tu cupo has gastado**.

```
󰔟 ▰▰▰▰▰▰▰▱ 86%  ·  󰚩 89%
```

- Verde: vas por debajo del ritmo de la semana. Amarillo: hasta 10 puntos por encima. Rojo: más de 10 puntos por encima.
- El tooltip muestra el uso semanal, la sesión de 5 h y cuándo reinicia.
- Dos formas de verla: una **barra en Waybar** (Linux) y un **widget en tu teléfono Android**.

Del video de la Semana 8 de la serie Flujo Dev (palabra clave: **BARRA**).

## Qué trae

| Carpeta | Qué es |
|---|---|
| `waybar/` | El script de la barra, un ejemplo del bloque de configuración y el CSS |
| `servidor/` | El servidor local que expone tu uso en JSON + un Cloudflare Worker de ejemplo |
| `android/` | Código de la app y el widget (Kotlin) |

El uso se lee del endpoint `api.anthropic.com/api/oauth/usage` con la sesión de Claude Code (`~/.claude/.credentials.json`). Necesitas haber iniciado sesión en Claude Code en el PC con una cuenta Pro o Max.

## 1. La barra en Waybar

1. Copia el script y hazlo ejecutable:
   ```bash
   mkdir -p ~/.local/bin
   cp waybar/claude-usage-bar.py ~/.local/bin/
   chmod +x ~/.local/bin/claude-usage-bar.py
   ```
2. Pega el bloque de `waybar/config-ejemplo.jsonc` en `~/.config/waybar/config` y agrega `"custom/claude-usage-bar"` a `modules-left`, `modules-center` o `modules-right`.
3. Pega `waybar/style-ejemplo.css` en `~/.config/waybar/style.css`.
4. Reinicia Waybar: `pkill waybar; waybar &`.

Necesitas una fuente con íconos (Nerd Font) para los símbolos del texto.

### Cuándo reinicia tu semana

Por defecto el script usa el campo `resets_at` que devuelve la API, así que no tienes que configurar nada. Si en tu caso la API lo entrega corrido y la barra salta, puedes fijar el día y la hora local con variables de entorno:

| Variable | Qué hace | Ejemplo |
|---|---|---|
| `CLAUDE_BAR_RESET_DAY` | Día del reinicio, lunes=0 … domingo=6 | `4` (viernes) |
| `CLAUDE_BAR_RESET_HOUR` | Hora local del reinicio, 0 a 23 | `12` |
| `CLAUDE_BAR_WIDTH` | Casillas de la barra (por defecto 8) | `10` |

Si defines día y hora, mandan sobre la API. En el bloque de Waybar se pasan así: `"exec": "env CLAUDE_BAR_RESET_DAY=4 CLAUDE_BAR_RESET_HOUR=12 ~/.local/bin/claude-usage-bar.py"`.

## 2. El widget del teléfono: monta tu propio endpoint

**Importante:** la app no trae ningún servidor. El uso sale de tu PC, así que **tienes que montar tu propio endpoint** que consulte tu uso, igual que lo hago yo: un servidor local en tu PC, expuesto a internet (o a tu red privada) con un túnel, y protegido con un token que tú inventas. Después instalas el APK y en la app escribes **tu** URL y **tu** token. Nada de lo mío viene incluido.

### 2.1 Servidor local

1. Genera un token y guárdalo:
   ```bash
   mkdir -p ~/.config/claude-usage-bar
   python3 -c "import secrets; print(secrets.token_urlsafe(32))" > ~/.config/claude-usage-bar/token
   chmod 600 ~/.config/claude-usage-bar/token
   ```
2. Pruébalo a mano (escucha solo en `127.0.0.1:3430`):
   ```bash
   python3 servidor/claude-usage-server.py &
   curl -H "Authorization: Bearer $(cat ~/.config/claude-usage-bar/token)" http://127.0.0.1:3430/uso
   ```
   Debe devolver un JSON con `semana`, `uso`, `sesion`, `estado`, `reinicio_texto`, etc.
3. Para que arranque solo, usa `servidor/claude-usage-bar.service` (ajusta la ruta del `ExecStart`) como servicio de usuario de systemd.

El servidor reutiliza `waybar/claude-usage-bar.py`, así que deja las dos carpetas juntas. Variables opcionales: `CLAUDE_BAR_PORT`, `CLAUDE_BAR_TOKEN_FILE`, `CLAUDE_BAR_SCRIPT`.

### 2.2 Exponerlo al teléfono

Elige una de estas dos formas:

**A. Tailscale (lo más simple).** Instala Tailscale en el PC y en el teléfono. Si necesitas que el servidor sea alcanzable desde la red de Tailscale, escúchalo en la IP de Tailscale o usa `tailscale serve`. La URL queda como `https://tu-pc.tu-tailnet.ts.net/uso`.

**B. Cloudflare (túnel + Worker), como lo tengo yo.** Necesitas un dominio en Cloudflare.
1. Crea un túnel con `cloudflared` que apunte a `http://127.0.0.1:3430` y un servicio Workers VPC sobre ese túnel.
2. En `servidor/worker/` pon tu dominio y el id de tu servicio VPC en `wrangler.jsonc`.
3. Guarda el mismo token como secreto del Worker: `npx wrangler secret put TOKEN`.
4. Despliega: `npx wrangler deploy`.

La URL queda como `https://uso.tu-dominio.example/uso`. Con cualquiera de las dos, el servidor solo responde si llega el token correcto.

### 2.3 La app Android

1. Descarga e instala [claude-usage-bar.apk](https://github.com/theraven998/Recursos/raw/main/apps/claude-usage-bar/claude-usage-bar.apk) (permite instalar apps de origen desconocido), o compílalo tú.
2. Abre la app. En **Ajustes** escribe:
   - **URL del endpoint:** `https://tu-endpoint.example.com/uso`
   - **Token:** `TU_TOKEN` (el del paso 2.1)
3. Toca **Guardar**: la app consulta una vez para comprobar que responde.
4. Agrega el widget a la pantalla de inicio. Si no hay configuración, el widget dice «Toca para configurar». Tócalo para refrescar.

Compilar tú mismo (necesitas JDK 17 y el SDK de Android):
```bash
cd android
echo "sdk.dir=$ANDROID_HOME" > local.properties
./gradlew assembleDebug        # APK en app/build/outputs/apk/debug/
```
Los builds de Android pesan en RAM: si tu PC es modesto, usa `--max-workers=2` o compila en otra máquina.

## Seguridad

- Tu token vive solo en tu PC (archivo `token`), en el secreto del Worker y en el teléfono. No lo subas a ningún repo.
- El servidor solo escucha en `127.0.0.1`; lo que lo expone al mundo es tu túnel.
- El script lee tu credencial de Claude Code solo para pedir tu uso a Anthropic. No la envía a ningún otro lado.

## Licencias

El código está bajo licencia [MIT](../../LICENSE): puedes usarlo, modificarlo y redistribuirlo manteniendo el aviso de copyright. La fuente Doto del widget (`android/app/src/main/res/font/`) es de sus autores bajo la licencia SIL Open Font License.
