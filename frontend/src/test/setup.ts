import "@testing-library/jest-dom/vitest";
import { afterAll, afterEach, beforeAll } from "vitest";
import { setGlobalOrigin } from "undici";
import { query } from "@solidjs/router";
import { server } from "./msw/server";

// The app makes relative-URL requests (e.g. new Request("/api/drivers?...")).
// Real browsers resolve those against the current page; Node's native
// fetch/Request (which vitest's jsdom/happy-dom environments sit on top of)
// can't, and throws "Failed to parse URL" without this. Match the test
// environment's default window.location so relative requests resolve the
// same way they would in the browser. See: https://github.com/mswjs/msw/issues/1625
setGlobalOrigin("http://localhost:3000");

beforeAll(() => server.listen({ onUnhandledRequest: "error" }));
afterEach(() => {
  server.resetHandlers();
  // AppContextProvider persists session/user/preferences to localStorage
  // (see appState.ts's saveState effect) and initialState() reads it back
  // on the next mount. Without this, a test that logs a user in leaks that
  // session into whichever test runs next in the same file.
  localStorage.clear();
  // query() (used by Bookings/BookingCreate/Vehicles/Profile/VehicleEdit for
  // data loading) caches responses in a module-level map keyed by arguments,
  // independent of any component's lifecycle. Without clearing it, a test
  // that populates the cache for a given set of arguments (e.g. the default
  // "PENDING page 1" bookings query) can serve stale data to a later test
  // that uses the same arguments but mocks a different response.
  query.clear();
});
afterAll(() => server.close());

// jsdom/happy-dom doesn't implement matchMedia; appState.ts reads it to
// pick an initial light/dark theme, so stub it out to always report "no
// preference".
if (!window.matchMedia) {
  window.matchMedia = (query: string) =>
    ({
      matches: false,
      media: query,
      onchange: null,
      addListener: () => {},
      removeListener: () => {},
      addEventListener: () => {},
      removeEventListener: () => {},
      dispatchEvent: () => false,
    }) as MediaQueryList;
}

// jsdom doesn't implement scrollIntoView at all (unlike scrollTo, which it
// stubs with just a "not implemented" warning) — it's simply undefined, so
// any component that calls it (e.g. Chat.tsx scrolling to the latest
// message) throws a real TypeError in tests.
if (!Element.prototype.scrollIntoView) {
  Element.prototype.scrollIntoView = () => {};
}

// jsdom's own URL.createObjectURL/revokeObjectURL exist but the blob: URLs
// they produce aren't resolvable by jsdom's <img> resource loading —
// observed silently falling back to fetching the document root instead.
// ImageWithFallback.tsx (used by Profile, and likely other pages with
// pictures) calls these on every render, so override them unconditionally
// with a self-contained data URI, which never triggers a network fetch.
const ONE_PIXEL_GIF = "data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==";
URL.createObjectURL = () => ONE_PIXEL_GIF;
URL.revokeObjectURL = () => {};
