import { expect, test } from "vitest";
import { HtmlIdFamily } from "@/components/htmlIds";

test("id() with no postfix returns the family's own element id", () => {
  const family = new HtmlIdFamily();
  expect(family.id()).toBe(family.elementId);
});

test("id(postfix) appends '-postfix' to the element id", () => {
  const family = new HtmlIdFamily();
  expect(family.id("label")).toBe(`${family.elementId}-label`);
});

test("a prefix is prepended to the element id, separated by '-'", () => {
  const family = new HtmlIdFamily("zone-input");
  expect(family.elementId.startsWith("zone-input-")).toBe(true);
  expect(family.id()).toBe(family.elementId);
  expect(family.id("error")).toBe(`${family.elementId}-error`);
});

test("with no prefix, the element id is used as-is", () => {
  const family = new HtmlIdFamily();
  expect(family.elementId.startsWith("-")).toBe(false);
});

test("every family gets a distinct element id, even with the same prefix", () => {
  const a = new HtmlIdFamily("field");
  const b = new HtmlIdFamily("field");
  expect(a.elementId).not.toBe(b.elementId);
});

test("an empty-string prefix is treated as 'no prefix' (falsy check, not a null/undefined check)", () => {
  // The constructor guards with `if (prefix)`, so "" -- while a valid
  // string -- takes the same branch as omitting the argument entirely.
  const family = new HtmlIdFamily("");
  expect(family.elementId.startsWith("-")).toBe(false);
});
