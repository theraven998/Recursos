# Los 10 patrones que delatan a una IA

Cada patrón trae cómo detectarlo y con qué reemplazarlo.

**1. Degradado morado → azul ("AI purple"), a veces en el texto**
- Detectar: `linear-gradient` / `bg-gradient-to-*` con `purple`, `violet`, `indigo`, `fuchsia` o
  `blue`; `bg-clip-text text-transparent`; hex tipo `#6366f1`, `#8b5cf6`, `#a855f7`.
- En su lugar: un solo color de marca, plano, con 1 neutro y 1 acento. Si quieres degradado, que
  sea entre dos tonos del mismo color y solo en un elemento (nunca en el texto de los títulos).

**2. Inter, Roboto o la fuente del sistema en todo, sin jerarquía ni identidad**
- Detectar: una sola familia (`Inter`, `Roboto`, `system-ui`, `font-sans` por defecto) en títulos y
  cuerpo; tamaños casi iguales entre títulos y párrafos.
- En su lugar: un par tipográfico (una fuente con carácter para títulos + una legible para el
  cuerpo), escala definida (por ejemplo 12/14/16/20/28/40/56), pesos con propósito y
  `letter-spacing` ajustado en títulos grandes. Todo como variables, no por componente.

**3. Hero centrado gigante + subtítulo vago + CTA + exactamente 3 cards idénticas**
- Detectar: hero con título centrado enorme, subtítulo tipo "Unlock your potential", "Build the
  future", "Todo lo que necesitas en un solo lugar", un botón, y debajo una fila de 3 cards iguales
  con ícono, título y dos líneas.
- En su lugar: un titular que diga qué hace el producto y para quién, en concreto. Alineación
  asimétrica o con una imagen/captura real del producto. Las características en 2, 4 o más ítems
  de tamaños distintos, o una lista con ejemplos reales, no tres cajas gemelas.

**4. Cards dentro de cards / todo es una card**
- Detectar: contenedor con borde y sombra que contiene otro con borde y sombra; secciones enteras
  envueltas en `rounded-xl border shadow`.
- En su lugar: un solo nivel de contenedor. Agrupa con espacio, líneas divisorias finas, fondo
  alterno o tipografía. Una card solo si es un objeto que se abre, se mueve o se compara.

**5. Glassmorphism / `backdrop-blur` decorativo sin función**
- Detectar: `backdrop-blur`, `backdrop-filter: blur`, `bg-white/10`, `bg-black/20` en cards o
  paneles que no están sobre una imagen o contenido que justifique el desenfoque.
- En su lugar: fondo sólido del token de superficie. Deja el blur solo donde tiene función
  (barra de navegación fija sobre contenido que hace scroll, modal sobre la página).

**6. Glows, bordes neón y sombras de colores sobre dark mode**
- Detectar: `box-shadow` con color saturado y radio grande, `shadow-purple-500/50`, `drop-shadow`
  de color, bordes `border-cyan-400` con brillo, `text-shadow` de neón.
- En su lugar: sombras neutras, cortas y suaves (o ninguna), y separación por contraste de
  superficie. El énfasis se logra con color plano en el elemento que importa, no con brillo.

**7. Emoji como íconos (🚀 ✨ 🧠 ⚡) dentro de un cuadrito redondeado**
- Detectar: emoji en títulos de sección, listas y cards, a menudo dentro de un `div` con fondo
  suave y `rounded-lg`.
- En su lugar: una librería de íconos consistente (Lucide, Phosphor, Heroicons, la que ya use el
  proyecto) con un solo grosor de trazo y tamaño, o sin ícono si no aporta. Cero emoji como UI.

**8. Dashboard genérico con métricas falsas**
- Detectar: tarjetas de "Total Users 12,345 ↑12%", gráficas con datos inventados, "Revenue",
  "Active Sessions" que no vienen de ningún dato real.
- En su lugar: muestra los datos reales del producto, o un estado vacío útil que explique qué va a
  aparecer y cómo empezar. Si es una demo, rotúlalo como ejemplo y usa datos con sentido del
  dominio, no números redondos y porcentajes verdes al azar.

**9. Mismo `border-radius` enorme, mismo padding y misma sombra en todo**
- Detectar: `rounded-2xl` / `rounded-3xl` en botones, inputs, cards, imágenes e íconos; el mismo
  `p-6` y `shadow-lg` repetidos en todos los componentes.
- En su lugar: una escala de radios por tipo de elemento (por ejemplo inputs y botones pequeños,
  cards medianas, avatares circulares) y una escala de espaciado y elevación (3 niveles máximo),
  todo en tokens. El ritmo visual sale de variar con intención, no de repetir.

**10. Dark mode obligatorio + una sola columna larga sin grid; terminal falso de tres bolitas**
- Detectar: solo existe tema oscuro sin razón de producto; todo en una columna centrada de
  `max-w-3xl` sin ninguna rejilla; bloque de "terminal" decorativo con tres círculos
  rojo/amarillo/verde.
- En su lugar: decide el tema según el público y el producto; si hay dark mode, que sea una opción
  que respete `prefers-color-scheme`, con ambos temas definidos en tokens. Usa una rejilla real
  (12 columnas o asimétrica) y anchos distintos por sección. Quita las bolitas: si muestras
  código o comandos, que sea un bloque de código real y copiable.
