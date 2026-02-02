import { children, For, ParentComponent, Show } from "solid-js";
import { useTableContext } from "./Context";
import { CheckboxIcon } from "./CheckboxIcon";
import { HtmlIdFamily } from "../htmlIds";

export const TableHeaders: ParentComponent = (props) => {
  const tableState = useTableContext();
  const [_, setCheckAll] = tableState.checkAll;

  const handleInput = (e: Event) => {
    const input = e.currentTarget as HTMLInputElement;
    const nextState = input.checked;
    setCheckAll(nextState);
  };

  const resolvedChildren = children(() => props.children);

  const checkId = new HtmlIdFamily('check-all');

  return (
    <thead class="border-b border-outline bg-surface-alt text-sm text-on-surface-strong dark:border-outline-dark dark:bg-surface-dark-alt dark:text-on-surface-dark-strong">
      <tr>
        <Show when={tableState.checkbox}>
          <th scope="col" class="p-4">
            <label for={checkId.id()} class="flex items-center text-on-surface dark:text-on-surface-dark">
              <div class="relative flex items-center">
                <input
                  type="checkbox"
                  id={checkId.id()}
                  class="before:content[''] peer relative size-4 appearance-none overflow-hidden rounded border border-outline bg-surface before:absolute before:inset-0 checked:border-primary checked:before:bg-primary focus:outline-2 focus:outline-offset-2 focus:outline-outline-strong checked:focus:outline-primary active:outline-offset-0 dark:border-outline-dark dark:bg-surface-dark-alt dark:checked:border-primary-dark dark:checked:before:bg-primary-dark dark:focus:outline-outline-dark-strong dark:checked:focus:outline-primary-dark"
                  oninput={handleInput}
                />
                <CheckboxIcon />
              </div>
            </label>
          </th>
        </Show>
        <For each={resolvedChildren.toArray()}>
          {(item) => (
            <th scope="col" class="p-4">
              {item}
            </th>
          )}
        </For>
      </tr>
    </thead>
  );
};
