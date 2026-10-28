# Skill anti diseño de IA

Skill de **Claude Code** que quita de tu proyecto web o app los **20 patrones de diseño que delatan que lo hizo una IA** y los reemplaza por decisiones con intención.

## Instalar

Un solo comando (copia la skill a tu carpeta personal de skills):

```bash
git clone --depth 1 https://github.com/theraven998/Recursos.git /tmp/recursos && mkdir -p ~/.claude/skills && cp -r /tmp/recursos/skills/anti-diseno-ia ~/.claude/skills/ && rm -rf /tmp/recursos
```

Para instalarla solo en un proyecto, copia la carpeta a `.claude/skills/anti-diseno-ia/` dentro de ese proyecto.
Reinicia Claude Code para que la cargue.

## Usar

Abre Claude Code en la raíz de tu proyecto y escribe `/anti-diseno-ia` (o pídele "quita el look de IA de mi diseño").

1. **Auditoría:** te muestra una tabla con los 20 patrones: si aparecen, dónde y qué tan graves son.
2. **Elegir:** respondes qué quitar (`todos`, `1,3,14`, `1-10` o `todos menos 5`) y, si falta, personalidad de marca, color y dark mode. No toca nada antes.
3. **Corregir:** aplica solo lo elegido, centralizado en tokens (color, tipografía, radios, espaciado), sin romper funcionalidad y sin commits ni deploy sin tu confirmación.
4. **Resumen:** qué cambió por patrón, qué quedó y por qué, y qué capturas tomar antes y después.

En un **proyecto nuevo** sin código, usa los 20 patrones como prohibiciones al generar la interfaz.

## Qué incluye

- `SKILL.md`: el flujo en fases.
- `references/patrones.md`: los 20 patrones, cómo detectarlos y con qué reemplazarlos.
- `references/tokens.md`: estructura de tokens de ejemplo.
- `scripts/auditar.sh`: conteo opcional de señales con `grep` para web, React Native, Android (Compose y XML), SwiftUI y Flutter, con los archivos donde más aparecen (solo lee, no modifica nada).

## Los 20 patrones

Estilo visual:

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

Estructura y contenido de plantilla:

11. Pastilla encima del título ("✨ Nuevo", "Powered by AI")
12. Rejilla de puntos y orbes de color borrosos detrás del hero
13. Copy de relleno: "Seamless", "Revolutionize", "Empower", "sin esfuerzo"
14. Testimonios inventados: avatar, 5 estrellas, "Sarah J., CEO"
15. Logos de "empresas que confían" y cifras redondas (10K+, 99.9 %, 24/7)
16. Precios en 3 planes con el del medio "Más popular"
17. Animaciones en todo: fade-in al hacer scroll y hover que agranda cada card
18. Botón con degradado y flechita ("Empieza gratis →") + secundario "Saber más"
19. Navbar flotante en forma de píldora con blur
20. Cierre de plantilla: "¿Listo para empezar?" + FAQ en acordeón + footer con enlaces a `#`

## Créditos

La estructura de skill (SKILL.md + scripts) se inspira en [design-slop-skill](https://github.com/hahahahahahahahah6/design-slop-skill) (MIT, hao li). El contenido, los 20 patrones y el flujo son propios.

---

Hecho por [@dev__alejo](https://www.instagram.com/dev__alejo/) (Instagram) · [@dev.alejon](https://www.tiktok.com/@dev.alejon) (TikTok) · Serie *mejorando tu flujo como desarrollador hasta fin de año*, semana 8.

← [Todos los recursos](../../README.md)
