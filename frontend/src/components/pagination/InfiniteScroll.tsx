// import { createInfiniteScroll } from "@solid-primitives/pagination";
import { PaginatedResource } from "~/src/app-context/pagination.ts";
import { createInfiniteScroll } from "./createInfiniteScroll.ts";
import { Accessor, JSX } from "solid-js";

type InfiniteScrollProps<T> = {
  fetcher: (page: number) => Promise<PaginatedResource<T>>;
  children: (args: {
    content: Accessor<T[]>;
    loaderElement: (el: Element) => void;
    containerElement: (el: Element) => void;
    end: Accessor<boolean>;
  }) => JSX.Element;
};

export default function InfiniteScroll<T>(props: InfiniteScrollProps<T>) {
  const [content, loaderElement, containerElement, { end }] = createInfiniteScroll(props.fetcher);

  return props.children({
    content,
    loaderElement,
    containerElement,
    end,
  });
}
