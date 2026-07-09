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
 */
export function renderRoute(route: string, initialUrl: string, component: Component) {
  const history = createMemoryHistory();
  history.set({ value: initialUrl });

  return render(() => (
    <MetaProvider>
      <MemoryRouter history={history} root={(props) => <AppContextProvider>{props.children}</AppContextProvider>}>
        <Route path={route} component={component} />
      </MemoryRouter>
    </MetaProvider>
  ));
}
