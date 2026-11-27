import { expect, test } from "vitest";
import { getDaysInMonth, getFirstDayOfMonth, sortShifts } from "@/pages/BookingCreate";

// -- sortShifts -----------------------------------------------------------
// Regression coverage for the bug fixed in dee3504d: availability timeSlots
// arrive from the API in arbitrary order and must be sorted into the
// canonical MORNING/AFTERNOON/EVENING order before display.

test("sortShifts puts shifts into canonical MORNING/AFTERNOON/EVENING order", () => {
  expect(sortShifts(["EVENING", "MORNING", "AFTERNOON"])).toEqual(["MORNING", "AFTERNOON", "EVENING"]);
});

test("sortShifts is a no-op when already in canonical order", () => {
  expect(sortShifts(["MORNING", "AFTERNOON", "EVENING"])).toEqual(["MORNING", "AFTERNOON", "EVENING"]);
});

test("sortShifts handles a single shift", () => {
  expect(sortShifts(["AFTERNOON"])).toEqual(["AFTERNOON"]);
});

test("sortShifts handles a subset of shifts, preserving canonical relative order", () => {
  expect(sortShifts(["EVENING", "MORNING"])).toEqual(["MORNING", "EVENING"]);
});

test("sortShifts does not mutate the input array", () => {
  const input = ["EVENING", "MORNING"];
  sortShifts(input);
  expect(input).toEqual(["EVENING", "MORNING"]);
});

test("sortShifts sorts unrecognized shift values before all known ones (documents current behavior)", () => {
  // Unknown values yield indexOf === -1, which sorts before MORNING (index 0).
  expect(sortShifts(["EVENING", "BOGUS", "MORNING"])).toEqual(["BOGUS", "MORNING", "EVENING"]);
});

// -- getDaysInMonth ---------------------------------------------------------

test("getDaysInMonth returns 31 for January", () => {
  expect(getDaysInMonth(2026, 0)).toBe(31);
});

test("getDaysInMonth returns 28 for February in a non-leap year", () => {
  expect(getDaysInMonth(2026, 1)).toBe(28);
});

test("getDaysInMonth returns 29 for February in a leap year", () => {
  expect(getDaysInMonth(2024, 1)).toBe(29);
});

test("getDaysInMonth returns 30 for April", () => {
  expect(getDaysInMonth(2026, 3)).toBe(30);
});

// -- getFirstDayOfMonth ------------------------------------------------------

test("getFirstDayOfMonth returns the JS day-of-week (0=Sunday) of the 1st", () => {
  // 2026-07-01 is a Wednesday.
  expect(getFirstDayOfMonth(2026, 6)).toBe(3);
});

test("getFirstDayOfMonth returns 0 when the month starts on a Sunday", () => {
  // 2026-03-01 is a Sunday.
  expect(getFirstDayOfMonth(2026, 2)).toBe(0);
});
