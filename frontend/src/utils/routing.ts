// The router base (e.g. "/paw-2024b-01"), without a trailing slash.
export const ROUTER_BASE = import.meta.env.BASE_URL.replace(/\/$/, "");

// `useLocation().pathname` from @solidjs/router INCLUDES the router base. Navigation
// targets passed to `navigate()` / `<A href>` / `<Navigate href>` must NOT include it,
// since the router prepends the base itself — otherwise the base ends up duplicated
// (e.g. "/paw-2024b-01/paw-2024b-01/..."). Strip the base before feeding a pathname
// read from the location back into any navigation.
export function stripBase(pathname: string): string {
  if (ROUTER_BASE && pathname.startsWith(ROUTER_BASE)) {
    return pathname.slice(ROUTER_BASE.length) || "/";
  }
  return pathname;
}
