import { Component, createSignal, JSX, children, For, ParentComponent } from "solid-js";
import { Collapse } from "../transitions/Collapse";
import { HtmlIdFamily } from "../htmlIds";
import { A } from "@solidjs/router";

type BaseItemProps = {
  text: string;
  icon?: JSX.Element;
  onClick?: (event: Event) => void;
  selected?: boolean;
};

type ItemProps = BaseItemProps & {
  href: string;
};

export const Item: Component<ItemProps> = (props) => {
  const resolvedIcon = children(() => props.icon);

  return (
    <A
      href={props.href}
      class="w-full flex items-center rounded-radius gap-2 px-2 py-1.5 font-medium text-on-surface underline-offset-2 hover:bg-primary/5 hover:text-on-surface-strong focus-visible:underline focus:outline-hidden dark:text-on-surface-dark dark:hover:bg-primary-dark/5 dark:hover:text-on-surface-dark-strong"
      classList={{
        "text-on-surface-strong bg-primary/10 dark:text-on-surface-dark-strong dark:bg-primary-dark/10": props.selected,
      }}
      onClick={props.onClick}
    >
      {resolvedIcon()}
      <span>{props.text}</span>
    </A>
  );
};

type CollapsibleItemProps = BaseItemProps & {
  beginExpanded?: boolean;
};

export const CollapsibleItem: ParentComponent<CollapsibleItemProps> = (props) => {
  const resolvedChildren = children(() => props.children);
  const resolvedIcon = children(() => props.icon);

  const [expanded, setExpanded] = createSignal(!!props.beginExpanded);

  const itemId = new HtmlIdFamily("sidebar-item");

  return (
    <div class="flex flex-col">
      <button
        type="button"
        onClick={() => setExpanded(!expanded())}
        id={itemId.id("btn")}
        aria-controls={itemId.id("item-list")}
        aria-expanded={expanded() ? "true" : "false"}
        class="flex items-center justify-between rounded-radius gap-2 px-2 py-1.5 font-medium underline-offset-2 focus:outline-hidden focus-visible:underline text-on-surface hover:bg-primary/5 hover:text-on-surface-strong dark:text-on-surface-dark dark:hover:text-on-surface-dark-strong dark:hover:bg-primary-dark/5"
      >
        {resolvedIcon()}
        <span class="mr-auto text-left">{props.text}</span>
        <svg
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 20 20"
          fill="currentColor"
          class="size-5 transition-transform rotate-0 shrink-0"
          classList={{
            "rotate-180": expanded(),
            "rotate-0": !expanded(),
          }}
          aria-hidden="true"
        >
          <path
            fill-rule="evenodd"
            d="M5.22 8.22a.75.75 0 0 1 1.06 0L10 11.94l3.72-3.72a.75.75 0 1 1 1.06 1.06l-4.25 4.25a.75.75 0 0 1-1.06 0L5.22 9.28a.75.75 0 0 1 0-1.06Z"
            clip-rule="evenodd"
          />
        </svg>
      </button>

      {/* HACK: I'm passing the height dynamically calculated using child count but I should find a way to make */}
      {/* collapse work automatically... */}
      <Collapse expanded={expanded} height={resolvedChildren.toArray().length * 52}>
        <ul class="mt-1 overflow-hidden" aria-labelledby={itemId.id("btn")} id={itemId.id("item-list")}>
          <For each={resolvedChildren.toArray()}>
            {(item) => (
              <li class="w-full">
                <div class="flex ml-4 border-outline text-on-surface dark:border-outline-dark dark:text-on-surface-dark block border-l py-2.5 pl-4 font-medium capitalize transition duration-200 hover:border-l-2 hover:border-outline-strong hover:text-on-surface-strong dark:hover:border-x-outline-dark-strong dark:hover:text-on-surface-dark-strong">
                  {item}
                </div>
              </li>
            )}
          </For>
        </ul>
      </Collapse>
    </div>
  );
};
