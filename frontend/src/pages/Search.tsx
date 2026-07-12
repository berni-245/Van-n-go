import { createSignal, For, Show, Suspense } from "solid-js";
import { createAsync, useLocation, useSearchParams } from "@solidjs/router";
import { Title } from "@solidjs/meta";
import { stripBase } from "@/utils/routing";
import { useApi, useTranslator } from "@/app-context/AppContext";
import ZoneInput from "@/components/inputs/ZoneInput";
import Button from "@/components/Button";
import DriverCard from "@/components/cards/SearchCard";

const SIZES = ["SMALL", "MEDIUM", "LARGE"] as const;
const DAYS = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"] as const;
const SORT_ORDERS = ["ALPHABETICAL", "BEST_RATING", "LEAST_PRICE"] as const;

export default function Search() {
  const t = useTranslator();
  const api = useApi();
  const location = useLocation();

  const [searchParams, setSearchParams] = useSearchParams<{
    zoneQuery?: string;
    zoneId?: string;
    size?: string;
    maxPrice?: string;
    weekDay?: string;
    minRating?: string;
    sortOrder?: string;
    page?: string;
  }>();

  const [zoneQuery, setZoneQuery] = createSignal(searchParams.zoneQuery ?? "");
  const [zoneId, setZoneId] = createSignal<number | null>(searchParams.zoneId ? Number(searchParams.zoneId) : null);
  const [size, setSize] = createSignal(searchParams.size ?? "");
  const [maxPrice, setMaxPrice] = createSignal(searchParams.maxPrice ?? "");
  const [weekDay, setWeekDay] = createSignal(searchParams.weekDay ?? "");
  const [minRating, setMinRating] = createSignal(searchParams.minRating ?? "");
  const [sortOrder, setSortOrder] = createSignal(searchParams.sortOrder ?? "ALPHABETICAL");
  const [zoneError, setZoneError] = createSignal<string | null>(null);

  const page = () => (searchParams.page ? Number(searchParams.page) : 1);

  const results = createAsync(async () => {
    if (!searchParams.zoneId) return null;
    return api.searchDrivers({
      originZoneId: Number(searchParams.zoneId),
      size: searchParams.size || undefined,
      maxPrice: searchParams.maxPrice ? Number(searchParams.maxPrice) : undefined,
      weekDay: searchParams.weekDay || undefined,
      minRating: searchParams.minRating ? Number(searchParams.minRating) : undefined,
      sortOrder: searchParams.sortOrder || undefined,
      page: page(),
    });
  });

  const handleSubmit = (e: SubmitEvent) => {
    e.preventDefault();

    if (!zoneQuery().trim()) {
      setZoneError(t("global.error.validation.NotBlank") as string);
      return;
    }
    if (zoneId() === null) {
      setZoneError(t("global.error.validation.InvalidZone") as string);
      return;
    }

    setZoneError(null);
    setSearchParams({
      zoneQuery: zoneQuery(),
      zoneId: zoneId() !== null ? String(zoneId()) : undefined,
      size: size() || undefined,
      maxPrice: maxPrice() || undefined,
      weekDay: weekDay() || undefined,
      minRating: minRating() || undefined,
      sortOrder: sortOrder() || undefined,
      page: "1",
    });
  };

  const goToPage = (p: number) => {
    setSearchParams({ page: String(p) });
  };

  const currentSearchUrl = () => {
    return stripBase(location.pathname) + location.search;
  };

  return (
    <>
      <Title>{t("driver.search.title" as any)} - Van N' Go</Title>

      <section class="mx-auto w-full max-w-5xl px-4 py-8">
        {/* ── Search form ── */}
        <div class="mb-6 rounded-radius border border-outline bg-surface-alt px-4 py-3 shadow dark:border-outline-dark dark:bg-surface-dark-alt">
          <h1 class="mb-3 text-lg font-bold text-on-surface-strong dark:text-on-surface-dark-strong">
            {t("driver.search.title" as any)}
          </h1>

          <form onSubmit={handleSubmit} class="grid gap-2 sm:grid-cols-2 lg:grid-cols-3">
            {/* Zone */}
            <div class="sm:col-span-2 lg:col-span-3">
              <ZoneInput
                label={t("client.search.selectZone" as any)}
                value={zoneQuery()}
                onInput={setZoneQuery}
                onSelect={setZoneId}
                error={zoneError()}
              />
            </div>

            {/* Size */}
            <div>
              <label class="mb-0.5 block text-xs font-medium text-on-surface dark:text-on-surface-dark">
                {t("global.generic.word.size" as any)}
              </label>
              <select
                class="block w-full rounded-md border border-outline bg-surface px-2 py-1.5 text-sm focus:outline-none focus:ring dark:border-outline-dark dark:bg-surface-dark"
                value={size()}
                onInput={(e) => setSize(e.currentTarget.value)}
              >
                <option value="">{t("global.generic.word.any" as any)}</option>
                <For each={SIZES}>
                  {(s) => <option value={s}>{t(`global.generic.word.${s.toLowerCase()}` as any)}</option>}
                </For>
              </select>
            </div>

            {/* Weekday */}
            <div>
              <label class="mb-0.5 block text-xs font-medium text-on-surface dark:text-on-surface-dark">
                {t("global.generic.word.weekday" as any)}
              </label>
              <select
                class="block w-full rounded-md border border-outline bg-surface px-2 py-1.5 text-sm focus:outline-none focus:ring dark:border-outline-dark dark:bg-surface-dark"
                value={weekDay()}
                onInput={(e) => setWeekDay(e.currentTarget.value)}
              >
                <option value="">{t("global.generic.word.any" as any)}</option>
                <For each={DAYS}>{(d) => <option value={d}>{t(`day.${d.toLowerCase()}` as any)}</option>}</For>
              </select>
            </div>

            {/* Sort order */}
            <div>
              <label class="mb-0.5 block text-xs font-medium text-on-surface dark:text-on-surface-dark">
                {t("driver.search.sortOrder" as any)}
              </label>
              <select
                class="block w-full rounded-md border border-outline bg-surface px-2 py-1.5 text-sm focus:outline-none focus:ring dark:border-outline-dark dark:bg-surface-dark"
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
              <label class="mb-0.5 block text-xs font-medium text-on-surface dark:text-on-surface-dark">
                {t("driver.search.maxPrice" as any)}
              </label>
              <input
                type="number"
                min="1000"
                step="1000"
                placeholder="—"
                class="block w-full rounded-md border border-outline bg-surface px-2 py-1.5 text-sm focus:outline-none focus:ring dark:border-outline-dark dark:bg-surface-dark"
                value={maxPrice()}
                onInput={(e) => setMaxPrice(e.currentTarget.value)}
              />
            </div>

            {/* Min rating */}
            <div>
              <label class="mb-0.5 block text-xs font-medium text-on-surface dark:text-on-surface-dark">
                {t("driver.search.minRating" as any)}
              </label>
              <input
                type="number"
                min="1"
                max="5"
                step="1"
                placeholder="—"
                class="block w-full rounded-md border border-outline bg-surface px-2 py-1.5 text-sm focus:outline-none focus:ring dark:border-outline-dark dark:bg-surface-dark"
                value={minRating()}
                onInput={(e) => setMinRating(e.currentTarget.value)}
              />
            </div>

            {/* Submit */}
            <div class="flex items-end sm:col-span-2 lg:col-span-3">
              <Button
                type="submit"
                category="primary"
                text={t("global.components.availability.Search" as any) as string}
              />
            </div>
          </form>
        </div>

        {/* ── Results ── */}
        <Suspense
          fallback={
            <p class="text-center text-on-surface dark:text-on-surface-dark">{t("global.sr.loading" as any)}…</p>
          }
        >
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
                      {(driver) => (
                        <DriverCard driver={driver} from={encodeURIComponent(currentSearchUrl())} />
                      )}
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
