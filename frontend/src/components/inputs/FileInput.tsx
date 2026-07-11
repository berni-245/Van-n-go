import { Component, JSX, Show, splitProps, For } from "solid-js";
import { HtmlIdFamily } from "@/components/htmlIds";

type FileInputProps = {
  label?: string;
  error?: string | string[];
  helperText?: string;
} & JSX.InputHTMLAttributes<HTMLInputElement>;

const FileInput: Component<FileInputProps> = (props) => {
  const inputId = new HtmlIdFamily("input");

  const [local, filteredProps] = splitProps(props, ["label", "error", "class", "helperText"]);

  return (
    <div
      class={
        "relative flex w-full max-w-sm flex-col gap-1 text-on-surface dark:text-on-surface-dark " + (local.class || "")
      }
    >
      <label
        for={inputId.id()}
        class="flex w-fit items-center gap-1 pl-0.5 text-sm"
        classList={{ "text-danger": !!local.error }}
      >
        <Show when={local.error}>
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
        {local.label}
      </label>

      <input
        id={inputId.id()}
        type="file"
        class="w-full max-w-md overflow-clip rounded-radius border bg-surface-alt/50 text-sm file:mr-4 file:border-none file:bg-surface-alt file:px-4 file:py-2 file:font-medium file:text-on-surface-strong focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary disabled:cursor-not-allowed disabled:opacity-75 dark:bg-surface-dark-alt/50 dark:file:bg-surface-dark-alt dark:file:text-on-surface-dark-strong dark:focus-visible:outline-primary-dark"
        classList={{
          "border-danger": !!local.error,
          "border-outline dark:border-outline-dark": !local.error,
        }}
        {...filteredProps}
      />

      <Show
        when={local.error}
        fallback={
          <Show when={local.helperText}>
            <small class="pl-0.5 text-gray-500 dark:text-gray-400">{local.helperText}</small>
          </Show>
        }
      >
        <div class="flex flex-col gap-0.5">
          <Show
            when={Array.isArray(local.error)}
            fallback={<small class="pl-0.5 text-danger">{local.error as string}</small>}
          >
            <For each={local.error as string[]}>{(err) => <small class="pl-0.5 text-danger">• {err}</small>}</For>
          </Show>
        </div>
      </Show>
    </div>
  );
};

export default FileInput;
