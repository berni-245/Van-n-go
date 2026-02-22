import { Component, JSX, Show, splitProps, For } from "solid-js";
import { HtmlIdFamily } from "@/components/htmlIds";

type InputProps = {
  label?: string;
    error?: string | string[];
} & JSX.InputHTMLAttributes<HTMLInputElement>;

const TextInput: Component<InputProps> = (props) => {
  const inputId = new HtmlIdFamily("input");

  const [_, filteredProps] = splitProps(props, ["label", "error", "class"]);

  return (
    <div class={`flex w-full max-w-xs flex-col gap-1 text-on-surface dark:text-on-surface-dark ${props.class ?? ""}`}>
      <label
        for={inputId.id()}
        class="flex w-fit items-center gap-1 pl-0.5 text-sm"
        classList={{ "text-danger": !!props.error }}
      >
        <Show when={props.error}>
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 16 16"
            aria-hidden="true"
            fill="currentColor"
            class="w-4 h-4"
          >
            <path d="M5.28 4.22a.75.75 0 0 0-1.06 1.06L6.94 8l-2.72 2.72a.75.75 0 1 0 1.06 1.06L8 9.06l2.72 2.72a.75.75 0 1 0 1.06-1.06L9.06 8l2.72-2.72a.75.75 0 0 0-1.06-1.06L8 6.94 5.28 4.22Z" />
          </svg>
        </Show>
        {props.label}
      </label>
      <input
        id={inputId.id()}
        class="w-full rounded-radius border bg-surface-alt px-2 py-2 text-sm focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary disabled:cursor-not-allowed disabled:opacity-75 dark:bg-surface-dark-alt/50 dark:focus-visible:outline-primary-dark"
        classList={{ "border-danger": !!props.error }}
        {...filteredProps}
      />


        <Show when={props.error}>
            <div className="flex flex-col gap-0.5">
                <Show
                    when={Array.isArray(props.error)}
                    fallback={<small className="pl-0.5 text-danger">{props.error}</small>}
                >
                    <For each={props.error as string[]}>
                        {(err) => <small className="pl-0.5 text-danger">• {err}</small>}
                    </For>
                </Show>
            </div>
        </Show>

    </div>
  );
};

export default TextInput;
