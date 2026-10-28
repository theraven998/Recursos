#!/usr/bin/env bash
# Cuenta señales de los 10 patrones. Solo lee; no modifica nada.
# Sirve para web (CSS, Tailwind, JSX, Vue, Svelte, Astro), React Native, Android (Jetpack Compose y XML),
# SwiftUI y Flutter.
# Uso: bash auditar.sh [carpeta]   (por defecto, la carpeta actual)
set -u
dir="${1:-.}"
cd "$dir" 2>/dev/null || { echo "No existe la carpeta: $dir"; exit 1; }

inc=(--include='*.css' --include='*.scss' --include='*.sass' --include='*.less' --include='*.html'
     --include='*.js' --include='*.jsx' --include='*.ts' --include='*.tsx' --include='*.mjs'
     --include='*.vue' --include='*.svelte' --include='*.astro'
     --include='*.kt' --include='*.xml' --include='*.swift' --include='*.dart')
exc=(--exclude-dir=node_modules --exclude-dir=.next --exclude-dir=.nuxt --exclude-dir=dist --exclude-dir=build
     --exclude-dir=out --exclude-dir=.git --exclude-dir=.gradle --exclude-dir=.kotlin --exclude-dir=.idea
     --exclude-dir=Pods --exclude-dir=DerivedData --exclude-dir=.dart_tool --exclude-dir=.expo
     --exclude-dir=coverage --exclude-dir=vendor)

# Qué stack hay (solo para que leas el conteo con contexto)
hay() { grep -rqlE "${inc[@]}" "${exc[@]}" -- "$1" . 2>/dev/null; }
stack=()
hay 'className=|<div|@tailwind|font-family' && stack+=("web")
hay 'StyleSheet\.create' && stack+=("React Native")
hay '@Composable' && stack+=("Jetpack Compose")
hay 'android:background|<shape' && stack+=("Android XML")
hay 'import SwiftUI' && stack+=("SwiftUI")
hay 'import .package:flutter' && stack+=("Flutter")
echo "Carpeta: $dir"
echo "Stack detectado: ${stack[*]:-ninguno (¿carpeta correcta?)}"
echo

# c "nombre" 'regex': líneas que coinciden + los 3 archivos con más coincidencias
c() {
  local res total
  res=$(grep -rEn "${inc[@]}" "${exc[@]}" -- "$2" . 2>/dev/null)
  total=$(printf '%s' "$res" | grep -c . )
  printf '%-22s %4s\n' "$1" "$total"
  [ "$total" -gt 0 ] && printf '%s\n' "$res" | cut -d: -f1 | sort | uniq -c | sort -rn | head -3 |
    sed 's#\./##' | awk '{printf "    %4s  %s\n", $1, $2}'
  return 0
}

c "1 morado/degradado" \
  'purple|violet|indigo|fuchsia|#6366f1|#8b5cf6|#a855f7|#7c3aed|bg-clip-text|bg-gradient-to-|linear-gradient|Brush\.(linear|horizontal|vertical)Gradient|LinearGradient\(|<gradient'
c "2 Inter/Roboto/sistema" \
  'font-family:[^;]*(Inter|Roboto|Arial|system-ui)|font-(inter|roboto)|fontFamily[ :=]*["'"'"']?(Inter|Roboto)|FontFamily\.(Default|SansSerif)|R\.font\.(inter|roboto)|fontFamily: *.(Inter|Roboto)|\.font\(\.system'
c "3 hero/3 cards" \
  'Unlock|Build the future|Supercharge|todo lo que necesitas|Lleva tu .* al siguiente nivel|grid-cols-3|repeat\(3, *1fr\)'
c "4 card en card" \
  'rounded-(xl|2xl|3xl)[^"]*border[^"]*shadow|<Card|Card\(|ElevatedCard|OutlinedCard|CardView|\.background\(.*RoundedRectangle'
c "5 glass/blur" \
  'backdrop-(blur|filter)|Modifier\.blur|\.blur\(|BackdropFilter|ImageFilter\.blur|\.ultraThinMaterial|\.thinMaterial|BlurView'
c "6 glow/neón" \
  'shadow-(purple|violet|cyan|blue|pink|fuchsia)-|drop-shadow|text-shadow|box-shadow:[^;]*[0-9]{2}px|shadowColor|spotColor|ambientColor|\.shadow\(color:|glow|halo|BoxShadow\('
c "7 emoji como ícono" \
  '🚀|✨|🧠|⚡|🔥|🎯|💡|📈|🛡|💎'
c "8 métricas falsas" \
  'Total Users|Active Sessions|Revenue|12,345|\+12%|99\.9%|fake|mock|dummy|lorem'
c "9 mismo radio" \
  'rounded-(2xl|3xl|full)|shadow-(lg|xl|2xl)|border-radius: *(1[6-9]|[2-9][0-9])px|borderRadius: *(1[6-9]|[2-9][0-9])|RoundedCornerShape\((1[6-9]|[2-9][0-9])|CircleShape|cornerRadius[:(] *(1[6-9]|[2-9][0-9])|BorderRadius\.circular\((1[6-9]|[2-9][0-9])'
c "10 oscuro/terminal" \
  'max-w-(2xl|3xl)|#ff5f56|#ffbd2e|#27c93f|bg-red-500.*bg-yellow-500|darkColorScheme|isSystemInDarkTheme|preferredColorScheme\(\.dark|ThemeMode\.dark|colorScheme: *.dark'

echo
echo "Son indicios, no veredictos: un conteo alto puede ser intencional y uno en 0 no descarta el patrón."
echo "Confirma leyendo los archivos que salen arriba."
