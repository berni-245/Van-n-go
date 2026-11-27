import { afterEach, expect, test, vi } from "vitest";
import { getSavedState, saveState, initialState, type AppState } from "@/app-context/appState";

const validState: AppState = {
  session: null,
  user: null,
  preferences: { theme: "light", locale: "en" },
};

afterEach(() => {
  localStorage.clear();
  vi.restoreAllMocks();
});

test("getSavedState returns null when nothing has been saved yet", () => {
  expect(getSavedState()).toBeNull();
});

test("saveState + getSavedState round-trips a valid AppState through localStorage", () => {
  saveState(validState);
  expect(getSavedState()).toEqual(validState);
});

test("getSavedState returns null for JSON that doesn't match the schema (e.g. an invalid theme)", () => {
  localStorage.setItem(
    "appState",
    JSON.stringify({ session: null, user: null, preferences: { theme: "purple", locale: "en" } })
  );
  expect(getSavedState()).toBeNull();
});

test("getSavedState returns null, rather than throwing, for unparseable JSON", () => {
  localStorage.setItem("appState", "{not valid json");
  expect(() => getSavedState()).not.toThrow();
  expect(getSavedState()).toBeNull();
});

test("initialState returns the saved state when one exists, without consulting matchMedia", () => {
  saveState(validState);
  const matchMediaSpy = vi.spyOn(window, "matchMedia");

  expect(initialState()).toEqual(validState);
  expect(matchMediaSpy).not.toHaveBeenCalled();
});

test("initialState picks the dark theme from the OS preference when nothing is saved", () => {
  vi.spyOn(window, "matchMedia").mockReturnValue({ matches: true } as MediaQueryList);
  expect(initialState().preferences.theme).toBe("dark");
});

test("initialState picks the light theme when the OS has no dark preference and nothing is saved", () => {
  vi.spyOn(window, "matchMedia").mockReturnValue({ matches: false } as MediaQueryList);
  expect(initialState().preferences.theme).toBe("light");
});

test("initialState starts with no session and no user when nothing is saved", () => {
  vi.spyOn(window, "matchMedia").mockReturnValue({ matches: false } as MediaQueryList);
  const state = initialState();
  expect(state.session).toBeNull();
  expect(state.user).toBeNull();
});
