import { For, Suspense } from "solid-js";
import { createAsync } from "@solidjs/router";
import { Driver } from "@/api/types";
import { useApi } from "../app-context/AppContext";

export default function DriverAvailability() {
  const drivers = createAsync(async () => {
    const api = useApi();
    const res = await api.authFetch("/drivers");
    return (await res.json()) as Driver[];
  });

  return (
    <div>
      <Suspense fallback="Loading...">
        <For each={drivers()}>{(driver) => <div>{driver.username}</div>}</For>
      </Suspense>
    </div>
  );
}
