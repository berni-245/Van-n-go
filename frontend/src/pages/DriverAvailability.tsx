import { For, Suspense } from "solid-js";
import { createAsync } from "@solidjs/router";
import { addBasicAuth } from "../api/authorization";
import { Driver } from "../api/types";

export default function DriverAvailability() {
  const drivers = createAsync(async () => {
    const request = new Request("/api/drivers");
    addBasicAuth({ request, username: "driver1", password: "driver1Pass" });
    const res = await fetch(request);
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
