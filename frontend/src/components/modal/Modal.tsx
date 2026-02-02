import { createSignal, onCleanup, ParentComponent, Show } from "solid-js";
import { Portal } from "solid-js/web";
import { Transition } from "solid-transition-group";
import createFocusTrap from "solid-focus-trap";
import { HtmlIdFamily } from "@components/htmlIds";
import { ModalContext, ModalState, useModalContext } from "./Context";

const ModalRoot: ParentComponent<{ checkbox?: boolean }> = (props) => {
  const state: ModalState = { open: createSignal(false), htmlId: new HtmlIdFamily("modal") };

  return (
    <ModalContext.Provider value={state}>
      <div>{props.children}</div>
    </ModalContext.Provider>
  );
};

const OpenElement: ParentComponent = (props) => {
  const tableState = useModalContext();
  const [_, setIsOpen] = tableState.open;
  return <div onClick={() => setIsOpen(true)}>{props.children}</div>;
};

const Dialog: ParentComponent = (props) => {
  const tableState = useModalContext();
  const [isOpen, setIsOpen] = tableState.open;
  const [contentRef, setContentRef] = createSignal<HTMLElement | null>(null);

  createFocusTrap({
    element: contentRef,
    enabled: () => isOpen(),
  });

  const handleEscape = (e: KeyboardEvent) => {
    if (e.key === "Escape") setIsOpen(false);
  };
  window.addEventListener("keydown", handleEscape);
  onCleanup(() => window.removeEventListener("keydown", handleEscape));

  const closeIfClickOutside = (e: MouseEvent) => {
    if (e.target === contentRef()) setIsOpen(false);
  };

  return (
    <Portal>
      <Transition name="ease-out">
        <Show when={isOpen()}>
          <div
            ref={setContentRef}
            onClick={closeIfClickOutside}
            class="fixed inset-0 z-30 flex items-end justify-center bg-black/20 p-4 pb-8 backdrop-blur-md sm:items-center lg:p-8"
            role="dialog"
            aria-modal="true"
            aria-labelledby={tableState.htmlId.id("title")}
          >
            {/* <!-- Modal Dialog --> */}
            <div class="flex max-w-lg flex-col gap-4 overflow-hidden rounded-radius border border-outline bg-surface text-on-surface dark:border-outline-dark dark:bg-surface-dark-alt dark:text-on-surface-dark">
              {props.children}
            </div>
          </div>
        </Show>
      </Transition>
    </Portal>
  );
};

const DialogHeader: ParentComponent = (props) => {
  const tableState = useModalContext();
  const [_, setIsOpen] = tableState.open;
  return (
    <div class="flex items-center justify-between border-b border-outline bg-surface-alt/60 p-4 dark:border-outline-dark dark:bg-surface-dark/20">
      <div id={tableState.htmlId.id("title")}>{props.children}</div>
      <button onClick={() => setIsOpen(false)} aria-label="close modal">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 24 24"
          aria-hidden="true"
          stroke="currentColor"
          fill="none"
          stroke-width="1.4"
          class="w-5 h-5"
        >
          <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
    </div>
  );
};

const DialogBody: ParentComponent = (props) => {
  return <div class="px-4 py-8">{props.children}</div>;
};

const DialogFooter: ParentComponent = (props) => {
  return (
    <div class="flex flex-col-reverse justify-between gap-2 border-t border-outline bg-surface-alt/60 p-4 dark:border-outline-dark dark:bg-surface-dark/20 sm:flex-row sm:items-center md:justify-end">
      {props.children}
    </div>
  );
};

const Dialog2 = Object.assign(Dialog, {
  Header: DialogHeader,
  Body: DialogBody,
  Footer: DialogFooter,
});

export const Modal = Object.assign(ModalRoot, {
  OpenElement: OpenElement,
  Dialog: Dialog2,
});
