// import { createInfiniteScroll } from "@solid-primitives/pagination";
import {PaginatedResource} from "@/app-context/pagination.ts";
import {createInfiniteScroll} from "./createInfiniteScroll.ts";
import {Accessor, JSX, Show} from "solid-js";
import LoadingDots from "@/components/LoadingDots.tsx";

type InfiniteScrollProps<T> = {
    fetcher: (page: number) => Promise<PaginatedResource<T>>;
    children: (content: Accessor<T[]>) => JSX.Element;
};

export default function InfiniteScroll<T>(props: InfiniteScrollProps<T>) {
    const [content, loaderElement, {end}] = createInfiniteScroll(props.fetcher);

    return (
        <>
            {props.children(content)}
            <Show when={!end()}>
                <LoadingDots ref={loaderElement}/>
            </Show>
        </>
    );
}
