import * as i18n from "@solid-primitives/i18n";
import { Meta, Title } from "@solidjs/meta";
import {createContext, createEffect, createResource, ParentComponent, useContext} from "solid-js";
import { createStore, SetStoreFunction } from "solid-js/store";
import { AppState, initialState, saveState } from "./appState";
import { Dictionary, en_flat_dict, fetchDictionary } from "./locale";
import { Api, initializeApi } from "./api";
import {Toaster, ToastProvider} from "solid-notifications";

const AppStateContext = createContext<[AppState, SetStoreFunction<AppState>]>();
const I18nContext = createContext<i18n.Translator<Dictionary>>();
const ApiContext = createContext<Api>();

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

export function useApi() {
  const api = useContext(ApiContext);
  if (!api) throw new Error("ApiContext not found");
  return api;
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
      },
    });
    console.log("Saved state");
  });

  createEffect(() => {
    const theme = appState.preferences.theme;
    document.documentElement.setAttribute("data-theme", theme);
  });

  const [dict] = createResource(() => appState.preferences.locale, fetchDictionary, { initialValue: en_flat_dict });
  const t = i18n.translator(dict, i18n.resolveTemplate);

  const api = initializeApi(appState, setAppState);

  return (
      <AppStateContext.Provider value={[appState, setAppState]}>
        <I18nContext.Provider value={t}>
          <ApiContext.Provider value={api}>
            <ToastProvider
                theme={appState.preferences.theme}
                positionY="bottom"
                class={({type}) => {
                  const classes = "flex w-full items-center gap-2.5 p-4 border rounded-radius ";
                  switch (type) {
                    case "success":
                      return classes + "bg-success/10 border-success";
                    case "error":
                      return classes + "bg-danger/10 border-danger";
                    case "warning":
                      return classes + "bg-warning/10 border-warning";
                    case "info":
                      return classes + "bg-info/10 border-info";
                    case "loading":
                      return classes;
                    case "default":
                      return classes;
                  }
                }}
                wrapperClass="bg-surface text-on-surface dark:bg-surface-dark dark:text-on-surface-dark"
            >
            <Title>{t("global.title")}</Title>
            <Meta name="lang" content={appState.preferences.locale} />
              <Toaster/>
            <div>{props.children}</div>
            </ToastProvider>
          </ApiContext.Provider>
        </I18nContext.Provider>
      </AppStateContext.Provider>
  );
};
