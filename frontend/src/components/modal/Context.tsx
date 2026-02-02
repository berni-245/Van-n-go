import { createContext, useContext } from "solid-js";
import { Signal } from "solid-js/types/server/reactive.js";
import { HtmlIdFamily } from "../htmlIds";

export interface ModalState {
  open: Signal<boolean>;
  htmlId: HtmlIdFamily;
}

export const ModalContext = createContext<ModalState>();

export function useModalContext() {
  const context = useContext(ModalContext);
  if (!context) {
    throw new Error("Can't find ModalContext");
  }
  return context;
}
