#!/usr/bin/env python3
"""Servidor del endpoint de Claude Usage Bar (para el widget del teléfono).

GET /uso con «Authorization: Bearer <token>» (token en ~/.config/claude-usage-bar/token)
→ mismos números que el módulo de Waybar (claude-usage-bar.py), en JSON.
Consulta la API de Anthropic solo cuando llega una petición (caché mínima de 30 s
para no repetir si se toca varias veces seguidas). Escucha solo en 127.0.0.1;
desde fuera llega por un túnel (Cloudflare Worker + túnel, o Tailscale). Ver el README.
"""
import hmac
import importlib.util
import json
import os
import time
from datetime import datetime, timezone
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path

PUERTO = int(os.environ.get("CLAUDE_BAR_PORT", "3430"))
MIN_INTERVALO = 30
# Token que debe mandar el teléfono. Créalo con:  python3 -c "import secrets;print(secrets.token_urlsafe(32))"
TOKEN_FILE = Path(os.environ.get("CLAUDE_BAR_TOKEN_FILE", Path.home() / ".config/claude-usage-bar/token"))
TOKEN = TOKEN_FILE.read_text().strip()
# Script de la barra de Waybar (de aquí se reutilizan pedir_uso y el cálculo del reinicio).
BARRA = Path(os.environ.get("CLAUDE_BAR_SCRIPT", Path(__file__).resolve().parent.parent / "waybar/claude-usage-bar.py"))

spec = importlib.util.spec_from_file_location("cs", BARRA)
cs = importlib.util.module_from_spec(spec)
spec.loader.exec_module(cs)

DIAS = ["lun", "mar", "mié", "jue", "vie", "sáb", "dom"]


def leer_uso():
    """Pide a la API (salvo que la caché tenga < 30 s); si falla, usa la caché."""
    if cs.CACHE.exists() and time.time() - cs.CACHE.stat().st_mtime < MIN_INTERVALO:
        return json.loads(cs.CACHE.read_text()), False
    try:
        nuevo = cs.pedir_uso()
        cs.CACHE.parent.mkdir(parents=True, exist_ok=True)
        cs.CACHE.write_text(json.dumps(nuevo))
        return nuevo, False
    except Exception:
        return (json.loads(cs.CACHE.read_text()) if cs.CACHE.exists() else None), True


def calcular():
    datos, viejo = leer_uso()
    if not datos or not datos.get("seven_day"):
        return None
    ahora = datetime.now(timezone.utc)
    api_reinicio = datetime.fromisoformat(datos["seven_day"]["resets_at"])
    reinicio = datetime.fromtimestamp(cs.proximo_reinicio(ahora.timestamp(), datos) or api_reinicio.timestamp(), timezone.utc)
    usado = datos["seven_day"]["utilization"]
    if api_reinicio <= ahora and viejo:
        usado = None
    semana = max(0.0, min(100.0, (ahora.timestamp() - (reinicio.timestamp() - cs.SEMANA)) / cs.SEMANA * 100))
    usado = float(usado or 0)
    sesion = (datos.get("five_hour") or {}).get("utilization")
    diff = usado - semana
    falta = int((reinicio - ahora).total_seconds())
    dias, resto = divmod(falta, 86400)
    local = reinicio.astimezone()
    return {
        "semana": round(semana, 1),
        "uso": round(usado, 1),
        "sesion": None if sesion is None else round(float(sesion), 1),
        "diff": round(diff, 1),
        "estado": "ok" if diff <= 0 else "warning" if diff <= 10 else "critical",
        "reinicio": local.isoformat(timespec="seconds"),
        "reinicio_texto": f"{DIAS[local.weekday()]} {local:%d/%m %I:%M %p}",
        "falta": f"{dias} d {resto // 3600} h",
        "consultado": datetime.now().astimezone().isoformat(timespec="seconds"),
        "cache": viejo,
    }


class H(BaseHTTPRequestHandler):
    def _json(self, code, obj):
        cuerpo = json.dumps(obj, ensure_ascii=False).encode()
        self.send_response(code)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Cache-Control", "no-store")
        self.send_header("Content-Length", str(len(cuerpo)))
        self.end_headers()
        self.wfile.write(cuerpo)

    def do_GET(self):
        if self.path.split("?")[0] != "/uso":
            return self._json(404, {"error": "no existe"})
        dado = self.headers.get("Authorization", "").removeprefix("Bearer ").strip()
        if not hmac.compare_digest(dado, TOKEN):
            return self._json(401, {"error": "no autorizado"})
        r = calcular()
        if r is None:
            return self._json(503, {"error": "sin datos de uso"})
        self._json(200, r)

    def log_message(self, fmt, *args):
        pass


if __name__ == "__main__":
    ThreadingHTTPServer(("127.0.0.1", PUERTO), H).serve_forever()
