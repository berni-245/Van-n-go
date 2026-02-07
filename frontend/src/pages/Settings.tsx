import { useAppState, useTranslator } from "../app-context/AppContext";
import { Theme } from "../app-context/appState";
import { Locale } from "../app-context/locale";
import Select from "../components/Select";

export default function Settings() {
  const [appState, setAppState] = useAppState();
  const t = useTranslator();
  return (
    <div class="flex gap-4">
      <Select
        label={t("global.language.label")}
        onchange={(event) => setAppState("preferences", { locale: event.currentTarget.value as Locale })}
      >
        <option value="en" selected={appState.preferences.locale === "en"}>
          {t("global.language.english")}
        </option>
        <option value="es" selected={appState.preferences.locale === "es"}>
          {t("global.language.spanish")}
        </option>
      </Select>
      <Select
        label={t("global.theme.label")}
        onchange={(event) => setAppState("preferences", { theme: event.currentTarget.value as Theme })}
      >
        <option value="dark" selected={appState.preferences.theme === "dark"}>
          {t("global.theme.dark")}
        </option>
        <option value="light" selected={appState.preferences.theme === "light"}>
          {t("global.theme.light")}
        </option>
      </Select>
    </div>
  );
}
