import { For, JSX } from "solid-js";
import { PaginatedResource } from "@/app-context/pagination";
import { createAsync, useSearchParams } from "@solidjs/router";

interface PaginationProps<T> {
  fetcher: (page: number) => Promise<PaginatedResource<T>>;
  children: (content: T[] | undefined) => JSX.Element;
}

export default function Pagination<T>(props: PaginationProps<T>) {
  const [params, setParams] = useSearchParams();
  const page = () => Number(params.page || "1");

  const contents = createAsync(() => props.fetcher(page()));

  const data = () => contents.latest?.data;
  const pageInfo = () => contents.latest?.pages;
  const firstPage = () => pageInfo()?.first.pageNumber || page();
  const lastPage = () => pageInfo()?.last.pageNumber || page();
  const nextPage = () => pageInfo()?.next?.pageNumber || page();
  const prevPage = () => pageInfo()?.prev?.pageNumber || page();
  const pages = () => pagesArray(page(), firstPage(), lastPage());

  // TODO: localization
  return (
    <div class="flex flex-col flex-1 min-h-0">
      <div class="flex-1">{props.children(data())}</div>
      <nav aria-label="pagination" class="mt-6 flex justify-center">
        <ul class="flex shrink-0 items-center gap-2 text-sm font-medium">
          <li>
            <button
              onClick={() => setParams({ page: firstPage() })}
              class="flex items-center rounded-radius p-1 text-on-surface hover:text-primary dark:text-on-surface-dark dark:hover:text-primary-dark"
              aria-label="previous page"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 20 20"
                fill="currentColor"
                aria-hidden="true"
                class="size-6"
              >
                <path d="M4.75 4.5a.75.75 0 0 1 .75.75v9.5a.75.75 0 0 1-1.5 0v-9.5a.75.75 0 0 1 .75-.75Z" />
                <path
                  fill-rule="evenodd"
                  d="M13.78 5.22a.75.75 0 0 1 0 1.06L10.06 10l3.72 3.72a.75.75 0 1 1-1.06 1.06l-4.25-4.25a.75.75 0 0 1 0-1.06l4.25-4.25a.75.75 0 0 1 1.06 0Z"
                  clip-rule="evenodd"
                />
              </svg>
            </button>
          </li>
          <li>
            <button
              onClick={() => setParams({ page: prevPage() })}
              class="flex items-center rounded-radius p-1 text-on-surface hover:text-primary dark:text-on-surface-dark dark:hover:text-primary-dark"
              aria-label="previous page"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 20 20"
                fill="currentColor"
                aria-hidden="true"
                class="size-6"
              >
                <path
                  fill-rule="evenodd"
                  d="M11.78 5.22a.75.75 0 0 1 0 1.06L8.06 10l3.72 3.72a.75.75 0 1 1-1.06 1.06l-4.25-4.25a.75.75 0 0 1 0-1.06l4.25-4.25a.75.75 0 0 1 1.06 0Z"
                  clip-rule="evenodd"
                />
              </svg>
            </button>
          </li>
          <For each={pages()}>
            {(pageNr) => (
              <li>
                <button
                  onClick={() => setParams({ page: pageNr })}
                  class="flex size-6 items-center justify-center rounded-radius p-1 text-on-surface hover:text-primary dark:text-on-surface-dark dark:hover:text-primary-dark"
                  classList={{
                    "bg-primary font-bold text-on-primary dark:bg-primary-dark dark:text-on-primary-dark":
                      pageNr === page(),
                  }}
                  aria-label="page 1"
                >
                  {pageNr}
                </button>
              </li>
            )}
          </For>
          <li>
            <button
              onClick={() => setParams({ page: nextPage() })}
              class="flex items-center rounded-radius p-1 text-on-surface hover:text-primary dark:text-on-surface-dark dark:hover:text-primary-dark"
              aria-label="next page"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 20 20"
                fill="currentColor"
                aria-hidden="true"
                class="size-6"
              >
                <path
                  fill-rule="evenodd"
                  d="M8.22 5.22a.75.75 0 0 1 1.06 0l4.25 4.25a.75.75 0 0 1 0 1.06l-4.25 4.25a.75.75 0 0 1-1.06-1.06L11.94 10 8.22 6.28a.75.75 0 0 1 0-1.06Z"
                  clip-rule="evenodd"
                />
              </svg>
            </button>
          </li>
          <li>
            <button
              onClick={() => setParams({ page: lastPage() })}
              class="flex items-center rounded-radius p-1 text-on-surface hover:text-primary dark:text-on-surface-dark dark:hover:text-primary-dark"
              aria-label="next page"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 20 20"
                fill="currentColor"
                aria-hidden="true"
                class="size-6"
              >
                <path
                  fill-rule="evenodd"
                  d="M6.22 5.22a.75.75 0 0 1 1.06 0l4.25 4.25a.75.75 0 0 1 0 1.06l-4.25 4.25a.75.75 0 1 1-1.06-1.06L9.94 10 6.22 6.28a.75.75 0 0 1 0-1.06Z"
                  clip-rule="evenodd"
                />
                <path d="M15.25 4.5a.75.75 0 0 1 .75.75v9.5a.75.75 0 0 1-1.5 0v-9.5a.75.75 0 0 1 .75-.75Z" />
              </svg>
            </button>
          </li>
        </ul>
      </nav>
    </div>
  );
}

export const pagesArray = (currentPage: number, firstPage: number, lastPage: number) => {
  const maxVisible = 5;

  if (lastPage - firstPage + 1 <= maxVisible) {
    return Array.from({ length: lastPage - firstPage + 1 }, (_, i) => firstPage + i);
  }

  let start = currentPage - Math.floor(maxVisible / 2);
  let end = currentPage + Math.floor(maxVisible / 2);

  // Shift right if we're before the start boundary
  if (start < firstPage) {
    start = firstPage;
    end = firstPage + maxVisible - 1;
  }

  // Shift left if we're past the end boundary
  if (end > lastPage) {
    end = lastPage;
    start = lastPage - maxVisible + 1;
  }

  return Array.from({ length: maxVisible }, (_, i) => start + i);
};
