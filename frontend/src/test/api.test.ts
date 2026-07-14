import { expect, test } from "vitest";
import { apiRequest, extractNumericIdFromEndOfPath, parseTotalPages, API_ROOT } from "@/app-context/api";

// -- apiRequest --------------------------------------------------------

test("apiRequest resolves a relative path against API_ROOT", () => {
  const req = apiRequest("drivers/1");
  expect(req.url).toBe(`${API_ROOT}drivers/1`);
});

test("apiRequest leaves an absolute HATEOAS URI untouched", () => {
  const req = apiRequest("https://api.example.com/other-base/drivers/1");
  expect(req.url).toBe("https://api.example.com/other-base/drivers/1");
});

test("apiRequest rejects a path starting with '/', which would drop the API base", () => {
  expect(() => apiRequest("/drivers/1")).toThrowError(/relative API path or an absolute URI/);
});

test("apiRequest appends queryParams to a relative path", () => {
  const req = apiRequest("drivers", { queryParams: { page: "2", size: "10" } });
  const url = new URL(req.url);
  expect(url.searchParams.get("page")).toBe("2");
  expect(url.searchParams.get("size")).toBe("10");
});

test("apiRequest appends queryParams to an absolute URI without discarding its own path", () => {
  const req = apiRequest("https://api.example.com/base/drivers/1", { queryParams: { page: "3" } });
  const url = new URL(req.url);
  expect(url.pathname).toBe("/base/drivers/1");
  expect(url.searchParams.get("page")).toBe("3");
});

test("apiRequest forwards headers and other RequestInit options", () => {
  const req = apiRequest("drivers", { headers: { Accept: "application/vnd.test+json" } });
  expect(req.headers.get("Accept")).toBe("application/vnd.test+json");
});

// -- extractNumericIdFromEndOfPath --------------------------------------

test("extractNumericIdFromEndOfPath reads the trailing numeric segment", () => {
  expect(extractNumericIdFromEndOfPath("https://api.example.com/clients/42")).toBe(42);
});

test("extractNumericIdFromEndOfPath ignores a trailing slash", () => {
  expect(extractNumericIdFromEndOfPath("https://api.example.com/clients/42/")).toBe(42);
});

test("extractNumericIdFromEndOfPath returns null when the last segment isn't numeric", () => {
  expect(extractNumericIdFromEndOfPath("https://api.example.com/clients/abc")).toBeNull();
});

test("extractNumericIdFromEndOfPath returns null for an empty string", () => {
  expect(extractNumericIdFromEndOfPath("")).toBeNull();
});

test("extractNumericIdFromEndOfPath returns null when the path is only slashes", () => {
  expect(extractNumericIdFromEndOfPath("///")).toBeNull();
});

// -- parseTotalPages -----------------------------------------------------

test("parseTotalPages falls back to currentPage when there's no Link header", () => {
  expect(parseTotalPages(null, 3)).toBe(3);
});

test("parseTotalPages reads the page number off the 'last' rel", () => {
  const link =
    '<https://api.example.com/drivers?page=1>; rel="first", <https://api.example.com/drivers?page=7>; rel="last"';
  expect(parseTotalPages(link, 1)).toBe(7);
});

test("parseTotalPages falls back to currentPage when there's no 'last' rel", () => {
  const link =
    '<https://api.example.com/drivers?page=1>; rel="first", <https://api.example.com/drivers?page=2>; rel="next"';
  expect(parseTotalPages(link, 1)).toBe(1);
});

test("parseTotalPages falls back to currentPage when 'last' has no parseable page number", () => {
  const link = '<https://api.example.com/drivers?page=abc>; rel="last"';
  expect(parseTotalPages(link, 5)).toBe(5);
});
