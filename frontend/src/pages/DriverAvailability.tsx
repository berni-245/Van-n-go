import { For, Suspense } from "solid-js";
import { createAsync } from "@solidjs/router";

interface Driver {
  id: number;
  name: string;
}

export default function DriverAvailability() {
  const drivers = createAsync(async () => {
    const res = await fetch("/api/drivers");
    return (await res.json()) as Driver[];
  });

  return (
    <div>
      <Suspense fallback="Loading...">
        <For each={drivers()}>{(driver) => <div>{driver.name}</div>}</For>
      </Suspense>
    </div>
  );
}
