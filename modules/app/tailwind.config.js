/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./main.js",
    "./src/main/scala/co/beautybard/**/*.scala"
  ],
  theme: {
    extend: {},
  },
  plugins: [
    require('daisyui'),
  ],
  daisyui: {
    themes: [
      {
        light: {
          ...require('daisyui/src/theming/themes').valentine
        }
      },
      {
        dark: {
          ...require('daisyui/src/theming/themes').dim,
          ...Object.fromEntries(
            Object.entries(require('daisyui/src/theming/themes').valentine)
              .filter(([k]) => k.startsWith('--'))
          )
        }
      },
    ]
  }
}

