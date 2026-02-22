// Note: We copy the official implementation from
// `https://github.com/solidjs-community/solid-primitives/blob/main/packages/pagination/src/index.ts#L372`
// in order to fix some of the issues it has. See:
// `https://github.com/solidjs-community/solid-primitives/issues/795#issuecomment-2938828459`

import { tryOnCleanup } from "@solid-primitives/utils";
import {
  Accessor,
  batch,
  createComputed,
  createEffect,
  createResource,
  createSignal,
  onCleanup,
  Setter,
} from "solid-js";
import { PaginatedResource } from "@/app-context/pagination";

export function createInfiniteScroll<T>(fetcher: (page: number) => Promise<PaginatedResource<T>>): [
  pages: Accessor<T[]>,
  loaderElement: (el: Element) => void,
  containerElement: (el: Element) => void,
  options: {
    page: Accessor<number>;
    setPage: Setter<number>;
    setPages: Setter<T[]>;
    end: Accessor<boolean>;
    setEnd: Setter<boolean>;
  },
] {
  const [page, setPage] = createSignal(1);
  const [pages, setPages] = createSignal<T[]>([]);
  const [end, setEnd] = createSignal(false);

  const io = new IntersectionObserver((entries) => {
    if (entries.length === 0) return;
    const entry = entries[0];
    const nextPage = contents.latest?.pages.next?.pageNumber;
    if (entry.isIntersecting && !contents.loading && nextPage) {
      setPage(nextPage);
    }
    if (!nextPage) setEnd(true);
  });
  onCleanup(() => io.disconnect());

  const loaderElement = (el: Element) => {
    io.observe(el);
    tryOnCleanup(() => io.unobserve(el));
  };

  const lastLoaded = (el: Element) => {
    createEffect(() => {
      pages();
      el.scrollIntoView({ block: "end", behavior: "instant" });
    });
  };

  const [contents] = createResource(page, fetcher);

  createComputed(() => {
    const content = contents.latest;
    if (!content) return;
    batch(() => {
      setPages((p) => [...p, ...content.data]);
    });
  });

  return [
    pages,
    loaderElement,
    lastLoaded,
    {
      page,
      setPage,
      setPages,
      end,
      setEnd,
    },
  ];
}
