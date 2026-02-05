import { debounce } from "@solid-primitives/scheduled";
import { UserState } from "./userState";
import { initialLocale, Locale } from "./locale";

export type Theme = "dark" | "light";

export interface AppState {
  session: {
    authToken: string;
    refreshToken: string;
  } | null;
  user: UserState | null;
  preferences: {
    theme: Theme;
    locale: Locale;
  };
}

export function getSavedState() {
  const saved = localStorage.getItem("appState");
  if (saved) {
    return JSON.parse(saved) as AppState;
  }
}

export const saveState = debounce((state: AppState) => {
  localStorage.setItem("appState", JSON.stringify(state));
}, 150);

export function initialState(): AppState {
  const saved = getSavedState();
  if (saved) return saved;

  const darkThemeMq = window.matchMedia("(prefers-color-scheme: dark)");
  let theme: Theme;
  if (darkThemeMq.matches) {
    theme = "dark";
  } else {
    theme = "light";
  }

  return {
    session: null,
    user: null,
    preferences: { theme, locale: initialLocale() },
  };
}
