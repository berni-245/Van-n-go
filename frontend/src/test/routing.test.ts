import { afterEach, expect, test, vi } from "vitest";

// ROUTER_BASE is computed once, at module-evaluation time, from
// import.meta.env.BASE_URL. To exercise both "app served from a sub-path"
// and "app served from the root" we mutate BASE_URL *before* a fresh import
// of the module: import.meta.env is a plain mutable object at runtime, and
// vi.resetModules() clears vitest's module cache so the next import()
// re-evaluates routing.ts against whatever BASE_URL is set at that moment.
const ORIGINAL_BASE_URL = import.meta.env.BASE_URL;

afterEach(() => {
  import.meta.env.BASE_URL = ORIGINAL_BASE_URL;
  vi.resetModules();
});

async function loadRoutingWithBase(baseUrl: string) {
  vi.resetModules();
  import.meta.env.BASE_URL = baseUrl;
  return import("@/utils/routing");
}

test("ROUTER_BASE strips the trailing slash off BASE_URL", async () => {
  const { ROUTER_BASE } = await loadRoutingWithBase("/paw-2024b-01/");
  expect(ROUTER_BASE).toBe("/paw-2024b-01");
});

test("stripBase removes the base and keeps the rest of the path", async () => {
  const { stripBase } = await loadRoutingWithBase("/paw-2024b-01/");
  expect(stripBase("/paw-2024b-01/search")).toBe("/search");
});

test("stripBase collapses a bare base (no remaining path) to '/'", async () => {
  const { stripBase } = await loadRoutingWithBase("/paw-2024b-01/");
  expect(stripBase("/paw-2024b-01")).toBe("/");
});

test("stripBase leaves paths that don't start with the base untouched", async () => {
  const { stripBase } = await loadRoutingWithBase("/paw-2024b-01/");
  expect(stripBase("/other/path")).toBe("/other/path");
});

test("stripBase is a no-op when the app is served from the root (no base configured)", async () => {
  const { stripBase } = await loadRoutingWithBase("/");
  expect(stripBase("/search")).toBe("/search");
});

test("stripBase matches on a raw string prefix, not a path-segment boundary (documents current behavior)", async () => {
  // This isn't necessarily desirable -- it's here so that if stripBase is
  // ever tightened to require a "/" (or end-of-string) right after the
  // base, this test fails loudly and its expectation can be flipped
  // alongside the fix, instead of the regression going unnoticed.
  const { stripBase } = await loadRoutingWithBase("/paw-2024b-01/");
  expect(stripBase("/paw-2024b-01x/foo")).toBe("x/foo");
});
