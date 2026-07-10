import { http, HttpResponse } from "msw";
import type { ApiError, Availability, WeekDay } from "@/api/types";
import { apiUri } from "@/test/msw/bookingFixtures";

export const DRIVER_VEHICLES_ENDPOINT = "*/api/drivers/:driverId/vehicles";
export const AVAILABILITY_ENDPOINT = "*/api/drivers/:driverId/vehicles/:vehicleId/availability";
export const BOOKED_TIMES_ENDPOINT = "*/api/drivers/:driverId/vehicles/:vehicleId/booked-times";
export const CREATE_BOOKING_ENDPOINT = "*/api/bookings";

const ALL_DAYS: WeekDay[] = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"];

/**
 * A vehicle shaped like what `getDriverVehicles` returns (BookingCreate
 * treats these loosely as `any[]`, unlike the `Vehicle` type used
 * elsewhere).
 */
export function makeBookableVehicle(overrides: { id?: number; driverId?: number; [key: string]: unknown } = {}) {
  const driverId = overrides.driverId ?? 1;
  const id = overrides.id ?? 1;
  return {
    id,
    plateNumber: "AB123CD",
    volumeM3: 12,
    hourlyRate: 1500,
    description: "Cargo van",
    self: apiUri(`drivers/${driverId}/vehicles/${id}`),
    picture: "",
    driver: apiUri(`drivers/${driverId}`),
    availability: apiUri(`drivers/${driverId}/vehicles/${id}/availability`),
    bookedTimes: apiUri(`drivers/${driverId}/vehicles/${id}/booked-times`),
    ...overrides,
  };
}

/**
 * By default, available every day of the week for the morning shift. This
 * sidesteps day-of-week-dependent flakiness: whatever day "today" happens to
 * be when the test runs, it (and the rest of the current month) shows up as
 * available, so the calendar never needs to auto-advance to a later month.
 */
export function makeAvailability(overrides: Partial<Availability> = {}): Availability {
  return {
    zones: [apiUri("zones/1"), apiUri("zones/2")],
    timeSlots: ALL_DAYS.map((day) => ({ day, shift: "MORNING" as const })),
    ...overrides,
  };
}

export function driverVehiclesResponse(vehicles: unknown[]) {
  return http.get(DRIVER_VEHICLES_ENDPOINT, () => HttpResponse.json(vehicles));
}

export function availabilityResponse(availability: Availability) {
  return http.get(AVAILABILITY_ENDPOINT, () => HttpResponse.json(availability));
}

export function bookedTimesResponse(bookedTimes: { date: string; shift: string }[] = []) {
  return http.get(BOOKED_TIMES_ENDPOINT, () => HttpResponse.json(bookedTimes));
}

export function createBookingSuccessResponse(booking: Record<string, unknown> = { id: 1 }) {
  return http.post(CREATE_BOOKING_ENDPOINT, () => HttpResponse.json(booking, { status: 201 }));
}

export function createBookingFieldErrorsResponse(errors: ApiError[], status = 400) {
  return http.post(CREATE_BOOKING_ENDPOINT, () => HttpResponse.json(errors, { status }));
}

export function createBookingConflictResponse() {
  return http.post(CREATE_BOOKING_ENDPOINT, () => new HttpResponse(null, { status: 409 }));
}
