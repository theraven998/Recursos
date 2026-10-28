# Tokens de ejemplo

Punto de partida para centralizar. Adáptalo al stack (CSS, `@theme` de Tailwind v4, `tailwind.config`, tema de React Native/Flutter). Los valores son ilustrativos: usa el color y las fuentes de la marca.

```css
:root {
  /* Color: 1 neutro, 1 marca, 1 acento, superficies y texto */
  --color-bg: #fafaf7;
  --color-surface: #ffffff;
  --color-line: #e4e2dc;
  --color-fg: #1a1a17;
  --color-muted: #6b6a63;
  --color-brand: #1f5f4a;
  --color-accent: #d9622b;

  /* Tipografía: un par con carácter + escala */
  --font-display: "Fraunces", Georgia, serif;
  --font-body: "Source Sans 3", system-ui, sans-serif;
  --text-sm: 0.875rem;  --text-base: 1rem;   --text-lg: 1.25rem;
  --text-xl: 1.75rem;   --text-2xl: 2.5rem;  --text-3xl: 3.5rem;

  /* Radios por tipo de elemento */
  --radius-control: 6px;  /* botones, inputs */
  --radius-card: 10px;    /* cards (si de verdad hacen falta) */
  --radius-full: 9999px;  /* avatares, chips */

  /* Espaciado (ritmo 4/8) y elevación (máx. 3 niveles, neutros) */
  --space-1: 4px; --space-2: 8px; --space-3: 16px; --space-4: 24px; --space-5: 48px; --space-6: 96px;
  --shadow-1: 0 1px 2px rgb(0 0 0 / 0.06);
  --shadow-2: 0 4px 12px rgb(0 0 0 / 0.08);
}

@media (prefers-color-scheme: dark) {
  :root {
    --color-bg: #121411; --color-surface: #1a1d19; --color-line: #2b2f29;
    --color-fg: #ecebe4; --color-muted: #9a9a90;
  }
}
```

Reglas: ningún componente declara un hex, un radio o una sombra propios; todos leen de aquí. El modo oscuro solo redefine los tokens.
