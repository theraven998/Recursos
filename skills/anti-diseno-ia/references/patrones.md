# Los 20 patrones que delatan a una IA

Cada patrón trae cómo detectarlo y con qué reemplazarlo. Del 1 al 10 son de estilo visual; del 11 al 20,
de estructura y contenido de plantilla.

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

**11. Pastilla encima del título ("✨ Nuevo", "Powered by AI", "Presentamos X →")**
- Detectar: un `span` o `div` con `rounded-full`, borde fino y texto pequeño justo antes del `h1`,
  muchas veces con emoji, brillo o flecha.
- En su lugar: quítala. Si de verdad hay una novedad, dila en el titular o en una línea de texto
  normal con fecha y enlace a lo que cambió.

**12. Fondo con rejilla de puntos o líneas y orbes de color borrosos detrás del hero**
- Detectar: `bg-grid`, `radial-gradient` repetido como patrón, SVG de cuadrícula, `div` absolutos con
  `blur-3xl` y `bg-purple-500/30` (o similar) detrás del título.
- En su lugar: fondo del token de superficie. Si el hero necesita fuerza, que sea una captura real
  del producto, una foto propia o tipografía grande con buen contraste.

**13. Copy de relleno ("Seamless", "Revolutionize", "Empower", "Next-gen", "sin esfuerzo")**
- Detectar: verbos y adjetivos vacíos: seamless, effortless, revolutionize, empower, elevate,
  supercharge, next-gen, cutting-edge, game-changer, "potencia tu", "revoluciona", "lleva tu X al
  siguiente nivel", "sin esfuerzo", "todo en un solo lugar".
- En su lugar: frases con sujeto, acción y resultado medible ("Carga tu inventario desde Excel en 2
  minutos"). Si no sabes el dato, márcalo [POR CONFIRMAR]; no lo inventes.

**14. Testimonios inventados (avatar sonriente, 5 estrellas, "Sarah J., CEO de Acme")**
- Detectar: arreglos de testimonios con nombres genéricos, `pravatar`, `randomuser`, `unsplash` de
  caras, estrellas fijas en 5, cargos tipo "CEO", "Founder" sin empresa real.
- En su lugar: testimonios reales con permiso (nombre, empresa y enlace), una captura de un mensaje
  real, o nada. Una sección vacía es mejor que una falsa.

**15. Franja de logos de "empresas que confían" y cifras redondas (10K+, 99.9 %, 24/7)**
- Detectar: "Trusted by", "Empresas que confían en nosotros", logos de Acme, Google o Stripe en gris
  sin relación real; contadores tipo `10K+`, `500+`, `99.9%`, `24/7`, `4.9/5`.
- En su lugar: solo clientes y cifras verificables, con la fuente. Si aún no hay, muestra el producto
  funcionando o un caso concreto.

**16. Precios en 3 planes con el del medio "Más popular" resaltado**
- Detectar: tres columnas de precio (Starter / Pro / Enterprise), la del centro más grande, con
  borde de color o degradado y etiqueta "Most popular" / "Más popular"; checks verdes iguales.
- En su lugar: los planes que el negocio tiene de verdad (aunque sea uno), con lo que cambia entre
  ellos en una tabla comparativa. Destaca un plan solo si hay una razón real.

**17. Animaciones en todo (fade-in al hacer scroll en cada sección, hover que agranda cada card)**
- Detectar: `framer-motion` / `motion.div` con `initial={{ opacity: 0, y: 20 }}` en cada sección,
  `AOS`, `animate-fade-in`, `hover:scale-105` en todas las cards, `transition-all` en todo.
- En su lugar: movimiento solo donde explica algo (un cambio de estado, un paso de un flujo).
  Respeta `prefers-reduced-motion`. El contenido se ve al cargar, sin esperar animaciones.

**18. Botón principal con degradado y flechita ("Empieza gratis →") + secundario fantasma ("Saber más")**
- Detectar: par de botones repetido en hero y cierre: uno con `bg-gradient-to-*` o glow y `→` /
  `ArrowRight`, otro con borde y fondo transparente que dice "Learn more" / "Saber más".
- En su lugar: un solo botón principal en color de marca plano con un verbo concreto de lo que pasa
  al hacer clic ("Crear mi cuenta", "Ver la demo de 2 minutos"). El secundario solo si lleva a algo
  distinto y útil.

**19. Navbar flotante en forma de píldora con blur**
- Detectar: `nav` con `fixed top-4 left-1/2 -translate-x-1/2 rounded-full backdrop-blur border`,
  separado del borde de la página y centrado.
- En su lugar: una barra de navegación anclada al borde, a lo ancho de la rejilla, con fondo sólido
  o blur solo si pasa sobre contenido. Que sus enlaces lleven a páginas que existen.

**20. Cierre de plantilla: banner "¿Listo para empezar?" + FAQ en acordeón + footer de 4 columnas con enlaces a `#`**
- Detectar: sección final "Ready to get started?" / "¿Listo para empezar?" con el mismo par de
  botones; FAQ genérica en acordeón; footer con columnas Producto / Empresa / Recursos / Legal cuyos
  enlaces van a `#` o a páginas que no existen; "© 2024 Company, Inc.".
- En su lugar: cierra con la siguiente acción concreta y datos de contacto reales. FAQ solo con
  preguntas que la gente hace de verdad. Footer con los enlaces que existen; quita los `#`.
