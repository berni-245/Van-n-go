import { children, For, ParentComponent, Show } from "solid-js";
import { useTableContext } from "./Context";
import { CheckboxIcon } from "./CheckboxIcon";

export const TableRow: ParentComponent = (props) => {
  const tableState = useTableContext();
  const [checkAll, _] = tableState.checkAll;
  const resolvedChildren = children(() => props.children);
  return (
    <tr class="even:bg-primary/5 dark:even:bg-primary-dark/10">
      <Show when={tableState.checkbox}>
        <td class="p-4">
          <label for="user2335" class="flex items-center text-on-surface dark:text-on-surface-dark">
            <div class="relative flex items-center">
              <input
                type="checkbox"
                id="user2335"
                class="before:content[''] peer relative size-4 appearance-none overflow-hidden rounded border border-outline bg-surface before:absolute before:inset-0 checked:border-primary checked:before:bg-primary focus:outline-2 focus:outline-offset-2 focus:outline-outline-strong checked:focus:outline-primary active:outline-offset-0 dark:border-outline-dark dark:bg-surface-dark-alt dark:checked:border-primary-dark dark:checked:before:bg-primary-dark dark:focus:outline-outline-dark-strong dark:checked:focus:outline-primary-dark"
                checked={checkAll()}
              />
              <CheckboxIcon />
            </div>
          </label>
        </td>
      </Show>
      <For each={resolvedChildren.toArray()}>{(item) => <td class="p-4">{item}</td>}</For>;
    </tr>
  );
};
