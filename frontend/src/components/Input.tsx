import { Component } from "solid-js";
import { HtmlIdFamily } from "./htmlIds";

type InputProps = {
  label: string;
  name?: string;
  placeholder: string;
};

const Input: Component<InputProps> = (props) => {
  const inputId = new HtmlIdFamily("input");
  return (
    <div class="flex w-full max-w-xs flex-col gap-1 text-on-surface dark:text-on-surface-dark">
      <label for={inputId.id()} class="w-fit pl-0.5 text-sm">
        {props.label}
      </label>
      <input
        id={inputId.id()}
        type="text"
        class="w-full rounded-radius border border-outline bg-surface-alt px-2 py-2 text-sm focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary disabled:cursor-not-allowed disabled:opacity-75 dark:border-outline-dark dark:bg-surface-dark-alt/50 dark:focus-visible:outline-primary-dark"
        name={props.name}
        placeholder={props.placeholder}
        autocomplete="name"
      />
    </div>
  );
};

export default Input;
