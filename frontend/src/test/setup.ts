import "@testing-library/jest-dom/vitest";
import { afterAll, afterEach, beforeAll } from "vitest";
import { setGlobalOrigin } from "undici";
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

