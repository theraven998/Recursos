# Prompt LEGAL para apps

📄 **El prompt:** [`PROMPT.md`](PROMPT.md)

Prompt para **Claude Code** que genera la **política de privacidad**, los **términos y condiciones** y la
**página de eliminación de cuenta** que piden Google Play y App Store, a partir de lo que tu app hace de verdad.

## Cómo usarlo

1. Abre Claude Code en la raíz del proyecto de tu app.
2. Copia todo el contenido de [`PROMPT.md`](PROMPT.md) y pégalo.
3. Responde las preguntas (o escribe **express** para que use lo que detecte y marque lo demás como `[POR CONFIRMAR]`).

Qué hace:

- Revisa tu proyecto: permisos (Android e iOS), SDK de terceros, cuentas, pagos, copias de seguridad.
- Te pregunta solo lo que no puede deducir, en una sola ronda.
- Genera los documentos en español e inglés, según las leyes de tu país (Ley 1581 en Colombia, RGPD, LGPD, COPPA…).
- Crea una web nueva con `/privacidad`, `/terminos` y `/eliminar-cuenta`, o agrega esas rutas a tu web actual.
- Te deja las respuestas para *Seguridad de los datos* (Google Play) y *Privacidad de la app* (App Store Connect).

> Esto es un borrador técnico, no asesoría legal. Si tu app maneja datos sensibles (salud, finanzas) o cobra,
> pásalo por un abogado antes de publicar.

---

Hecho por [@dev__alejo](https://www.instagram.com/dev__alejo/) (Instagram) · [@dev.alejon](https://www.tiktok.com/@dev.alejon) (TikTok) · Serie *mejorando tu flujo como desarrollador hasta fin de año*, semana 6.

← [Todos los recursos](../../README.md)
