import { expect, test } from "vitest";
import {
  isValidPlateNumber,
  isValidVehicleVolume,
  isValidHourlyRate,
  isValidVehicleDescriptionLength,
  isValidEmail,
  MAX_VEHICLE_DESCRIPTION_LENGTH,
} from "@/utils/validation";

// -- isValidPlateNumber ---------------------------------------------------

test("isValidPlateNumber accepts the old format (3 letters + 3 digits)", () => {
  expect(isValidPlateNumber("ABC123")).toBe(true);
});

test("isValidPlateNumber accepts the Mercosur format (2 letters + 3 digits + 2 letters)", () => {
  expect(isValidPlateNumber("AB123CD")).toBe(true);
});

test("isValidPlateNumber accepts lowercase letters in either format", () => {
  expect(isValidPlateNumber("abc123")).toBe(true);
  expect(isValidPlateNumber("ab123cd")).toBe(true);
});

test("isValidPlateNumber rejects a plate with the wrong number of digits", () => {
  expect(isValidPlateNumber("ABC12")).toBe(false);
  expect(isValidPlateNumber("ABC1234")).toBe(false);
});

test("isValidPlateNumber rejects a plate that mixes the two formats", () => {
  expect(isValidPlateNumber("ABC12CD")).toBe(false);
  expect(isValidPlateNumber("AB1234")).toBe(false);
});

test("isValidPlateNumber rejects an empty string", () => {
  expect(isValidPlateNumber("")).toBe(false);
});

test("isValidPlateNumber rejects a plate with symbols or spaces", () => {
  expect(isValidPlateNumber("ABC-123")).toBe(false);
  expect(isValidPlateNumber("ABC 123")).toBe(false);
});

test("isValidPlateNumber rejects extra leading/trailing characters (not anchored loosely)", () => {
  expect(isValidPlateNumber("XABC123")).toBe(false);
  expect(isValidPlateNumber("ABC123X")).toBe(false);
});

// -- isValidVehicleVolume --------------------------------------------------

test("isValidVehicleVolume accepts values within [1, 100]", () => {
  expect(isValidVehicleVolume(1)).toBe(true);
  expect(isValidVehicleVolume(50)).toBe(true);
  expect(isValidVehicleVolume(100)).toBe(true);
});

test("isValidVehicleVolume rejects values outside [1, 100]", () => {
  expect(isValidVehicleVolume(0.99)).toBe(false);
  expect(isValidVehicleVolume(100.01)).toBe(false);
});

test("isValidVehicleVolume rejects NaN, e.g. from parseFloat('abc')", () => {
  expect(isValidVehicleVolume(NaN)).toBe(false);
});

// -- isValidHourlyRate ------------------------------------------------------

test("isValidHourlyRate accepts values within [100, 100000]", () => {
  expect(isValidHourlyRate(100)).toBe(true);
  expect(isValidHourlyRate(50000)).toBe(true);
  expect(isValidHourlyRate(100000)).toBe(true);
});

test("isValidHourlyRate rejects values outside [100, 100000]", () => {
  expect(isValidHourlyRate(99.99)).toBe(false);
  expect(isValidHourlyRate(100000.01)).toBe(false);
});

test("isValidHourlyRate rejects NaN, e.g. from parseFloat('abc')", () => {
  expect(isValidHourlyRate(NaN)).toBe(false);
});

// -- isValidVehicleDescriptionLength -----------------------------------------

test("isValidVehicleDescriptionLength accepts a description at the max length", () => {
  expect(isValidVehicleDescriptionLength("a".repeat(MAX_VEHICLE_DESCRIPTION_LENGTH))).toBe(true);
});

test("isValidVehicleDescriptionLength rejects a description one char over the max length", () => {
  expect(isValidVehicleDescriptionLength("a".repeat(MAX_VEHICLE_DESCRIPTION_LENGTH + 1))).toBe(false);
});

test("isValidVehicleDescriptionLength accepts an empty description (length alone doesn't require content)", () => {
  expect(isValidVehicleDescriptionLength("")).toBe(true);
});

// -- isValidEmail -------------------------------------------------------------

test("isValidEmail accepts a plain address", () => {
  expect(isValidEmail("user@example.com")).toBe(true);
});

test("isValidEmail accepts an address with dots and a subdomain", () => {
  expect(isValidEmail("first.last@mail.example.com")).toBe(true);
});

test("isValidEmail rejects a missing '@'", () => {
  expect(isValidEmail("userexample.com")).toBe(false);
});

test("isValidEmail rejects a missing domain dot", () => {
  expect(isValidEmail("user@example")).toBe(false);
});

test("isValidEmail rejects whitespace inside the address", () => {
  expect(isValidEmail("us er@example.com")).toBe(false);
});

test("isValidEmail rejects an empty string", () => {
  expect(isValidEmail("")).toBe(false);
});

test("isValidEmail rejects more than one '@'", () => {
  expect(isValidEmail("user@@example.com")).toBe(false);
});
