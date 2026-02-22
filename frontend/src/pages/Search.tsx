import { createSignal, For, Show, Suspense } from "solid-js";
import { createAsync } from "@solidjs/router";
import { Title } from "@solidjs/meta";
import { useApi, useTranslator } from "@/app-context/AppContext";
import ZoneInput from "@/components/inputs/ZoneInput";
import Button from "@/components/Button";
import { Driver } from "@/api/types";
import { DriverSearchParams } from "@/app-context/api";
import ImageWithFallback from "@/components/ImageWithFallback";

const SIZES = ["SMALL", "MEDIUM", "LARGE"] as const;
const DAYS  = ["MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY","SUNDAY"] as const;
const SORT_ORDERS = ["ALPHABETICAL", "RATING", "PRICE"] as const;

export default function Search() {
    const t   = useTranslator();
    const api = useApi();

    // form state
    const [zoneQuery,  setZoneQuery]  = createSignal("");
    const [zoneId,     setZoneId]     = createSignal<number | null>(null);
    const [size,       setSize]       = createSignal("");
    const [maxPrice,   setMaxPrice]   = createSignal("");
    const [weekDay,    setWeekDay]    = createSignal("");
    const [minRating,  setMinRating]  = createSignal("");
    const [sortOrder,  setSortOrder]  = createSignal("ALPHABETICAL");
    const [page,       setPage]       = createSignal(1);

    const [submitted, setSubmitted] = createSignal<DriverSearchParams | null>(null);

    const results = createAsync(async () => {
        const params = submitted();
        if (!params) return null;
        return api.searchDrivers(params);
    });

    const handleSubmit = (e: SubmitEvent) => {
        e.preventDefault();
        setPage(1);
        setSubmitted(buildParams(1));
    };

    const buildParams = (p: number): DriverSearchParams => ({
        originZoneId: zoneId(),
        size:         size()      || undefined,
        maxPrice:     maxPrice()  ? Number(maxPrice())  : undefined,
        weekDay:      weekDay()   || undefined,
        minRating:    minRating() ? Number(minRating()) : undefined,
        sortOrder:    sortOrder() || undefined,
        page:         p,
    });

    const goToPage = (p: number) => {
        setPage(p);
        setSubmitted(buildParams(p));
    };

    return (
        <>
            <Title>{t("driver.search.title" as any)} - Van N' Go</Title>

            <section class="mx-auto w-full max-w-5xl px-4 py-8">

                {/* ── Search form ── */}
                <div class="mb-8 rounded-radius border border-outline bg-surface-alt p-6 shadow dark:border-outline-dark dark:bg-surface-dark-alt">
                    <h1 class="mb-6 text-2xl font-bold text-on-surface-strong dark:text-on-surface-dark-strong">
                        {t("driver.search.title" as any)}
                    </h1>

                    <form onSubmit={handleSubmit} class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">

                        {/* Zone */}
                        <div class="sm:col-span-2 lg:col-span-3">
                            <ZoneInput
                                label={t("client.search.selectZone" as any)}
                                value={zoneQuery()}
                                onInput={setZoneQuery}
                                onSelect={setZoneId}
                                error={
                                    zoneQuery().trim() !== "" && zoneId() === null
                                        ? (t("client.search.zoneInvalid" as any) as string)
                                        : null
                                }
                            />
                        </div>

                        {/* Size */}
                        <div>
                            <label class="mb-1 block text-sm font-medium text-on-surface dark:text-on-surface-dark">
                                {t("generic.word.size" as any)}
                            </label>
                            <select
                                class="block w-full rounded-md border border-outline bg-surface px-3 py-2 text-sm focus:outline-none focus:ring dark:border-outline-dark dark:bg-surface-dark"
                                value={size()}
                                onInput={(e) => setSize(e.currentTarget.value)}
                            >
                                <option value="">{t("generic.word.any" as any)}</option>
                                <For each={SIZES}>
                                    {(s) => <option value={s}>{t(`generic.word.${s.toLowerCase()}` as any)}</option>}
                                </For>
                            </select>
                        </div>

                        {/* Weekday */}
                        <div>
                            <label class="mb-1 block text-sm font-medium text-on-surface dark:text-on-surface-dark">
                                {t("generic.word.weekday" as any)}
                            </label>
                            <select
                                class="block w-full rounded-md border border-outline bg-surface px-3 py-2 text-sm focus:outline-none focus:ring dark:border-outline-dark dark:bg-surface-dark"
                                value={weekDay()}
                                onInput={(e) => setWeekDay(e.currentTarget.value)}
                            >
                                <option value="">{t("generic.word.any" as any)}</option>
                                <For each={DAYS}>
                                    {(d) => <option value={d}>{t(`day.${d.toLowerCase()}` as any)}</option>}
                                </For>
                            </select>
                        </div>

                        {/* Sort order */}
                        <div>
                            <label class="mb-1 block text-sm font-medium text-on-surface dark:text-on-surface-dark">
                                {t("driver.search.sortOrder" as any)}
                            </label>
                            <select
                                class="block w-full rounded-md border border-outline bg-surface px-3 py-2 text-sm focus:outline-none focus:ring dark:border-outline-dark dark:bg-surface-dark"
                                value={sortOrder()}
                                onInput={(e) => setSortOrder(e.currentTarget.value)}
                            >
                                <For each={SORT_ORDERS}>
                                    {(o) => <option value={o}>{t(`driver.search.order.${o.toLowerCase()}` as any)}</option>}
                                </For>
                            </select>
                        </div>

                        {/* Max price */}
                        <div>
                            <label class="mb-1 block text-sm font-medium text-on-surface dark:text-on-surface-dark">
                                {t("driver.search.maxPrice" as any)}
                            </label>
                            <input
                                type="number"
                                min="0"
                                step="100"
                                placeholder="—"
                                class="block w-full rounded-md border border-outline bg-surface px-3 py-2 text-sm focus:outline-none focus:ring dark:border-outline-dark dark:bg-surface-dark"
                                value={maxPrice()}
                                onInput={(e) => setMaxPrice(e.currentTarget.value)}
                            />
                        </div>

                        {/* Min rating */}
                        <div>
                            <label class="mb-1 block text-sm font-medium text-on-surface dark:text-on-surface-dark">
                                {t("driver.search.minRating" as any)}
                            </label>
                            <input
                                type="number"
                                min="1"
                                max="5"
                                step="1"
                                placeholder="—"
                                class="block w-full rounded-md border border-outline bg-surface px-3 py-2 text-sm focus:outline-none focus:ring dark:border-outline-dark dark:bg-surface-dark"
                                value={minRating()}
                                onInput={(e) => setMinRating(e.currentTarget.value)}
                            />
                        </div>

                        {/* Submit */}
                        <div class="flex items-end sm:col-span-2 lg:col-span-3">
                            <Button type="submit" category="primary" text={t("components.availability.Search" as any) as string} />
                        </div>
                    </form>
                </div>

                {/* ── Results ── */}
                <Suspense fallback={<p class="text-center text-on-surface dark:text-on-surface-dark">Loading…</p>}>
                    <Show when={results()}>
                        {(data) => (
                            <>
                                <Show
                                    when={data().drivers.length > 0}
                                    fallback={
                                        <p class="text-center text-on-surface dark:text-on-surface-dark">
                                            {t("driver.search.noResults" as any)}
                                        </p>
                                    }
                                >
                                    <ul class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
                                        <For each={data().drivers}>
                                            {(driver) => <DriverCard driver={driver} />}
                                        </For>
                                    </ul>

                                    {/* ── Pagination ── */}
                                    <Show when={data().maxPages > 1}>
                                        <div class="mt-8 flex items-center justify-center gap-2">
                                            <Button
                                                category="secondary"
                                                text="←"
                                                disabled={page() <= 1}
                                                onClick={() => goToPage(page() - 1)}
                                            />
                                            <span class="text-sm text-on-surface dark:text-on-surface-dark">
                        {page()} / {data().maxPages}
                      </span>
                                            <Button
                                                category="secondary"
                                                text="→"
                                                disabled={page() >= data().maxPages}
                                                onClick={() => goToPage(page() + 1)}
                                            />
                                        </div>
                                    </Show>
                                </Show>
                            </>
                        )}
                    </Show>
                </Suspense>

            </section>
        </>
    );
}

// ---- Driver result card ------------------------------------------------

function DriverCard(props: { driver: Driver }) {
    const { driver } = props;
    return (
        <li class="flex flex-col overflow-hidden rounded-radius border border-outline bg-surface-alt shadow-sm transition hover:shadow-md dark:border-outline-dark dark:bg-surface-dark-alt">
            <div class="flex items-center gap-4 p-4">
                <div class="h-14 w-14 shrink-0 overflow-hidden rounded-full border border-outline dark:border-outline-dark">
                    <ImageWithFallback
                        src={driver.profilePic}
                        fallbackSrc="/images/defaultUserPfp.png"
                        skipSrc={!driver.hasProfilePic}
                        class="h-full w-full object-cover"
                        alt={driver.username}
                    />
                </div>
                <div class="min-w-0">
                    <p class="truncate font-semibold text-on-surface-strong dark:text-on-surface-dark-strong">
                        {driver.username}
                    </p>
                    <Show when={driver.rating != null}>
                        <p class="text-sm text-on-surface dark:text-on-surface-dark">
                            {"★".repeat(driver.rating!)} {"☆".repeat(5 - driver.rating!)}
                        </p>
                    </Show>
                </div>
            </div>
            <Show when={driver.description}>
                <p class="border-t border-outline px-4 py-3 text-sm text-on-surface dark:border-outline-dark dark:text-on-surface-dark line-clamp-3">
                    {driver.description}
                </p>
            </Show>
        </li>
    );
}