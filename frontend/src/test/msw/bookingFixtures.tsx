import { http, HttpResponse } from "msw";
import type { Booking, Vehicle } from "@/api/types";
import { BookingState, ShiftPeriod } from "@/api/types";

const ORIGIN = "http://localhost:3000";
/** Builds a fully-qualified HATEOAS-style URI the way the real API would. */
export function apiUri(path: string) {
  return `${ORIGIN}/api/${path}`;
}

export const BOOKINGS_ENDPOINT = "*/api/bookings";
export const VEHICLE_ENDPOINT = "*/api/drivers/:driverId/vehicles/:vehicleId";
export const ZONE_BY_ID_ENDPOINT = "*/api/zones/:id";

export function makeBooking(overrides: Partial<Booking> = {}): Booking {
  return {
    id: 1,
    self: apiUri("bookings/1"),
    client: apiUri("clients/1"),
    driver: apiUri("drivers/1"),
    vehicle: apiUri("drivers/1/vehicles/1"),
    originZone: apiUri("zones/1"),
    destinationZone: apiUri("zones/2"),
    proofOfPayment: apiUri("bookings/1/proof-of-payment"),
    state: BookingState.Pending,
    date: "2026-08-01T09:00:00Z",
    jobDescription: "Moving a few boxes of books.",
    shiftPeriod: ShiftPeriod.Morning,
    review: apiUri("bookings/1/review"),
    ...overrides,
  };
}

export function makeVehicle(overrides: Partial<Vehicle> = {}): Vehicle {
  return {
    id: 1,
    plateNumber: "AB123CD",
    volumeM3: 12,
    hourlyRate: 1500,
    description: "Cargo van",
    self: apiUri("drivers/1/vehicles/1"),
    picture: "",
    driver: apiUri("drivers/1"),
    availability: apiUri("drivers/1/vehicles/1/availability"),
    bookedTimes: apiUri("drivers/1/vehicles/1/booked-times"),
    ...overrides,
  };
}

/**
 * Responds with a fixed list of bookings and a Link header reporting the
 * given page bounds. `parsePageInfo` (pagination.ts) requires both `first`
 * and `last` to be present, or it discards the whole header.
 */
export function bookingsResponse(bookings: Booking[], pageInfo: { first?: number; last?: number } = {}) {
  const first = pageInfo.first ?? 1;
  const last = pageInfo.last ?? 1;
  return http.get(BOOKINGS_ENDPOINT, () =>
    HttpResponse.json(bookings, {
      headers: {
        Link: `<${apiUri(`bookings?page=${first}`)}>; rel="first", <${apiUri(`bookings?page=${last}`)}>; rel="last"`,
      },
    })
  );
}

/** api.getBookings() treats any non-ok response as an empty page rather than throwing. */
export function bookingsErrorResponse(status = 500) {
  return http.get(BOOKINGS_ENDPOINT, () => new HttpResponse(null, { status }));
}

export function vehicleResponse(vehicle: Vehicle) {
  return http.get(VEHICLE_ENDPOINT, () => HttpResponse.json(vehicle));
}

/** Looks up each zone by the numeric `:id` in the URL, e.g. { 1: zoneA, 2: zoneB }. */
export function zonesByIdResponse(zonesById: Record<number, unknown>) {
  return http.get(ZONE_BY_ID_ENDPOINT, ({ params }) => {
    const zone = zonesById[Number(params.id)];
    return zone ? HttpResponse.json(zone) : new HttpResponse(null, { status: 404 });
  });
}
