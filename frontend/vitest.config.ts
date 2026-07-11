import {defineConfig, mergeConfig} from "vitest/config";
import type {PluginOption} from "vite";
import solid from "vite-plugin-solid";
import viteConfig from "./vite.config";

// vite-plugin-solid injects an import of the virtual "/@solid-refresh" HMR
// runtime whenever command==='serve' && mode!=='production' (both true under
// Vitest). Vitest 4's module runner can't resolve that virtual id — it feeds
// "file:///@solid-refresh" to Node's createRequire, which throws and aborts
// test collection. HMR is pointless in a headless test run, so replace the
// dev/prod Solid plugin (name "solid") with a hot-disabled one here; that
// flips needHmr to false and nothing gets injected. vite.config.ts (dev/prod)
// stays untouched.
const testPlugins = [
  ...(viteConfig.plugins?.flat(Infinity) as PluginOption[]).filter(
    (p) => !(p && typeof p === "object" && "name" in p && p.name === "solid")
  ),
  solid({ hot: false }),
];

export default mergeConfig(
  { ...viteConfig, plugins: testPlugins },
  defineConfig({
    test: {
      environment: "jsdom",
      globals: true,
      setupFiles: ["./src/test/setup.ts"],
      css: true,
      // The heavier integration tests (e.g. BookingCreate: calendar polling +
      // two comboboxes + typing a full jobDescription via userEvent + an action
      // submit + navigation) run close to the 5s default. Under the parallel
      // load of the full suite that's enough to flake into a timeout even
      // though nothing is actually stuck, so give every test more headroom.
      testTimeout: 15000,
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
