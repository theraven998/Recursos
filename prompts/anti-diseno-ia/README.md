# Prompt anti diseño de IA

📄 **El prompt:** [`PROMPT.md`](PROMPT.md)

Prompt para **Claude Code** (o cualquier agente de código) que quita de tu proyecto web o app los
**10 patrones de diseño que delatan que lo hizo una IA**, y los reemplaza por decisiones con intención.

## Cómo usarlo

1. Abre Claude Code en la raíz de tu proyecto.
2. Copia todo el contenido de [`PROMPT.md`](PROMPT.md) y pégalo.
3. Mira la tabla de auditoría, elige qué patrones quitar (`todos`, `1,3,7` o `todos menos 5`) y responde las preguntas de dirección de diseño.
4. Revisa los cambios y haz una captura antes y después.

Qué hace:

- **Fase 1, auditoría:** revisa CSS/Tailwind, componentes, tokens y copy, y te muestra una tabla con los 10 patrones: si aparecen, dónde y qué tan graves son.
- **Fase 2, elegir:** te pregunta qué quitar y, solo si falta, la personalidad de marca, el color y si necesitas dark mode. No toca nada antes de tu respuesta.
- **Fase 3, corregir:** aplica solo lo que elegiste, centralizado en tokens (color, tipografía, radios, espaciado), sin romper funcionalidad y sin commits ni deploy sin tu confirmación.
- **Resumen final:** qué cambió por patrón, qué quedó y por qué, y qué capturas tomar.
- **Proyecto nuevo:** si no hay código, usa los 10 patrones como prohibiciones al generar la interfaz.

## Los 10 patrones

1. Degradado morado → azul ("AI purple"), a veces en el texto
2. Inter, Roboto o fuente del sistema en todo, sin jerarquía ni identidad
3. Hero centrado gigante + subtítulo vago + CTA + exactamente 3 cards idénticas
4. Cards dentro de cards / todo es una card
5. Glassmorphism / `backdrop-blur` decorativo sin función
6. Glows, bordes neón y sombras de colores sobre dark mode
7. Emoji como íconos (🚀 ✨ 🧠 ⚡) en un cuadrito redondeado
8. Dashboard genérico con métricas falsas
9. Mismo `border-radius` enorme, mismo padding y misma sombra en todo
10. Dark mode obligatorio + una sola columna larga sin grid; terminal falso de tres bolitas

---

Hecho por [@dev__alejo](https://www.instagram.com/dev__alejo/) (Instagram) · [@dev.alejon](https://www.tiktok.com/@dev.alejon) (TikTok) · Serie *mejorando tu flujo como desarrollador hasta fin de año*, semana 8.

← [Todos los recursos](../../README.md)
