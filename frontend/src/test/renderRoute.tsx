import { render } from "@solidjs/testing-library";
import { createMemoryHistory, MemoryRouter, Route } from "@solidjs/router";
import { MetaProvider } from "@solidjs/meta";
import type { Component } from "solid-js";
import { AppContextProvider } from "@/app-context/AppContext";

/**
 * Renders `component` at `route` (e.g. "/search"), with the browser location
 * initialized to `initialUrl` (e.g. "/search?zoneId=5&page=2"). Wraps it the
 * same way src/index.tsx does: a Router whose `root` renders
 * AppContextProvider, so useApi()/useTranslator()/useSearchParams() all work
 * exactly as they do in the real app.
 *
 * `extraRoutes` registers additional stub routes alongside `route`. This is
 * for pages that navigate() elsewhere on success (e.g. Login -> "/client"):
 * give the destination a small marker component and assert on that marker
 * to confirm the navigation happened, instead of asserting on translated
 * copy from the real destination page.
 *
 * The memory history is returned so a test can also check
 * `history.get().value` directly if that's more convenient.
 */
/**
 * Renders `component` at `route` (e.g. "/search"), with the browser location
 * initialized to `initialUrl` (e.g. "/search?zoneId=5&page=2"). Wraps it the
 * same way src/index.tsx does: a Router whose `root` renders
 * AppContextProvider, so useApi()/useTranslator()/useSearchParams() all work
 * exactly as they do in the real app.
 *
 * `extraRoutes` registers additional stub routes alongside `route`. This is
 * for pages that navigate() elsewhere on success (e.g. Login -> "/client"):
 * give the destination a small marker component and assert on that marker
 * to confirm the navigation happened, instead of asserting on translated
 * copy from the real destination page.
 *
 * Note: there's no way to seed `location().state` for the *initial* route
 * this way — MemoryRouter's history only tracks plain path strings, and
 * discards any state given before the router exists. If a test needs
 * `location.state` (e.g. Login's redirect-back-to `from`), drive it through
 * a real in-app navigation instead, the same way ProtectedRoute does it:
 * render an extra route whose component is `<Navigate href="..." state={{...}} />`
 * and start `initialUrl` there.
 */
export function renderRoute(
  route: string,
  initialUrl: string,
  component: Component,
  extraRoutes: { path: string; component: Component }[] = [],
) {
  const history = createMemoryHistory();
  history.set({ value: initialUrl });

  const utils = render(() => (
    <MetaProvider>
      <MemoryRouter history={history} root={(props) => <AppContextProvider>{props.children}</AppContextProvider>}>
        <Route path={route} component={component} />
        {extraRoutes.map((r) => (
          <Route path={r.path} component={r.component} />
        ))}
      </MemoryRouter>
    </MetaProvider>
  ));

  return { ...utils, history };
}
