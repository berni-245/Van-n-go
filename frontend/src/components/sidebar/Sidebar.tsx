import { children, Component, createSignal, JSX, ParentComponent, Show } from "solid-js";
import { useTranslator } from "@/app-context/AppContext";
import { CollapsibleItem, Item } from "./Item";
import { A } from "@solidjs/router";

const SidebarRoot: ParentComponent<{ logo: JSX.Element }> = (props) => {
  const t = useTranslator();

  const resolvedChildren = children(() => props.children);
  const resolvedLogo = children(() => props.logo);

  const [showSidebar, setShowSidebar] = createSignal(false);

  return (
    <>
      {/* <!-- This allows screen readers to skip the sidebar and go directly to the main content. --> */}
      <a class="sr-only" href="#main-content">
        {t("global.sr.skipToMainContent")}
      </a>

      {/* <!-- dark overlay for when the sidebar is open on smaller screens  --> */}
      {showSidebar() && (
        <div
          class="fixed inset-0 z-10 bg-surface-dark/80 backdrop-blur-sm md:hidden"
          aria-hidden="true"
          onClick={() => setShowSidebar(false)}
        />
      )}

      <nav
        class="fixed items-center left-0 z-20 flex h-svh w-60 shrink-0 flex-col border-r border-outline bg-surface-alt p-4 transition-transform duration-300 md:w-64 md:translate-x-0 md:relative dark:border-outline-dark dark:bg-surface-dark-alt"
        classList={{
          "translate-x-0": showSidebar(),
          "-translate-x-60": !showSidebar(),
        }}
        aria-label="sidebar navigation"
      >
        {/* <!-- logo  --> */}
        <A href="/" class="ml-2 mb-12 w-fit text-2xl font-bold text-on-surface-strong dark:text-on-surface-dark-strong">
          {resolvedLogo()}
          <span class="sr-only">{t("global.home")}</span>
        </A>

        {/* <!-- sidebar links  --> */}
        <div class="w-full flex flex-col gap-2 overflow-y-auto pb-6">{resolvedChildren()}</div>
      </nav>

      {/* <!-- toggle button for small screen  --> */}
      <button
        class="fixed right-4 top-4 z-20 rounded-full bg-primary p-4 md:hidden text-on-primary dark:bg-primary-dark dark:text-on-primary-dark"
        onClick={() => setShowSidebar((showSidebar) => !showSidebar)}
      >
        <Show
          when={showSidebar()}
          fallback={
            <>
              <LilSidebarIcon />
              <span class="sr-only">{t("global.sr.openSidebar")}</span>
            </>
          }
        >
          <CrossIcon />
          <span class="sr-only">{t("global.sr.closeSidebar")}</span>
        </Show>
      </button>
    </>
  );
};

const CrossIcon: Component = () => {
  return (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="currentColor" class="size-5" aria-hidden="true">
      <path d="M2.146 2.854a.5.5 0 1 1 .708-.708L8 7.293l5.146-5.147a.5.5 0 0 1 .708.708L8.707 8l5.147 5.146a.5.5 0 0 1-.708.708L8 8.707l-5.146 5.147a.5.5 0 0 1-.708-.708L7.293 8z" />
    </svg>
  );
};

const LilSidebarIcon: Component = () => {
  return (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="currentColor" class="size-5" aria-hidden="true">
      <path d="M0 3a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2zm5-1v12h9a1 1 0 0 0 1-1V3a1 1 0 0 0-1-1zM4 2H2a1 1 0 0 0-1 1v10a1 1 0 0 0 1 1h2z" />
    </svg>
  );
};

export const Sidebar = Object.assign(SidebarRoot, {
  Item,
  CollapsibleItem,
});
