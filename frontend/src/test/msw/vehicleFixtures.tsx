import {http, HttpResponse} from "msw";
import type {Vehicle} from "@/api/types";
import {apiUri, makeVehicle} from "@/test/msw/bookingFixtures";

// Re-exported so vehicle tests can build fixtures from a single import without
// reaching into bookingFixtures (where makeVehicle happens to already live).
export {makeVehicle};

// Vehicles.tsx loads the logged-in driver's vehicles via
// api.getVehicles(userId, page), which issues
// `GET {API_ROOT}drivers/{id}/vehicles?page=N`. API_ROOT resolves to
// `${origin}${BASE_URL}api/`, so the path always ends in
// `/api/drivers/:driverId/vehicles` regardless of the configured base path.
export const VEHICLES_ENDPOINT = "*/api/drivers/:driverId/vehicles";

function pageLinkHeader(first: number, last: number) {
  return {
    Link:
      `<${apiUri(`drivers/1/vehicles?page=${first}`)}>; rel="first", ` +
      `<${apiUri(`drivers/1/vehicles?page=${last}`)}>; rel="last"`,
  };
}

/**
 * Responds with a fixed list of vehicles and a Link header reporting the given
 * page bounds. `parsePageInfo` (pagination.ts) requires both `first` and
 * `last` to be present, or it discards the whole header — which leaves the list
 * as a single, final page so InfiniteScroll stops without requesting more.
 */
export function vehiclesResponse(vehicles: Vehicle[], pageInfo: { first?: number; last?: number } = {}) {
  const first = pageInfo.first ?? 1;
  const last = pageInfo.last ?? 1;
  return http.get(VEHICLES_ENDPOINT, () => HttpResponse.json(vehicles, { headers: pageLinkHeader(first, last) }));
}

/**
 * Like vehiclesResponse(), but records the `driverId` taken from the URL path
 * and the requested `page` for every call, so a test can assert the component
 * fetches the *logged-in* driver's own vehicles.
 */
export function recordVehiclesRequest(vehicles: Vehicle[] = []) {
  const calls: { driverId: string; page: string | null }[] = [];
  const handler = http.get(VEHICLES_ENDPOINT, ({ request, params }) => {
    const url = new URL(request.url);
    calls.push({ driverId: String(params.driverId), page: url.searchParams.get("page") });
    return HttpResponse.json(vehicles, { headers: pageLinkHeader(1, 1) });
  });
  return { handler, calls };
}
