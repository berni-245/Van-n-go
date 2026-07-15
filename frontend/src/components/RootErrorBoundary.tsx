import { ErrorBoundary, ParentComponent } from "solid-js";
import { ROUTER_BASE } from "@/utils/routing";

// Last-resort boundary wrapping MetaProvider/Router/AppContextProvider themselves, so it
// must not depend on any of them (translator, routing, etc.) - only ParentErrorBoundary,
// nested inside AppContextProvider, can afford that.
const RootErrorBoundary: ParentComponent = (props) => {
  return (
    <ErrorBoundary
      fallback={() => (
        <div class="flex min-h-svh items-center justify-center bg-surface p-4 dark:bg-surface-dark">
          <div class="w-full max-w-lg rounded-2xl border border-outline bg-surface-alt p-8 text-center shadow-xl dark:border-outline-dark dark:bg-surface-dark-alt">
            <h1 class="text-2xl font-bold text-on-surface dark:text-on-surface-dark">Oops.</h1>
            <a
              href={`${ROUTER_BASE}/`}
              class="mt-6 inline-flex w-full items-center justify-center rounded-radius border border-primary bg-primary px-4 py-2 font-medium text-on-primary transition hover:opacity-75 dark:border-primary-dark dark:bg-primary-dark dark:text-on-primary-dark"
            >
              Home
            </a>
          </div>
        </div>
      )}
    >
      {props.children}
    </ErrorBoundary>
  );
};

export default RootErrorBoundary;
