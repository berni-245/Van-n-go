import { TokenPayloadSchema } from "@/api/jwt";
import { initialLocale } from "@/app-context/locale";
import * as z from "zod";
import { LocaleSchema } from "@/app-context/locale";
import { UserStateSchema } from "@/app-context/userState";

export type Theme = z.infer<typeof ThemeSchema>;
export type AppState = z.infer<typeof AppStateSchema>;

const ThemeSchema = z.enum(["dark", "light"]);

const AuthSessionSchema = z.object({
  token: z.object({
    auth: z.string(),
    refresh: z.string(),
  }),
  payload: z.object({
    auth: TokenPayloadSchema,
    refresh: TokenPayloadSchema,
  }),
});

const AppStateSchema = z.object({
  session: AuthSessionSchema.nullable(),
  user: UserStateSchema.nullable(),
  preferences: z.object({
    theme: ThemeSchema,
    locale: LocaleSchema,
  }),
});

export function getSavedState(): AppState | null {
  const saved = localStorage.getItem("appState");
  if (saved) {
    try {
      const parsed = JSON.parse(saved);
      const state = AppStateSchema.safeParse(parsed);
      if (state.success) return state.data;
    } catch {}
    console.log("Found malformed state in localStorage. Resetting.");
  }
  return null;
}

export const saveState = (state: AppState) => {
  localStorage.setItem("appState", JSON.stringify(state));
}

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
