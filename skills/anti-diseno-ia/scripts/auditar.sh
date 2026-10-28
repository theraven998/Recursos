#!/usr/bin/env bash
# Cuenta coincidencias de señales de los 10 patrones. Solo lee; no modifica nada.
# Uso: bash auditar.sh [carpeta]   (por defecto, la carpeta actual)
set -u
dir="${1:-.}"
inc=(--include='*.css' --include='*.scss' --include='*.html' --include='*.js' --include='*.jsx' --include='*.ts' --include='*.tsx' --include='*.vue' --include='*.svelte' --include='*.astro' --include='*.dart')
exc=(--exclude-dir=node_modules --exclude-dir=.next --exclude-dir=dist --exclude-dir=build --exclude-dir=out --exclude-dir=.git)
c() { grep -rEi "${inc[@]}" "${exc[@]}" -- "$2" "$dir" 2>/dev/null | wc -l | tr -d ' ' | xargs printf '%-4s %s\n' "$(printf '%s' "$1")"; }
echo "Señales encontradas (líneas) por patrón en: $dir"
c "1 morado/degradado:"  '(purple|violet|indigo|fuchsia|#6366f1|#8b5cf6|#a855f7).*|bg-clip-text|from-(purple|violet|indigo)'
c "2 Inter/Roboto:"      "font-family:[^;]*(Inter|Roboto)|font-(Inter|Roboto)|Inter\(|Roboto\("
c "3 hero/cards:"        'Unlock|Build the future|todo lo que necesitas|grid-cols-3'
c "4 card en card:"      'rounded-(xl|2xl|3xl)[^"]*border[^"]*shadow'
c "5 glass/blur:"        'backdrop-(blur|filter)'
c "6 glow/neón:"         'shadow-(purple|violet|cyan|blue|pink)-|drop-shadow|text-shadow|box-shadow:[^;]*(rgba|#)[^;]*[0-9]{2}px'
c "7 emoji como ícono:"  '🚀|✨|🧠|⚡|🔥|🎯|💡'
c "8 métricas falsas:"   'Total Users|Active Sessions|Revenue|12,345|\+12%'
c "9 radios/sombras:"    'rounded-(2xl|3xl)|shadow-(lg|xl|2xl)|border-radius: *(2[4-9]|[3-9][0-9])px'
c "10 columna/terminal:" 'max-w-(2xl|3xl)|#ff5f56|#ffbd2e|#27c93f|bg-red-500.*bg-yellow-500'
echo "Son indicios, no veredictos: confirma leyendo el código."
