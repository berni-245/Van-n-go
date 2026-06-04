import { defineConfig } from "vite";
import solidPlugin from "vite-plugin-solid";
import devtools from "solid-devtools/vite";
import tailwindcss from "@tailwindcss/vite";
import tsconfigPaths from "vite-tsconfig-paths";

const BASE = "/paw-2024b-01/";

export default defineConfig({
  plugins: [
    devtools({
      autoname: true,
      locator: {
        jsxLocation: true,
        componentLocation: true,
      },
    }),
    tsconfigPaths(),
    solidPlugin(),
    tailwindcss(),
  ],
  base: BASE,
  build: {
    target: "esnext",
  },
  server: {
    proxy: {
      [`${BASE}api`]: {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});
