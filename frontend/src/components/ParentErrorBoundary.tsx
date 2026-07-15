import { ErrorBoundary, For, ParentComponent } from "solid-js";
import { FaSolidTriangleExclamation } from "solid-icons/fa";
import Button from "./Button";

const ParentErrorBoundary: ParentComponent = (props) => {
  return (
    <ErrorBoundary
      fallback={(error, reset) => (
        <div class="flex min-h-svh items-center justify-center bg-surface p-4 dark:bg-surface-dark">
          <div class="w-full max-w-lg rounded-2xl border border-outline bg-surface-alt p-8 shadow-xl dark:border-outline-dark dark:bg-surface-dark-alt">
            <div class="flex items-center gap-3">
              <span class="flex size-10 shrink-0 items-center justify-center rounded-full bg-danger/10 text-danger dark:bg-red-400/10 dark:text-red-400">
                <FaSolidTriangleExclamation size={18} />
              </span>
              <h1 class="text-xl font-bold tracking-tight text-on-surface dark:text-on-surface-dark">
                Something went wrong
              </h1>
            </div>

            <p class="mt-3 text-sm text-on-surface/70 dark:text-on-surface-dark/70">{error.message}</p>

            <details class="mt-4 group">
              <summary class="cursor-pointer text-xs font-medium uppercase tracking-wide text-on-surface/50 hover:text-on-surface/80 dark:text-on-surface-dark/50 dark:hover:text-on-surface-dark/80">
                Show details
              </summary>
              <ul class="mt-2 max-h-64 overflow-auto rounded-radius border border-outline/50 bg-zinc-950/90 p-4 text-sm font-mono text-zinc-200 shadow-inner dark:border-outline-dark/50">
                <For each={error.stack?.split("\n")}>
                  {(frame, i) => (
                    <li class="group/frame flex gap-3 py-1 leading-relaxed transition-colors hover:bg-white/5">
                      <span class="w-6 shrink-0 text-right text-zinc-500 select-none">{i() + 1}</span>

                      <span
                        class={
                          i() === 0 ? "font-semibold text-red-400" : "text-zinc-300 group-hover/frame:text-zinc-100"
                        }
                      >
                        {frame.trim()}
                      </span>
                    </li>
                  )}
                </For>
              </ul>
            </details>

            <Button text="Try Again" onClick={reset} category="primary" class="mt-6 w-full" />
          </div>
        </div>
      )}
    >
      {props.children}
    </ErrorBoundary>
  );
};

export default ParentErrorBoundary;
