import { For, Suspense } from "solid-js";
import { createAsync, query } from "@solidjs/router";
import { useApi, useAppState } from "@/app-context/AppContext";
import Card from "@components/cards/VehicleCard";
import VehicleGridSkeleton from "../components/skeletons/VehicleGridSkeleton";
import Pagination from "../components/Pagination";
import { Title } from "@solidjs/meta";

const vehiclesQuery = query(async () => {
  const api = useApi();
  const [appState] = useAppState();

  // TODO: should probably be handled by rout guard.
  // if (!appState.user) return redirect(`/login`);

  return api.getVehicles(appState.user!.id);
}, "vehicles");

// const zonesQuery = query(async () => {
//   return useApi().getZones();
// }, "vehicles");

export default function Vehicles() {
  const vehicles = createAsync(() => {
    return vehiclesQuery();
  });

  return (
    <>
      <Title>Vehicles - Van N' Go</Title>
      <section class="mx-auto w-full max-w-7xl px-4 py-8">
        <div class="mb-6 flex items-center justify-between">
          <h2 class="text-2xl font-bold text-on-surface-strong dark:text-on-surface-dark-strong">My vehicles</h2>

          {/* Placeholder for future pagination controls */}
          <Pagination totalPages={10} currentPage={2} baseHref="http://localhost:8080/api/driver/vehicles" />
        </div>

        <Suspense fallback={<VehicleGridSkeleton />}>
          {/* <Suspense fallback="Loading..."> */}
          <div class="grid gap-6 grid-cols-1 sm:grid-cols-2 xl:grid-cols-3">
            <For each={vehicles()}>{(vehicle) => <Card vehicle={vehicle} />}</For>
          </div>
        </Suspense>
      </section>
    </>
  );
}
