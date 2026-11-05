#!/usr/bin/env python3
"""Claude Usage Bar: módulo de Waybar con el avance de la semana vs el uso semanal de Claude.

Lee el uso de https://api.anthropic.com/api/oauth/usage con el token de
Claude Code (~/.claude/.credentials.json). Guarda la respuesta en caché
5 min para no golpear la API; si el token venció o no hay red, usa la
última respuesta guardada (el avance de la semana se sigue calculando).
"""
import json
import os
import time
import urllib.request
from datetime import datetime, timezone
from pathlib import Path


# --- Configuración (variables de entorno, todas opcionales) -----------------
# Por defecto el reinicio semanal sale de `resets_at` de la API (lo más fiable).
# Si tu API lo entrega corrido y la barra salta, fija tú el reinicio:
#   CLAUDE_BAR_RESET_DAY   día de la semana, lunes=0 … domingo=6   (ej. 4 = viernes)
#   CLAUDE_BAR_RESET_HOUR  hora local, 0-23                        (ej. 12)
# Si defines los dos, mandan sobre la API.
REINICIO_DIA = int(os.environ["CLAUDE_BAR_RESET_DAY"]) if "CLAUDE_BAR_RESET_DAY" in os.environ else None
REINICIO_HORA = int(os.environ.get("CLAUDE_BAR_RESET_HOUR", "0")) if REINICIO_DIA is not None else None
ANCHO = int(os.environ.get("CLAUDE_BAR_WIDTH", "8"))  # casillas de la barra

CRED = Path.home() / ".claude/.credentials.json"
CACHE = Path.home() / ".cache/claude-usage-bar.json"
TTL = 300
SEMANA = 7 * 24 * 3600


def pedir_uso():
    token = json.loads(CRED.read_text())["claudeAiOauth"]["accessToken"]
    req = urllib.request.Request(
        "https://api.anthropic.com/api/oauth/usage",
        headers={"Authorization": f"Bearer {token}", "anthropic-beta": "oauth-2025-04-20"},
    )
    return json.loads(urllib.request.urlopen(req, timeout=8).read())


def uso():
    datos = None
    if CACHE.exists():
        datos = json.loads(CACHE.read_text())
        fresca = time.time() - CACHE.stat().st_mtime < TTL
        # Si en la caché ya pasó el reinicio, se pide de nuevo para que el uso baje a la hora exacta
        reinicio = proximo_reinicio(CACHE.stat().st_mtime, datos)
        if fresca and (reinicio is None or datetime.now(timezone.utc).timestamp() < reinicio):
            return datos, False
    try:
        nuevo = pedir_uso()
        CACHE.parent.mkdir(parents=True, exist_ok=True)
        CACHE.write_text(json.dumps(nuevo))
        return nuevo, False
    except Exception:
        return datos, True


def proximo_reinicio(desde, datos=None):
    """Timestamp del próximo reinicio semanal estrictamente después de `desde`.

    Con CLAUDE_BAR_RESET_DAY/HOUR usa ese día y hora local; si no, `resets_at` de la API
    (avanzando de a semanas si ya quedó en el pasado), o None si no hay dato.
    """
    if REINICIO_DIA is not None:
        d = datetime.fromtimestamp(desde).astimezone()
        r = d.replace(hour=REINICIO_HORA, minute=0, second=0, microsecond=0)
        r = datetime.fromtimestamp(r.timestamp() + ((REINICIO_DIA - d.weekday()) % 7) * 86400).astimezone()
        r = r.replace(hour=REINICIO_HORA, minute=0, second=0, microsecond=0)
        if r.timestamp() <= desde:
            r = datetime.fromtimestamp(r.timestamp() + SEMANA).astimezone()
        return r.timestamp()
    try:
        t = datetime.fromisoformat(datos["seven_day"]["resets_at"]).timestamp()
    except Exception:
        return None
    while t <= desde:
        t += SEMANA
    return t


def barra(pct):
    llenos = round(pct / 100 * ANCHO)
    return "▰" * llenos + "▱" * (ANCHO - llenos)


def main():
    datos, viejo = uso()
    if not datos or not datos.get("seven_day"):
        print(json.dumps({"text": "󰔟 sin datos", "class": "error", "tooltip": "No se pudo leer el uso de Claude"}))
        return

    ahora = datetime.now(timezone.utc)
    api_reinicio = datetime.fromisoformat(datos["seven_day"]["resets_at"])
    reinicio = datetime.fromtimestamp(proximo_reinicio(ahora.timestamp(), datos) or api_reinicio.timestamp(), timezone.utc)
    # Uso de la semana anterior en caché (no se pudo refrescar tras el reinicio): no cuenta
    if api_reinicio <= ahora and viejo:
        datos["seven_day"]["utilization"] = None
    inicio = reinicio.timestamp() - SEMANA
    semana = max(0.0, min(100.0, (ahora.timestamp() - inicio) / SEMANA * 100))
    usado = float(datos["seven_day"]["utilization"] or 0)
    sesion = (datos.get("five_hour") or {}).get("utilization")

    diff = usado - semana
    clase = "ok" if diff <= 0 else "warning" if diff <= 10 else "critical"

    falta = reinicio - ahora
    dias, resto = divmod(int(falta.total_seconds()), 86400)
    horas = resto // 3600
    local = reinicio.astimezone()
    dias_es = ["lun", "mar", "mié", "jue", "vie", "sáb", "dom"]

    tip = [
        f"Semana: {semana:.0f} % transcurrido",
        f"Uso semanal: {usado:.0f} %  ({'+' if diff > 0 else ''}{diff:.0f} pts vs semana)",
    ]
    if sesion is not None:
        tip.append(f"Sesión 5 h: {sesion:.0f} %")
    tip.append(f"Reinicia {dias_es[local.weekday()]} {local:%d/%m %I:%M %p} (en {dias} d {horas} h)")
    if REINICIO_DIA is not None and abs(api_reinicio.timestamp() - reinicio.timestamp()) > 3600 and api_reinicio > ahora:
        tip.append(f"⚠ la API dice reinicio {api_reinicio.astimezone():%d/%m %I:%M %p}")
    if viejo:
        tip.append("⚠ datos de uso en caché (token vencido o sin red)")

    print(json.dumps({
        "text": f"󰔟 {barra(semana)} {semana:.0f}%  ·  󰚩 {usado:.0f}%",
        "tooltip": "\n".join(tip),
        "class": clase,
        "percentage": round(semana),
    }))


if __name__ == "__main__":
    main()
