import { http, HttpResponse } from "msw";
import type { ZoneDTO } from "@/components/inputs/ZoneInput";

// Matches any origin, so it works regardless of how jsdom/happy-dom
// resolves the app's relative "/api/zones" request.
export const ZONES_ENDPOINT = "*/api/zones";

export function makeZone(overrides: Partial<ZoneDTO> = {}): ZoneDTO {
  return {
    id: 1,
    country: { name: "Argentina", code: "AR" },
    province: { name: "Buenos Aires" },
    neighborhood: { name: "Palermo" },
    ...overrides,
  };
}

/** Responds with a fixed list of zones. */
export function zonesResponse(zones: ZoneDTO[]) {
  return http.get(ZONES_ENDPOINT, () => HttpResponse.json(zones));
}

/**
 * Simulates a server error. ZoneInput's `getZones` call doesn't check
 * `res.ok`, so this only exercises the catch branch in
 * ZoneInput.fetchZones() when the error body isn't valid JSON (res.json()
 * rejects). That's the realistic case for a 500 with no body.
 */
export function zonesErrorResponse(status = 500) {
  return http.get(ZONES_ENDPOINT, () => new HttpResponse(null, { status }));
}
