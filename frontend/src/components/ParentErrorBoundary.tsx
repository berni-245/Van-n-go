import { ErrorBoundary, For, ParentComponent } from "solid-js";
import Button from "./Button";

const ParentErrorBoundary: ParentComponent = (props) => {
  return (
    <ErrorBoundary
      fallback={(error, reset) => (
        <div class="m-4">
          <h2>Something went wrong</h2>
          <p>{error.message}</p>
          <ul class="mt-4 mb-4 rounded-xl bg-zinc-950/90 p-4 text-sm font-mono text-zinc-200 shadow-lg ring-1 ring-white/10">
            <For each={error.stack?.split("\n")}>
              {(frame, i) => (
                <li class="group flex gap-3 py-1 leading-relaxed transition-colors hover:bg-white/5">
                  <span class="w-6 shrink-0 text-right text-zinc-500 select-none">{i() + 1}</span>

                  <span class={i() === 0 ? "font-semibold text-red-400" : "text-zinc-300 group-hover:text-zinc-100"}>
                    {frame.trim()}
                  </span>
                </li>
              )}
            </For>
          </ul>
          <Button text="Try Again" onClick={reset} category="secondary" />
        </div>
      )}
    >
      {props.children}
    </ErrorBoundary>
  );
};

export default ParentErrorBoundary;
