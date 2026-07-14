import { expect, test } from "vitest";
import { parsePageInfo } from "@/app-context/pagination";

function responseWithLink(link: string | null) {
  const headers = new Headers();
  if (link !== null) headers.set("link", link);
  return new Response(null, { headers });
}

test("returns null when there's no Link header at all", () => {
  expect(parsePageInfo(responseWithLink(null))).toBeNull();
});

test("parses first/last/next/prev links into a PageInfo", () => {
  const link =
    '<https://api.example.com/drivers?page=1>; rel="first", ' +
    '<https://api.example.com/drivers?page=5>; rel="last", ' +
    '<https://api.example.com/drivers?page=3>; rel="next", ' +
    '<https://api.example.com/drivers?page=1>; rel="prev"';

  expect(parsePageInfo(responseWithLink(link))).toEqual({
    first: { url: "https://api.example.com/drivers?page=1", pageNumber: 1, rel: "first" },
    last: { url: "https://api.example.com/drivers?page=5", pageNumber: 5, rel: "last" },
    next: { url: "https://api.example.com/drivers?page=3", pageNumber: 3, rel: "next" },
    prev: { url: "https://api.example.com/drivers?page=1", pageNumber: 1, rel: "prev" },
  });
});

test("next/prev are optional -- first+last alone is a valid PageInfo", () => {
  const link =
    '<https://api.example.com/drivers?page=1>; rel="first", <https://api.example.com/drivers?page=1>; rel="last"';

  expect(parsePageInfo(responseWithLink(link))).toEqual({
    first: { url: "https://api.example.com/drivers?page=1", pageNumber: 1, rel: "first" },
    last: { url: "https://api.example.com/drivers?page=1", pageNumber: 1, rel: "last" },
  });
});

test("returns null when 'last' is missing, even with other rels present", () => {
  const link =
    '<https://api.example.com/drivers?page=1>; rel="first", <https://api.example.com/drivers?page=2>; rel="next"';
  expect(parsePageInfo(responseWithLink(link))).toBeNull();
});

test("returns null when 'first' is missing, even with other rels present", () => {
  const link =
    '<https://api.example.com/drivers?page=5>; rel="last", <https://api.example.com/drivers?page=4>; rel="prev"';
  expect(parsePageInfo(responseWithLink(link))).toBeNull();
});

test("skips a link entry with an unrecognized rel, keeping the rest", () => {
  const link =
    '<https://api.example.com/drivers?page=1>; rel="first", ' +
    '<https://api.example.com/drivers?page=5>; rel="last", ' +
    '<https://api.example.com/drivers?page=9>; rel="bogus"';

  expect(parsePageInfo(responseWithLink(link))).toEqual({
    first: { url: "https://api.example.com/drivers?page=1", pageNumber: 1, rel: "first" },
    last: { url: "https://api.example.com/drivers?page=5", pageNumber: 5, rel: "last" },
  });
});

test("drops a link entry whose URL has no 'page' query param, which can cascade to an overall null", () => {
  const link = '<https://api.example.com/drivers>; rel="first", <https://api.example.com/drivers?page=5>; rel="last"';
  // "first" has no page param and gets silently dropped, so the "must have
  // both first and last" check then fails on the whole response.
  expect(parsePageInfo(responseWithLink(link))).toBeNull();
});

test("drops a link entry whose 'page' value isn't parseable as a number at all", () => {
  const link =
    '<https://api.example.com/drivers?page=abc>; rel="first", <https://api.example.com/drivers?page=5>; rel="last"';
  expect(parsePageInfo(responseWithLink(link))).toBeNull();
});

test("truncates a non-integer page value instead of rejecting it (documents current behavior, via parseInt)", () => {
  // parseInt("2.9") === 2, and Number.isInteger is checked *after* that
  // truncation, so a fractional page value is silently rounded down rather
  // than treated as invalid the way "abc" is above.
  const link =
    '<https://api.example.com/drivers?page=1>; rel="first", <https://api.example.com/drivers?page=2.9>; rel="last"';
  expect(parsePageInfo(responseWithLink(link))?.last).toEqual({
    url: "https://api.example.com/drivers?page=2.9",
    pageNumber: 2,
    rel: "last",
  });
});

test("skips a malformed link entry (missing the '; rel=\"...\"' segment)", () => {
  const link = '<https://api.example.com/drivers?page=1>, <https://api.example.com/drivers?page=5>; rel="last"';
  expect(parsePageInfo(responseWithLink(link))).toBeNull();
});
