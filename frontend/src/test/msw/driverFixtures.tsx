import { http, HttpResponse } from "msw";
import type { Driver } from "@/api/types";

// Matches any origin, so it works regardless of how jsdom/happy-dom
// resolves the app's relative "/paw-2024b-01/api/drivers" request.
export const DRIVERS_ENDPOINT = "*/api/drivers";

export function makeDriver(overrides: Partial<Driver> = {}): Driver {
  return {
    id: 1,
    username: "juan_perez",
    email: "juan@example.com",
    preferredLanguage: "es",
    profilePic: "",
    hasProfilePic: false,
    self: "/api/drivers/1",
    pendingBookings: "",
    acceptedBookings: "",
    finishedBookings: "",
    rejectedBookings: "",
    canceledBookings: "",
    description: "Reliable driver, own truck.",
    rating: 4,
    vehicles: "/api/drivers/1/vehicles",
    ...overrides,
  };
}

function linkHeader(lastPage: number) {
  return `<${DRIVERS_ENDPOINT}?page=1>; rel="first", <${DRIVERS_ENDPOINT}?page=${lastPage}>; rel="last"`;
}

/** Responds with a fixed list of drivers, optionally reporting more pages via the Link header. */
export function driversResponse(drivers: Driver[], opts: { lastPage?: number } = {}) {
  return http.get(DRIVERS_ENDPOINT, () => {
    const headers: Record<string, string> = {};
    if (opts.lastPage) headers["Link"] = linkHeader(opts.lastPage);
    return HttpResponse.json(drivers, { headers });
  });
}

/** Simulates a server error; Search should fall back to its empty state. */
export function driversErrorResponse(status = 500) {
  return http.get(DRIVERS_ENDPOINT, () => new HttpResponse(null, { status }));
}
