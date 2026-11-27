import { expect, test } from "vitest";
import { isAllowedFileType, isWithinSizeLimit, SizeMultiplier } from "@/utils/fileValidation";

// -- isAllowedFileType ----------------------------------------------------

test("isAllowedFileType accepts a MIME type whose subtype is in the allow-list", () => {
  expect(isAllowedFileType("image/png", ["png", "jpeg"])).toBe(true);
});

test("isAllowedFileType rejects a MIME type whose subtype isn't in the allow-list", () => {
  expect(isAllowedFileType("application/pdf", ["png", "jpeg"])).toBe(false);
});

test("isAllowedFileType matches case-insensitively on both sides", () => {
  expect(isAllowedFileType("image/PNG", ["png"])).toBe(true);
  expect(isAllowedFileType("image/png", ["PNG"])).toBe(true);
});

test("isAllowedFileType rejects when the allow-list is empty", () => {
  expect(isAllowedFileType("image/png", [])).toBe(false);
});

// -- isWithinSizeLimit ------------------------------------------------------

test("isWithinSizeLimit accepts a file exactly at the limit", () => {
  expect(isWithinSizeLimit(5 * SizeMultiplier.MB, 5, "MB")).toBe(true);
});

test("isWithinSizeLimit accepts a file under the limit", () => {
  expect(isWithinSizeLimit(1 * SizeMultiplier.MB, 5, "MB")).toBe(true);
});

test("isWithinSizeLimit rejects a file over the limit", () => {
  expect(isWithinSizeLimit(5 * SizeMultiplier.MB + 1, 5, "MB")).toBe(false);
});

test("isWithinSizeLimit converts the unit correctly for KB and GB", () => {
  expect(isWithinSizeLimit(500 * SizeMultiplier.KB, 1, "MB")).toBe(true);
  expect(isWithinSizeLimit(2 * SizeMultiplier.GB, 1, "GB")).toBe(false);
});

test("isWithinSizeLimit treats a zero-byte file as within any positive limit", () => {
  expect(isWithinSizeLimit(0, 1, "B")).toBe(true);
});
