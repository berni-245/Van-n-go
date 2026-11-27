import { Component, createSignal, For, JSX, Show, splitProps } from "solid-js";
import { HtmlIdFamily } from "@/components/htmlIds";

type InputProps = {
  label?: string;
  error?: string | string[];
} & JSX.InputHTMLAttributes<HTMLInputElement>;

const PasswordInput: Component<InputProps> = (props) => {
  const [showPassword, setShowPassword] = createSignal(false);

  const inputId = new HtmlIdFamily("input");

  const [local, filteredProps] = splitProps(props, ["label", "class", "error"]);

  return (
    <div class={"flex w-full max-w-xs flex-col gap-1 text-on-surface dark:text-on-surface-dark " + (local.class || "")}>
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
      <div class="relative">
        <input
          type={showPassword() ? "text" : "password"}
          id={inputId.id()}
          class="w-full rounded-radius border bg-surface-alt px-2 py-2 text-sm focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary disabled:cursor-not-allowed disabled:opacity-75 dark:bg-surface-dark-alt/50 dark:focus-visible:outline-primary-dark"
          classList={{
            "border-danger": !!local.error,
            "border-outline dark:border-outline-dark": !local.error,
          }}
          name="password"
          autocomplete="current-password"
          {...filteredProps}
        />
        <button
          type="button"
          onClick={() => setShowPassword((showPass) => !showPass)}
          class="absolute right-2.5 top-1/2 -translate-y-1/2 text-on-surface dark:text-on-surface-dark"
          aria-label="Show password"
        >
          <Show when={!showPassword()}>
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke-width="1.5"
              stroke="currentColor"
              aria-hidden="true"
              class="size-5"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M2.036 12.322a1.012 1.012 0 0 1 0-.639C3.423 7.51 7.36 4.5 12 4.5c4.638 0 8.573 3.007 9.963 7.178.07.207.07.431 0 .639C20.577 16.49 16.64 19.5 12 19.5c-4.638 0-8.573-3.007-9.963-7.178Z"
              />
              <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z" />
            </svg>
          </Show>
          <Show when={showPassword()}>
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke-width="1.5"
              stroke="currentColor"
              aria-hidden="true"
              class="size-5"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M3.98 8.223A10.477 10.477 0 0 0 1.934 12C3.226 16.338 7.244 19.5 12 19.5c.993 0 1.953-.138 2.863-.395M6.228 6.228A10.451 10.451 0 0 1 12 4.5c4.756 0 8.773 3.162 10.065 7.498a10.522 10.522 0 0 1-4.293 5.774M6.228 6.228 3 3m3.228 3.228 3.65 3.65m7.894 7.894L21 21m-3.228-3.228-3.65-3.65m0 0a3 3 0 1 0-4.243-4.243m4.242 4.242L9.88 9.88"
              />
            </svg>
          </Show>
        </button>
      </div>

      <Show when={local.error}>
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

export default PasswordInput;
