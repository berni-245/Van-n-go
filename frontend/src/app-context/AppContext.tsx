import { createContext, createEffect, createResource, ParentComponent, Suspense, useContext } from "solid-js";
import { Meta, Title } from "@solidjs/meta";
import { AppState, initialState, saveState } from "./appState";
import { createStore, SetStoreFunction } from "solid-js/store";
import * as i18n from "@solid-primitives/i18n";
import { Dictionary, en_flat_dict, fetchDictionary } from "./locale";

const AppStateContext = createContext<[AppState, SetStoreFunction<AppState>]>();
const I18nContext = createContext<i18n.Translator<Dictionary>>();

export const useAppState = () => {
  const ctx = useContext(AppStateContext);
  if (!ctx) throw new Error("AppStateContext not found");
  return ctx;
};

export function useTranslator() {
  const t = useContext(I18nContext);
  if (!t) throw new Error("I18nContext not found");
  return t;
}

export const AppContextProvider: ParentComponent = (props) => {
  const [appState, setAppState] = createStore<AppState>(initialState());

  createEffect(() => {
    // NOTE: we need to explicitly access the store values for the effect to trigger on state changes.
    // This means `session` and `user` will only trigger the effect if replaced whole. Since we are
    // accessing the individual attributes for `preferences`, then a change in them will also trigger
    // the effect.
    // If the state becomes more comples, or we want to trigger on internal `user` or `session` changes,
    // a solution could be either [`deepTrack`](https://github.com/solidjs/solid/discussions/829#discussioncomment-2102335)
    // or [wrapping `setAppState` to toggle a signal on any change](https://github.com/solidjs/solid/discussions/829#discussioncomment-2104793)
    // and then listen for changes to that signal.
    saveState({
      session: appState.session,
      user: appState.user,
      preferences: {
        theme: appState.preferences.theme,
        locale: appState.preferences.locale,
      }
    });
  });

  const [dict] = createResource(() => appState.preferences.locale, fetchDictionary, { initialValue: en_flat_dict });
  const t = i18n.translator(dict, i18n.resolveTemplate);

  return (
    <Suspense>
      <AppStateContext.Provider value={[appState, setAppState]}>
        <I18nContext.Provider value={t}>
          <Title>{t("global.title")}</Title>
          <Meta name="lang" content={appState.preferences.locale} />
          <div>{props.children}</div>
        </I18nContext.Provider>
      </AppStateContext.Provider>
    </Suspense>
  );
};
