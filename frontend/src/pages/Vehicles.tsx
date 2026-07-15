import { For, Show, Suspense } from "solid-js";
import { query } from "@solidjs/router";
import { useApi, useAppState, useTranslator } from "@/app-context/AppContext";
import VehicleCard from "@components/cards/VehicleCard";
import VehicleGridSkeleton from "@/components/skeletons/VehicleGridSkeleton";
import { Title } from "@solidjs/meta";
import Pagination from "@components/pagination/Pagination";

const vehiclesQuery = query((page: number) => {
  const api = useApi();
  const [appState] = useAppState();

  return api.getVehicles(appState.user!.id, page);
}, "vehicles");

export default function Vehicles() {
  const t = useTranslator();

  return (
    <>
      <Title>Vehicles - Van N' Go</Title>
      <section class="mx-auto flex w-full max-w-7xl flex-col min-h-full px-4 py-8">
        <div class="mb-6 flex items-center justify-between">
          <h2 class="text-2xl font-bold text-on-surface-strong dark:text-on-surface-dark-strong">
            {t("driver.vehicles.title" as any)}
          </h2>
        </div>

        <Suspense fallback={<VehicleGridSkeleton />}>
          <Pagination fetcher={vehiclesQuery}>
            {(vehicles) => (
              <Show
                when={vehicles && vehicles.length > 0}
                fallback={
                  <div class="flex flex-col items-center justify-center gap-3 py-24 text-center">
                    <span class="text-5xl">🚐</span>
                    <p class="text-lg font-semibold text-on-surface-strong dark:text-on-surface-dark-strong">
                      {t("driver.vehicles.empty.title" as any)}
                    </p>
                    <p class="max-w-sm text-sm text-on-surface dark:text-on-surface-dark">
                      {t("driver.vehicles.empty.subtitle" as any)}
                    </p>
                  </div>
                }
              >
                <div class="grid gap-6 grid-cols-1 sm:grid-cols-2 xl:grid-cols-3">
                  <For each={vehicles}>{(v) => <VehicleCard vehicle={v} />}</For>
                </div>
              </Show>
            )}
          </Pagination>
        </Suspense>
      </section>
    </>
  );
}
