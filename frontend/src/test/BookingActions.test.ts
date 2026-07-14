import { expect, test } from "vitest";
import { canFinishBooking } from "@/components/bookings/BookingActions";

test("canFinishBooking is true when the booking date is today", () => {
  const now = new Date("2026-07-14T15:00:00");
  expect(canFinishBooking("2026-07-14", now)).toBe(true);
});

test("canFinishBooking is true when the booking date is in the past", () => {
  const now = new Date("2026-07-14T15:00:00");
  expect(canFinishBooking("2026-07-01", now)).toBe(true);
});

test("canFinishBooking is false when the booking date is in the future", () => {
  const now = new Date("2026-07-14T15:00:00");
  expect(canFinishBooking("2026-07-15", now)).toBe(false);
});

test("canFinishBooking compares by calendar date, not time of day", () => {
  // Booking is "today" even one minute before midnight.
  const now = new Date("2026-07-14T23:59:00");
  expect(canFinishBooking("2026-07-14", now)).toBe(true);
});

test("canFinishBooking handles a year boundary correctly", () => {
  const now = new Date("2026-01-01T00:00:00");
  expect(canFinishBooking("2025-12-31", now)).toBe(true);
  expect(canFinishBooking("2026-01-02", now)).toBe(false);
});
