import { ParentComponent, createSignal } from "solid-js";
import { TableContext, TableState } from "./Context";
import { TableHeaders } from "./Headers";
import { TableRow } from "./Rows";

const TableContent: ParentComponent = (props) => {
  return <tbody class="divide-y divide-outline dark:divide-outline-dark">{props.children}</tbody>;
};

const TableRoot: ParentComponent<{ checkbox?: boolean }> = (props) => {
  const tableState: TableState = { checkbox: props.checkbox ?? false, checkAll: createSignal(false) };

  return (
    <TableContext.Provider value={tableState}>
      <div class="overflow-hidden w-full overflow-x-auto rounded-radius border border-outline dark:border-outline-dark">
        <table class="w-full text-left text-sm text-on-surface dark:text-on-surface-dark">{props.children}</table>
      </div>
    </TableContext.Provider>
  );
};

export const Table = Object.assign(TableRoot, {
  Headers: TableHeaders,
  Content: TableContent,
  Row: TableRow,
});
