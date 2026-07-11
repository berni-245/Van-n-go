import { Component, createSignal } from "solid-js";
import { HtmlIdFamily } from "./htmlIds";

type ToggleProps = {
  disabled?: boolean;
  label?: string;
  checked?: boolean; // Controlled value
  defaultChecked?: boolean; // Uncontrolled initial value
  onToggle?: (checked: boolean, e?: Event) => void;
};

const Toggle: Component<ToggleProps> = (props) => {
  const isControlled = () => props.checked !== undefined;
  const [localChecked, setLocalChecked] = createSignal<boolean>(!!props.defaultChecked);

  const checked = () => (isControlled() ? !!props.checked : localChecked());

  const handleInput = (e: Event) => {
    const input = e.currentTarget as HTMLInputElement;
    const nextState = input.checked;
    if (!isControlled()) setLocalChecked(nextState);
    props.onToggle?.(nextState, e);
  };

  const toggleId = new HtmlIdFamily("toggle");

  return (
    <label for={toggleId.id()} class="inline-flex items-center gap-3">
      <input
        id={toggleId.id()}
        type="checkbox"
        class="peer sr-only"
        role="switch"
        disabled={props.disabled}
        checked={checked()}
        onInput={handleInput}
      />

      {props.label && (
        <span class="tracking-wide font-medium text-on-surface peer-checked:text-on-surface-strong peer-disabled:cursor-not-allowed peer-disabled:opacity-70 dark:text-on-surface-dark dark:peer-checked:text-on-surface-dark-strong">
          {props.label}
        </span>
      )}

      <div
        class="relative h-6 w-11 after:h-5 after:w-5 peer-checked:after:translate-x-5 rounded-full border border-outline bg-surface-alt after:absolute after:bottom-0 after:left-[0.0625rem] after:top-0 after:my-auto after:rounded-full after:bg-on-surface after:transition-all after:content-[''] peer-checked:bg-primary peer-checked:after:bg-on-primary peer-focus:outline-2 peer-focus:outline-offset-2 peer-focus:outline-outline-strong peer-focus:peer-checked:outline-primary peer-active:outline-offset-0 peer-disabled:cursor-not-allowed peer-disabled:opacity-70 dark:border-outline-dark dark:bg-surface-dark-alt dark:after:bg-on-surface-dark dark:peer-checked:bg-primary-dark dark:peer-checked:after:bg-on-primary-dark dark:peer-focus:outline-outline-dark-strong dark:peer-focus:peer-checked:outline-primary-dark"
        aria-hidden="true"
      />
    </label>
  );
};

export default Toggle;
