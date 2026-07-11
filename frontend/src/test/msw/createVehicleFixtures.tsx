import {http, HttpResponse} from "msw";
import type {ApiError} from "@/api/types";

// CreateVehicle.tsx submits via api.createVehicle(driverId, payload), which
// issues `POST {API_ROOT}drivers/{id}/vehicles`. API_ROOT resolves to
// `${origin}${BASE_URL}api/`, so the path always ends in
// `/api/drivers/:driverId/vehicles`. (Same path as the vehicle *list*, but
// that's a GET — MSW matches on method too, so there's no collision.)
export const CREATE_VEHICLE_ENDPOINT = "*/api/drivers/:driverId/vehicles";

/** The vehicle is created; the API echoes it back with a 201. */
export function createVehicleSuccess(vehicle: Record<string, unknown> = { id: 1 }) {
  return http.post(CREATE_VEHICLE_ENDPOINT, () => HttpResponse.json(vehicle, { status: 201 }));
}

/**
 * The API rejects the payload with field-level validation errors (400). The
 * component groups these by `entity` and renders them next to the matching
 * field.
 */
export function createVehicleFieldErrors(errors: ApiError[], status = 400) {
  return http.post(CREATE_VEHICLE_ENDPOINT, () => HttpResponse.json(errors, { status }));
}

/**
 * The driver has hit their vehicle cap (409). The component surfaces this as a
 * toast and stays on the form rather than showing a field error.
 */
export function createVehicleLimitReached() {
  return http.post(CREATE_VEHICLE_ENDPOINT, () => new HttpResponse(null, { status: 409 }));
}

/**
 * Like createVehicleSuccess(), but records the `driverId` (from the URL path)
 * and the JSON body of every request, so a test can assert on exactly what the
 * component sent (e.g. that numeric fields are parsed to numbers).
 */
export function recordCreateVehicle(vehicle: Record<string, unknown> = { id: 1 }) {
  const calls: { driverId: string; body: Record<string, unknown> }[] = [];
  const handler = http.post(CREATE_VEHICLE_ENDPOINT, async ({ request, params }) => {
    calls.push({ driverId: String(params.driverId), body: (await request.json()) as Record<string, unknown> });
    return HttpResponse.json(vehicle, { status: 201 });
  });
  return { handler, calls };
}
