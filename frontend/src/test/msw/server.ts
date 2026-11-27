import { setupServer } from "msw/node";
import { http, HttpResponse } from "msw";

// SearchCard renders driver avatars through ImageWithFallback, which always
// fetches an image blob (even the default placeholder avatar when a driver
// has none). That's incidental to what these tests care about, so stub it
// out globally with a tiny real PNG rather than re-mocking it in every test
// that renders a list of drivers.
const PLACEHOLDER_PNG = Buffer.from(
  "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=",
  "base64"
);

// No handlers for the app's own API endpoints: each test registers exactly
// the responses it needs via `server.use(...)`. This keeps a test's mocked
// API behavior visible in the test itself, instead of hidden in a shared
// fixture file.
export const server = setupServer(
  http.get("*/images/*", () => new HttpResponse(PLACEHOLDER_PNG, { headers: { "Content-Type": "image/png" } }))
);
