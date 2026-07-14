import { afterEach, expect, test, vi } from "vitest";
import { en_flat_dict, fetchDictionary, initialLocale, toLocale } from "@/app-context/locale";

afterEach(() => {
  vi.restoreAllMocks();
  vi.doUnmock("~/lang/es/es");
});

test("toLocale accepts exactly the configured locales, case-sensitively", () => {
  expect(toLocale("en")).toBe("en");
  expect(toLocale("es")).toBe("es");
  expect(toLocale("fr")).toBeUndefined();
  // Case-sensitive on purpose: normalizing is the caller's job (see
  // initialLocale, which lowercases before its second check below).
  expect(toLocale("EN")).toBeUndefined();
});

function withNavigatorLanguage(language: string) {
  vi.spyOn(window.navigator, "language", "get").mockReturnValue(language);
}

test("initialLocale picks the locale from a language-region tag (e.g. 'es-AR' -> 'es')", () => {
  withNavigatorLanguage("es-AR");
  expect(initialLocale()).toBe("es");
});

test("initialLocale falls back to the lowercased full tag when the 2-letter prefix alone doesn't match", () => {
  // "ES" sliced to its first two characters is still "ES", which fails the
  // case-sensitive first check against "es" -- it's only picked up by the
  // second check, which lowercases the *whole* tag before comparing.
  withNavigatorLanguage("ES");
  expect(initialLocale()).toBe("es");
});

test("initialLocale defaults to 'en' for an unsupported language", () => {
  withNavigatorLanguage("fr-FR");
  expect(initialLocale()).toBe("en");
});

test("fetchDictionary('en') returns the flattened English dictionary directly", async () => {
  const dict = await fetchDictionary("en");
  expect(dict).toBe(en_flat_dict);
  expect(dict.global.login).toBe("Log in");
  // flatten() also adds a dot-joined shortcut alongside the nested shape.
  expect(dict["global.login"]).toBe("Log in");
});

test("fetchDictionary('es') overlays Spanish onto English only one level deep (documents current behavior)", async () => {
  vi.doMock("~/lang/es/es", () => ({
    dict: { global: { login: "Iniciar sesión" } }, // only translates one key of "global"
  }));
  vi.resetModules();
  const fresh = await import("@/app-context/locale");

  const dict = await fresh.fetchDictionary("es");

  expect(dict.global.login).toBe("Iniciar sesión"); // the translated leaf comes through
  // The merge is `{ ...en_flat_dict, ...flat_dict }` -- a shallow, top-level
  // merge. The mocked "global" object REPLACES the entire English "global"
  // namespace rather than being deep-merged key-by-key into it, so any
  // English key under "global" that the translation didn't repeat (e.g.
  // "title") is lost rather than falling back.
  expect(dict.global.title).toBeUndefined();
  // A namespace the translation didn't touch at all (e.g. "client") still
  // falls back to English in full, since the merge only replaces keys the
  // translation actually provided.
  expect(dict.client.search.selectZone).toBe(fresh.en_flat_dict.client.search.selectZone);
});
