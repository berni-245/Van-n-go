import { For, Suspense } from "solid-js";
import { query } from "@solidjs/router";
import { useApi, useAppState } from "@/app-context/AppContext";
import VehicleCard from "@components/cards/VehicleCard";
import VehicleGridSkeleton from "@/components/skeletons/VehicleGridSkeleton";
import { Title } from "@solidjs/meta";
import InfiniteScroll from "../components/pagination/InfiniteScroll";

const vehiclesQuery = query((page: number) => {
  const api = useApi();
  const [appState] = useAppState();

  return api.getVehicles(appState.user!.id, page);
}, "vehicles");

export default function Vehicles() {
  return (
    <>
      <Title>Vehicles - Van N' Go</Title>
      <section class="mx-auto w-full max-w-7xl px-4 py-8">
        <div class="mb-6 flex items-center justify-between">
          <h2 class="text-2xl font-bold text-on-surface-strong dark:text-on-surface-dark-strong">My vehicles</h2>
        </div>

        <Suspense fallback={<VehicleGridSkeleton />}>
          <InfiniteScroll fetcher={vehiclesQuery}>
            {(vehicles) => (
              <div class="grid gap-6 grid-cols-1 sm:grid-cols-2 xl:grid-cols-3">
                <For each={vehicles()}>{(v) => <VehicleCard vehicle={v} />}</For>
              </div>
            )}
          </InfiniteScroll>
        </Suspense>
      </section>
    </>
  );
}
