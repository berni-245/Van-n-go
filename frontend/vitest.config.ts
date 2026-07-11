import { defineConfig, mergeConfig } from "vitest/config";
import viteConfig from "./vite.config";

export default mergeConfig(
  viteConfig,
  defineConfig({
    test: {
      environment: "jsdom",
      globals: true,
      setupFiles: ["./src/test/setup.ts"],
      css: true,
      exclude: ["**/node_modules/**", "**/dist/**", "**/target/**"],
    },
    // vite-plugin-solid needs to resolve solid-js's browser/dev build
    // (not its server build) for both the normal and SSR resolution
    // pipelines, or reactivity breaks under jsdom.
    resolve: {
      conditions: ["development", "browser"],
    },
    ssr: {
      resolve: {
        conditions: ["development", "browser"],
      },
    },
  })
);
