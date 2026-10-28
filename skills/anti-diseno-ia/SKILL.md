---
name: anti-diseno-ia
description: Audita un proyecto web o app, detecta los 10 patrones de diseño que delatan que lo hizo una IA (degradado morado, tipografía genérica, hero con 3 cards gemelas, cards dentro de cards, glassmorphism, glows neón, emojis como íconos, dashboard con métricas falsas, mismos radios y sombras, dark mode obligatorio en una columna) y elimina solo los que el usuario elija, centralizando en tokens. También sirve como lista de prohibiciones al generar una interfaz nueva. Úsala cuando pidan quitar el look de IA, "que no parezca hecho con IA", revisar o limpiar el diseño, o antes de entregar una UI generada.
---

# Anti diseño de IA

Quitas de un proyecto los patrones de diseño que delatan que lo hizo una IA y los reemplazas por
decisiones con intención. Corriges solo lo que la persona elija, nada más.

Detalle de cada patrón (cómo detectarlo y con qué reemplazarlo): [references/patrones.md](references/patrones.md).
Estructura de tokens de ejemplo: [references/tokens.md](references/tokens.md).
Ayuda opcional para la auditoría: `bash scripts/auditar.sh <carpeta>` (solo lee). Detecta el stack (web, React Native, Jetpack Compose, Android XML, SwiftUI, Flutter) y cuenta señales por patrón con los 3 archivos que más tienen. Son indicios: confirma leyendo esos archivos; la decisión es tuya.

## Reglas
- No toques nada antes de que la persona elija qué patrones quitar (Fase 2).
- No rompas funcionalidad: no cambies lógica, rutas, nombres de props ni textos legales. Si un cambio visual obliga a tocar lógica, pregunta.
- Centraliza en tokens (variables CSS, `@theme` de Tailwind, `tailwind.config`, tema del framework) en vez de parches sueltos. Si el proyecto ya tiene tokens, extiéndelos.
- No inventes marca: si faltan colores, fuentes o referencias, pregunta.
- Sin commits, deploy ni dependencias nuevas (fuentes, íconos) sin confirmación.
- Un patrón a la vez, verificando que el proyecto compile. Español claro, tablas cortas.

## Fase 1: auditoría (sin preguntar, sin modificar)
1. Detecta el stack (Next.js, Vite, Astro, React Native, Flutter, HTML plano, Tailwind, CSS modules…) y dónde viven estilos, tokens y componentes.
2. Revisa CSS/Tailwind, componentes, tokens, layouts y copy (titulares, subtítulos, botones, textos de ejemplo). Usa `grep` y lee las pantallas principales.
3. Muestra esta tabla con los 10 patrones, aunque sea con "No":

| # | Patrón | ¿Aparece? | Dónde (archivo:línea y ejemplo corto) | Gravedad |
|---|---|---|---|---|

Gravedad: **alta** (primera pantalla o todo el sitio), **media** (varias secciones), **baja** (puntual).

**Si no hay código** (proyecto nuevo): haz una sola pregunta de dirección de diseño y usa los 10 patrones como **prohibiciones** al generar la interfaz. Al terminar cada pantalla, revisa la lista y confirma que no cae en ninguno.

## Fase 2: elegir qué quitar
Pregunta en un solo mensaje:
1. **¿Qué patrones quieres eliminar?** Responde "todos", números como "1,3,7" o "todos menos 5". Recomienda los de gravedad alta.
2. **Dirección de diseño**, solo lo que no puedas deducir del código:
   - Personalidad de marca o referencias (2 o 3 sitios, o palabras como "sobrio", "editorial", "técnico"). Si no sabe, propón 2 direcciones con paleta y fuentes concretas.
   - Color de marca (hex) o propón uno.
   - ¿Dark mode es necesario? (sí, no, o ambos según el sistema).

Espera la respuesta antes de seguir.

## Fase 3: corregir
1. Define o ajusta primero los **tokens**: colores, tipografía, radios, espaciado y elevación (ver [references/tokens.md](references/tokens.md)).
2. Aplica solo los patrones elegidos, uno por uno, con el "en su lugar" de [references/patrones.md](references/patrones.md), sin valores sueltos (`#8b5cf6`, `rounded-3xl`, `shadow-xl` repetidos).
3. Reescribe el copy genérico afectado con lo que el producto hace de verdad. Si no lo sabes, deja una propuesta marcada [POR CONFIRMAR].
4. Corre el build o linter y revisa que nada se rompió. Si hay servidor de desarrollo, indica la URL.
5. Pregunta antes de hacer commit o deploy.

## Resumen final
- **Qué cambió por patrón**: patrón → archivos → qué había → qué hay ahora.
- **Qué quedó y por qué**: patrones no elegidos o dejados a propósito, y los [POR CONFIRMAR].
- **Tokens nuevos**: dónde están y cómo usarlos.
- **Captura antes/después**: sugiere capturar 2 o 3 pantallas principales con el mismo tamaño y contenido.
- Pregunta si quiere hacer commit, con un mensaje simple.
