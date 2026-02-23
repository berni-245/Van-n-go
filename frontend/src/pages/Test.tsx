import {createComputed, createResource, Suspense} from "solid-js";

const sleep = (ms: number) => new Promise((r) => setTimeout(r, ms));

export default function Test() {
    let i = 3;

    const [v, {refetch: refetchV}] = createResource(async () => {
        await sleep(1000);
        return i++;
    });

    createComputed(() => {
        console.log(v.latest);
    });

    return (
        <div class="flex-col">
            <div>
                Test val: <Suspense fallback="Loading...">{v()}</Suspense>
            </div>
            <button class="mt-4 bg-on-surface" onClick={() => refetchV()}>
                Refetch
            </button>
        </div>
    );
}
