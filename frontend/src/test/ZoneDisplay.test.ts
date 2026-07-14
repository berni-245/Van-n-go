import { expect, test } from "vitest";
import { zoneDisplay } from "@/components/inputs/ZoneInput";

test("zoneDisplay joins neighborhood, province, and country when all are present", () => {
  expect(
    zoneDisplay({
      id: 1,
      neighborhood: { name: "Palermo" },
      province: { name: "Buenos Aires" },
      country: { name: "Argentina" },
    })
  ).toBe("Palermo, Buenos Aires, Argentina");
});

test("zoneDisplay omits a missing neighborhood without leaving an empty segment", () => {
  expect(
    zoneDisplay({
      id: 2,
      province: { name: "Buenos Aires" },
      country: { name: "Argentina" },
    })
  ).toBe("Buenos Aires, Argentina");
});

test("zoneDisplay omits a missing province", () => {
  expect(
    zoneDisplay({
      id: 3,
      neighborhood: { name: "Palermo" },
      country: { name: "Argentina" },
    })
  ).toBe("Palermo, Argentina");
});

test("zoneDisplay omits a missing country", () => {
  expect(
    zoneDisplay({
      id: 4,
      neighborhood: { name: "Palermo" },
      province: { name: "Buenos Aires" },
    })
  ).toBe("Palermo, Buenos Aires");
});

test("zoneDisplay returns just the one present field when the other two are missing", () => {
  expect(zoneDisplay({ id: 5, country: { name: "Argentina" } })).toBe("Argentina");
});

test("zoneDisplay returns an empty string when nothing is present", () => {
  expect(zoneDisplay({ id: 6 })).toBe("");
});

test("zoneDisplay drops a field whose name is an empty string, same as a missing field", () => {
  expect(
    zoneDisplay({
      id: 7,
      neighborhood: { name: "" },
      province: { name: "Buenos Aires" },
      country: { name: "Argentina" },
    })
  ).toBe("Buenos Aires, Argentina");
});
