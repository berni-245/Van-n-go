import { expect, test } from "vitest";
import { pagesArray } from "@/components/pagination/Pagination";

test("shows every page when the total fits within the visible window (<=5)", () => {
  expect(pagesArray(1, 1, 5)).toEqual([1, 2, 3, 4, 5]);
});

test("shows every page when there's only one page", () => {
  expect(pagesArray(1, 1, 1)).toEqual([1]);
});

test("centers the window around the current page when there's room on both sides", () => {
  expect(pagesArray(5, 1, 10)).toEqual([3, 4, 5, 6, 7]);
});

test("clamps the window to the start when the current page is near the first page", () => {
  expect(pagesArray(1, 1, 10)).toEqual([1, 2, 3, 4, 5]);
});

test("clamps the window to the end when the current page is near the last page", () => {
  expect(pagesArray(10, 1, 10)).toEqual([6, 7, 8, 9, 10]);
});

test("clamps the window to the end when the current page IS the last page", () => {
  expect(pagesArray(10, 1, 10)).toEqual([6, 7, 8, 9, 10]);
});

test("always returns exactly maxVisible (5) pages once the range exceeds the window", () => {
  expect(pagesArray(50, 1, 100)).toHaveLength(5);
});

test("respects a non-1 firstPage when clamping to the start", () => {
  expect(pagesArray(3, 3, 20)).toEqual([3, 4, 5, 6, 7]);
});
