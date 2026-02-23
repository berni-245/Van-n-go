import {Component, For} from "solid-js";
import {useTranslator} from "@/app-context/AppContext";
import {createInfiniteScroll} from "@solid-primitives/pagination";

type PaginationProps = {
    totalPages: number;
    currentPage: number;
    baseHref: string;
};

// TODO: use https://primitives.solidjs.community/package/pagination/
const Pagination: Component<PaginationProps> = (props) => {
    const t = useTranslator();
    //
    // const pageHref = (page: number) => {
    //   const url = new URL(props.baseHref);
    //   url.searchParams.set("page", page.toString());
    //   return url.toString();
    // };
    //
    // const [pages, setEl, { end }] = createInfiniteScroll(fetcher);

    return (
        <div>
            {/* <For each={pages()}>{(item) => <h4>{item}</h4>}</For> */}
            {/* <Show when={!end()}> */}
            {/*   <h1 ref={setEl}>Loading...</h1> */}
            {/* </Show> */}
        </div>
    );

    return (
        <nav aria-label={t("global.sr.pagination")}>
            <ul class="flex shrink-0 items-center gap-2 text-sm font-medium">
                <li>
                    <a
                        href="#"
                        class="flex items-center rounded-radius p-1 text-on-surface hover:text-primary dark:text-on-surface-dark dark:hover:text-primary-dark"
                        aria-label={t("global.sr.previousPage")}
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
                        Previous
                    </a>
                </li>

                <For each={Array.from({length: props.totalPages}, (_, index) => index + 1)}>
                    {(page) => (
                        <li>
                            <a
                                href={pageHref(page)}
                                class="flex size-6 items-center justify-center rounded-radius p-1 text-on-surface hover:text-primary dark:text-on-surface-dark dark:hover:text-primary-dark"
                                aria-label={t("global.sr.page", {number: page})}
                            >
                                {page}
                            </a>
                        </li>
                    )}
                </For>

                {/* <li> */}
                {/*   <a */}
                {/*     href="#" */}
                {/*     class="flex size-6 items-center justify-center rounded-radius p-1 text-on-surface hover:text-primary dark:text-on-surface-dark dark:hover:text-primary-dark" */}
                {/*     aria-label="more pages" */}
                {/*   > */}
                {/*     <svg */}
                {/*       xmlns="http://www.w3.org/2000/svg" */}
                {/*       fill="none" */}
                {/*       viewBox="0 0 24 24" */}
                {/*       stroke-width="1.5" */}
                {/*       aria-hidden="true" */}
                {/*       stroke="currentColor" */}
                {/*       class="size-6" */}
                {/*     > */}
                {/*       <path */}
                {/*         stroke-linecap="round" */}
                {/*         stroke-linejoin="round" */}
                {/*         d="M6.75 12a.75.75 0 1 1-1.5 0 .75.75 0 0 1 1.5 0ZM12.75 12a.75.75 0 1 1-1.5 0 .75.75 0 0 1 1.5 0ZM18.75 12a.75.75 0 1 1-1.5 0 .75.75 0 0 1 1.5 0Z" */}
                {/*       /> */}
                {/*     </svg> */}
                {/*   </a> */}
                {/* </li> */}
                {/* <li> */}
                {/*   <a */}
                {/*     href="#" */}
                {/*     class="flex size-6 items-center justify-center rounded-radius p-1 text-on-surface hover:text-primary dark:text-on-surface-dark dark:hover:text-primary-dark" */}
                {/*     aria-label="page 30" */}
                {/*   > */}
                {/*     30 */}
                {/*   </a> */}
                {/* </li> */}

                <li>
                    <a
                        href="#"
                        class="flex items-center rounded-radius p-1 text-on-surface hover:text-primary dark:text-on-surface-dark dark:hover:text-primary-dark"
                        aria-label={t("global.sr.nextPage")}
                    >
                        Next
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
                    </a>
                </li>
            </ul>
        </nav>
    );
};

export default Pagination;
