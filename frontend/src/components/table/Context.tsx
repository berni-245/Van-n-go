import { createContext, useContext } from "solid-js";
import { Signal } from "solid-js/types/server/reactive.js";

export interface TableState {
  checkbox: boolean;
  checkAll: Signal<boolean>;
}

export const TableContext = createContext<TableState>();

export function useTableContext() {
  const context = useContext(TableContext);
  if (!context) {
    throw new Error("Can't find TableContext");
  }
  return context;
}
