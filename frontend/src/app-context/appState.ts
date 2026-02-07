import { debounce } from "@solid-primitives/scheduled";
import { UserState } from "./userState";
import { initialLocale, Locale } from "./locale";
import { TokenPayload } from "../api/jwt";

export type Theme = "dark" | "light";

export type AuthSession = {
  authToken: string;
  refreshToken: string;
  payload: TokenPayload;
};

export interface AppState {
  session: AuthSession | null;
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
