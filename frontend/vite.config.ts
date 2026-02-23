import { defineConfig } from "vite";
import solidPlugin from "vite-plugin-solid";
import devtools from "solid-devtools/vite";
import tailwindcss from "@tailwindcss/vite";
import tsconfigPaths from "vite-tsconfig-paths";

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
  base: '/paw-2024b-01',
  build: {
    target: "esnext",
  },
});
