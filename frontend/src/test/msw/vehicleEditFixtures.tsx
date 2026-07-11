import {http, HttpResponse} from "msw";
import type {ApiError, Availability, Vehicle} from "@/api/types";
import {apiUri} from "@/test/msw/bookingFixtures";

// VehicleEdit loads the vehicle via api.getVehicle(userId, vehicleId) and
// updates it via api.updateVehicle(userId, vehicleId, ...). Both hit
// `{API_ROOT}drivers/{id}/vehicles/{vehicleId}`, differing only by method (GET
// vs PATCH), so the same path pattern serves both. The availability lives one
// segment deeper and never collides with the vehicle path.
export const VEHICLE_ENDPOINT = "*/api/drivers/:driverId/vehicles/:vehicleId";
export const VEHICLE_AVAILABILITY_ENDPOINT = "*/api/drivers/:driverId/vehicles/:vehicleId/availability";

/**
 * A vehicle shaped for the edit page. `picture` points at an /images/ URL so
 * it's served by the global stub in server.ts (the page eagerly fetches the
 * current picture as a preview blob); `availability` resolves to
 * VEHICLE_AVAILABILITY_ENDPOINT.
 */
export function makeEditableVehicle(overrides: Partial<Vehicle> = {}): Vehicle {
  return {
    id: 1,
    plateNumber: "AB123CD",
    volumeM3: 12,
    hourlyRate: 1500,
    description: "Cargo van",
    self: apiUri("drivers/1/vehicles/1"),
    picture: "http://localhost:3000/images/vehicle-1.png",
    driver: apiUri("drivers/1"),
    availability: apiUri("drivers/1/vehicles/1/availability"),
    bookedTimes: apiUri("drivers/1/vehicles/1/booked-times"),
    ...overrides,
  };
}

export function makeVehicleAvailability(overrides: Partial<Availability> = {}): Availability {
  return {
    zones: [apiUri("zones/1")],
    timeSlots: [{ day: "MONDAY", shift: "MORNING" }],
    ...overrides,
  };
}

/** GET the vehicle being edited (used to pre-fill the form). */
export function getVehicleResponse(vehicle: Vehicle) {
  return http.get(VEHICLE_ENDPOINT, () => HttpResponse.json(vehicle));
}

/** GET the vehicle's availability. The page reads it via res.text()+JSON.parse. */
export function vehicleAvailabilityResponse(availability: Availability) {
  return http.get(VEHICLE_AVAILABILITY_ENDPOINT, () => HttpResponse.json(availability));
}

/** PATCH update succeeds; the component shows a success toast (it does not navigate). */
export function updateVehicleSuccess(vehicle: Record<string, unknown> = {}) {
  return http.patch(VEHICLE_ENDPOINT, () => HttpResponse.json(vehicle, { status: 200 }));
}

/** PATCH rejected with field-level validation errors (400), grouped by `entity`. */
export function updateVehicleFieldErrors(errors: ApiError[], status = 400) {
  return http.patch(VEHICLE_ENDPOINT, () => HttpResponse.json(errors, { status }));
}

/** PATCH rejected with an access error (403/404); surfaced as a toast. */
export function updateVehicleAccessError(status = 403) {
  return http.patch(VEHICLE_ENDPOINT, () => new HttpResponse(null, { status }));
}

/**
 * Like updateVehicleSuccess(), but records the `driverId`/`vehicleId` from the
 * path and the JSON body of every PATCH, so a test can assert exactly what the
 * component sent.
 */
export function recordUpdateVehicle(vehicle: Record<string, unknown> = {}) {
  const calls: { driverId: string; vehicleId: string; body: Record<string, unknown> }[] = [];
  const handler = http.patch(VEHICLE_ENDPOINT, async ({ request, params }) => {
    calls.push({
      driverId: String(params.driverId),
      vehicleId: String(params.vehicleId),
      body: (await request.json()) as Record<string, unknown>,
    });
    return HttpResponse.json(vehicle, { status: 200 });
  });
  return { handler, calls };
}
