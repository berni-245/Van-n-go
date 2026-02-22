import { createAsync, query, redirect, useParams } from "@solidjs/router";
import { Suspense, createMemo } from "solid-js";
import { useApi, useAppState } from "../app-context/AppContext";
import { useToast } from "solid-notifications";
import { Availability, Zone } from "../api/types";
import FileInputDragDrop from "../components/inputs/FileInputDragDrop";

// NOTE: the page is separated in so many sections because solid sucks and using a `createAsync`
// accessor inside a query (like for `availability` which needs `vehicle`), or inside a
// `createMemo`, causes the current component's suspense boundary to be ignored and falling
// through to the parent's.
// With the current implementation the suspense is placed on the parent section.

/* ------------------ Queries ------------------ */

const vehicleQuery = query((vehicleId: number) => {
  const [appState] = useAppState();
  const toaster = useToast();

  const vehicle = useApi().getVehicle(appState.user!.id, vehicleId);
  if (!vehicle) {
    // TODO: toast message localization.
    toaster.notify("Request vehicle does not exist.", { type: "error" });
    return redirect("/driver/vehicles");
  }
  return vehicle;
}, "vehicle");

type AvailabilityWithZones = Availability & {
  zoneIds: number[];
};

const availabilityQuery = query(async (url: string): Promise<AvailabilityWithZones | Response> => {
  const toaster = useToast();

  const availability = await useApi().getVehicleAvailability(url);

  if (!availability) {
    toaster.notify("Failed to get availability.", { type: "error" });
    return redirect("/driver/vehicles");
  }

  const zoneIds = availability.zones.flatMap((zUrl) => {
    const idStr = zUrl.split("/").pop();
    const id = Number(idStr);
    return Number.isInteger(id) ? [id] : [];
  });

  return {
    ...availability,
    zoneIds,
  };
}, "availability");

const zonesQuery = query(() => {
  return useApi().getZones();
}, "zones");

/* ------------------ Sections ------------------ */

function VehicleSection(props: { vehicleId: number }) {
  const vehicle = createAsync(() => vehicleQuery(props.vehicleId));
  const zones = createAsync(() => zonesQuery());

  return (
    <div>
      <div>
        <FileInputDragDrop label="Vehicle Image" fileTypes={["PNG", "JPEG"]} maxSize={500} maxSizeUnit={"KB"} />
        <h3>Vehicle</h3>
        <Suspense fallback="Loading vehicle...">
          <p>{JSON.stringify(vehicle.latest)}</p>
          {vehicle.latest && <AvailabilitySection url={vehicle.latest!.availability} zones={zones.latest} />}
        </Suspense>
      </div>
      <div>
        <h3>All zones</h3>
        <Suspense fallback="Loading zones...">{JSON.stringify(zones.latest)}</Suspense>
      </div>
    </div>
  );
}

function AvailabilitySection(props: { url: string; zones: Record<number, Zone> | undefined }) {
  const availability = createAsync(() => availabilityQuery(props.url));

  return (
    <div>
      <h3>Availability</h3>
      <Suspense fallback="Loading availability...">
        <p>{JSON.stringify(availability.latest)}</p>
        <VehicleZonesSection zoneIds={availability.latest?.zoneIds} zones={props.zones} />
      </Suspense>
    </div>
  );
}

function VehicleZonesSection(props: { zoneIds: number[] | undefined; zones: Record<number, Zone> | undefined }) {
  const vehicleZones = createMemo(() =>
    props.zoneIds?.flatMap((id) => {
      const zone = props.zones?.[id];
      return zone ? [zone] : [];
    })
  );

  return (
    <>
      <h3>Vehicle Zones</h3>
      <p>{JSON.stringify(vehicleZones())}</p>
    </>
  );
}

/* ------------------ Page ------------------ */

export default function VehicleEdit() {
  const params = useParams();
  const vehicleId = parseInt(params.vehicleId);

  return <VehicleSection vehicleId={vehicleId} />;
}
