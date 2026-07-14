import { action, createAsync, query, redirect, useParams, useSubmission } from "@solidjs/router";
import { createEffect, createMemo, createSignal, For, Show, Suspense } from "solid-js";
import { useApi, useAppState, useTranslator } from "../app-context/AppContext";
import { useToast } from "solid-notifications";
import { ApiError, Availability, UpdateVehicle, Zone } from "../api/types";
import { Api } from "../app-context/api";
import FileInputDragDrop from "../components/inputs/FileInputDragDrop";
import TextInput from "../components/inputs/TextInput";
import Button from "../components/Button";
import Combobox from "@components/inputs/Combobox.tsx";
import {
  isValidPlateNumber,
  isValidVehicleVolume,
  isValidHourlyRate,
  isValidVehicleDescriptionLength,
} from "@/utils/validation";

export const editVehicleAction = action(
  async (api: Api, driverId: number, vehicleId: number, t: any, toaster: any, formData: FormData) => {
    const fieldErrors: Record<string, string[]> = {};

    const plateNumber = formData.get("plateNumber") as string;
    const volumeM3Str = formData.get("volumeM3") as string;
    const hourlyRateStr = formData.get("hourlyRate") as string;
    const description = formData.get("description") as string;

    if (plateNumber && !isValidPlateNumber(plateNumber)) {
      fieldErrors["plateNumber"] = [t("global.page.editVehicle.errors.plateRegex")];
    }

    let volumeM3: number | undefined;
    if (volumeM3Str) {
      volumeM3 = parseFloat(volumeM3Str);
      if (!isValidVehicleVolume(volumeM3)) {
        fieldErrors["volumeM3"] = [t("global.page.editVehicle.errors.volume")];
      }
    }
    let hourlyRate: number | undefined;
    if (hourlyRateStr) {
      hourlyRate = parseFloat(hourlyRateStr);
      if (!isValidHourlyRate(hourlyRate)) {
        fieldErrors["hourlyRate"] = [t("global.page.editVehicle.errors.rate")];
      }
    }

    if (description && !isValidVehicleDescriptionLength(description)) {
      fieldErrors["description"] = [t("global.page.editVehicle.errors.descriptionLength")];
    }

    if (Object.keys(fieldErrors).length > 0) {
      return { ok: false, resp: fieldErrors };
    }

    const payload = {
      plateNumber: plateNumber || undefined,
      volumeM3,
      hourlyRate,
      description: description || undefined,
    };

    const response = await api.updateVehicle(driverId, vehicleId, payload as UpdateVehicle);

    if (!response.ok) {
      if (response.status === 400) {
        const errorList: ApiError[] = await response.json();
        const apiErrors: Record<string, string[]> = {};

        errorList.forEach((err) => {
          if (err.entity && err.description) {
            if (!apiErrors[err.entity]) apiErrors[err.entity] = [];
            apiErrors[err.entity].push(err.description);
          } else if (err.description) {
            toaster.notify(err.description, { type: "error" });
          }
        });
        return { ok: false, resp: apiErrors };
      }
      if (response.status === 403 || response.status === 404) {
        toaster.notify(t("global.page.editVehicle.errors.vehicleAccess"), { type: "error" });
        return { ok: false, resp: {} };
      }
      throw new Error(t("global.page.editVehicle.errors.update"));
    }

    toaster.notify(t("global.page.editVehicle.success.update"), { type: "success" });
    return { ok: true, resp: await response.json() };
  },
  "edit-vehicle-action"
);

const vehicleQuery = query((vehicleId: number) => {
  const [appState] = useAppState();
  const toaster = useToast();
  const t = useTranslator();

  const vehicle = useApi().getVehicle(appState.user!.id, vehicleId);
  if (!vehicle) {
    toaster.notify(t("global.page.editVehicle.errors.vehicleNotFound"), { type: "error" });
    return redirect("/driver/vehicles");
  }
  return vehicle;
}, "vehicle");

type AvailabilityWithZones = Availability & {
  zoneIds: number[];
};

const availabilityQuery = query(async (url: string): Promise<AvailabilityWithZones | Response> => {
  const toaster = useToast();
  const t = useTranslator();
  const availability = await useApi().getVehicleAvailability(url);

  if (!availability) {
    toaster.notify(t("global.page.editVehicle.errors.availabilityFetch"), { type: "error" });
    return redirect("/driver/vehicles");
  }

  const zoneIds = availability.zones.flatMap((zUrl) => {
    const idStr = zUrl.split("/").pop();
    const id = Number(idStr);
    return Number.isInteger(id) ? [id] : [];
  });

  return { ...availability, zoneIds };
}, "availability");

const zonesQuery = query(() => {
  return useApi().getZones();
}, "zones");

function VehicleSection(props: { vehicleId: number }) {
  const vehicle = createAsync(() => vehicleQuery(props.vehicleId));
  const zones = createAsync(() => zonesQuery());

  const [appState] = useAppState();
  const api = useApi();
  const t = useTranslator();
  const toaster = useToast();

  const submission = useSubmission(editVehicleAction);
  const errors = () => (submission.result?.ok === false ? submission.result.resp : {});

  return (
    <div class="mx-auto max-w-4xl p-4 md:p-8 space-y-8">
      <div class="mb-6">
        <h1 class="text-3xl font-bold text-on-surface dark:text-on-surface-dark">
          {t("global.page.editVehicle.title")}
        </h1>
        <p class="text-on-surface/70 dark:text-on-surface-dark/70">{t("global.page.editVehicle.subtitle")}</p>
      </div>

      <Suspense fallback={<div class="animate-pulse">{t("global.page.editVehicle.loading.vehicle")}</div>}>
        <Show when={vehicle.latest}>
          {(v) => (
            <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
              <div class="lg:col-span-2 space-y-6">
                <div class="rounded-2xl border border-outline bg-surface-alt p-6 shadow-sm dark:border-outline-dark dark:bg-surface-dark-alt">
                  <h3 class="mb-4 text-xl font-semibold text-on-surface dark:text-on-surface-dark">
                    {t("global.page.editVehicle.generalData")}
                  </h3>

                  <form
                    action={editVehicleAction.with(api, appState.user!.id, props.vehicleId, t, toaster)}
                    method="post"
                    class="space-y-4"
                  >
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <TextInput
                        name="plateNumber"
                        label={t("global.page.editVehicle.labels.plate")}
                        value={v().plateNumber}
                        error={errors()?.["plateNumber"]}
                        disabled={submission.pending}
                      />
                      <TextInput
                        name="hourlyRate"
                        type="number"
                        label={t("global.page.editVehicle.labels.rate")}
                        value={v().hourlyRate?.toString()}
                        error={errors()?.["hourlyRate"]}
                        disabled={submission.pending}
                        min="1000"
                        max="100000"
                        step="100"
                      />
                    </div>

                    <TextInput
                      name="volumeM3"
                      type="number"
                      label={t("global.page.editVehicle.labels.volume")}
                      value={v().volumeM3?.toString()}
                      error={errors()?.["volumeM3"]}
                      disabled={submission.pending}
                      min="1"
                      max="100"
                      step="1"
                    />

                    <div class="flex flex-col gap-1 text-on-surface dark:text-on-surface-dark">
                      <label class="w-fit pl-0.5 text-sm">{t("global.page.editVehicle.labels.description")}</label>
                      <textarea
                        name="description"
                        rows="3"
                        maxLength={255}
                        class="w-full resize-none rounded-radius border border-outline bg-surface px-4 py-2 text-sm focus-visible:outline-2 focus-visible:outline-primary dark:border-outline-dark dark:bg-surface-dark dark:focus-visible:outline-primary-dark"
                        classList={{ "border-danger dark:border-red-400": !!errors()?.["description"] }}
                        disabled={submission.pending}
                        value={v().description || ""}
                      />
                      <Show when={errors()?.["description"]}>
                        <small class="pl-0.5 text-danger dark:text-red-400">• {errors()?.["description"]?.[0]}</small>
                      </Show>
                    </div>

                    <div class="pt-4 flex justify-end">
                      <Button
                        type="submit"
                        category="primary"
                        text={
                          submission.pending
                            ? t("global.page.editVehicle.buttons.saving")
                            : t("global.page.editVehicle.buttons.saveChanges")
                        }
                        disabled={submission.pending}
                      />
                    </div>
                  </form>
                </div>

                <div class="rounded-2xl border border-outline bg-surface-alt p-6 shadow-sm dark:border-outline-dark dark:bg-surface-dark-alt">
                  <AvailabilitySection vehicleId={props.vehicleId} url={v().availability} zones={zones()} />
                </div>
              </div>

              <div class="lg:col-span-1 space-y-6">
                <div class="rounded-2xl border border-outline bg-surface-alt p-6 shadow-sm dark:border-outline-dark dark:bg-surface-dark-alt">
                  <h3 class="mb-4 text-xl font-semibold text-on-surface dark:text-on-surface-dark">
                    {t("global.page.editVehicle.photoSection")}
                  </h3>
                  <PictureSection pictureUrl={v().picture} />
                </div>
              </div>
            </div>
          )}
        </Show>
      </Suspense>
    </div>
  );
}

export const editVehiclePictureAction = action(
  async (api: Api, pictureUrl: string, toaster: any, t: any, formData: FormData) => {
    const file = formData.get("picture");

    if (!(file instanceof File) || file.size === 0) {
      toaster.notify(t("global.page.editVehicle.errors.selectImageFirst"), { type: "warning" });
      return { ok: false };
    }

    const allowedTypes = ["image/jpeg", "image/png"];
    if (!allowedTypes.includes(file.type)) {
      toaster.notify(t("global.page.editVehicle.errors.invalidFileType"), { type: "error" });
      return { ok: false };
    }

    const response = await api.putVehicleImage(pictureUrl, file);

    if (!response.ok) {
      if (response.status === 400 || response.status === 415) {
        toaster.notify(t("global.page.editVehicle.errors.imageFormat"), { type: "error" });
        return { ok: false };
      }
      throw new Error(t("global.page.editVehicle.errors.imageUpload"));
    }

    toaster.notify(t("global.page.editVehicle.success.imageUpdate"), { type: "success" });
    return { ok: true };
  },
  "edit-vehicle-picture-action"
);

function PictureSection(props: { pictureUrl: string }) {
  const api = useApi();
  const toaster = useToast();
  const t = useTranslator();
  const submission = useSubmission(editVehiclePictureAction);

  return (
    <form
      action={editVehiclePictureAction.with(api, props.pictureUrl, toaster, t)}
      method="post"
      enctype="multipart/form-data"
      class="flex flex-col gap-4"
    >
      <Suspense
        fallback={
          <div class="animate-pulse bg-surface p-12 rounded-radius text-center">
            {t("global.page.editVehicle.loading.fileUpload")}
          </div>
        }
      >
        <FileInputDragDrop
          name="picture"
          label={t("global.page.editVehicle.labels.uploadImage")}
          fileTypes={["PNG", "JPEG"]}
          maxSize={10}
          maxSizeUnit={"MB"}
          putEndpoint={props.pictureUrl}
          currentValue={{ url: props.pictureUrl }}
        />
      </Suspense>

      <div class="flex justify-end border-t border-outline pt-4 dark:border-outline-dark">
        <Button
          type="submit"
          category="primary"
          text={
            submission.pending
              ? t("global.page.editVehicle.buttons.uploadingPhoto")
              : t("global.page.editVehicle.buttons.savePhoto")
          }
          disabled={submission.pending}
        />
      </div>
    </form>
  );
}

export const editAvailabilityAction = action(
  async (api: Api, driverId: number, vehicleId: number, toaster: any, t: any, formData: FormData) => {
    const zones_uri = formData.getAll("zones") as string[];
    const zones = zones_uri.map((z) => Number(z.split("/").pop()));
    const timeSlotStrings = formData.getAll("timeSlots") as string[];
    const timeSlots = timeSlotStrings.map((ts) => {
      const [day, shift] = ts.split("-");
      return { day, shift };
    });

    const payload = { zones, timeSlots };

    const response = await api.updateVehicleAvailability(driverId, vehicleId, payload);

    if (!response.ok) {
      if (response.status === 400) {
        const errorList: ApiError[] = await response.json();
        const apiErrors: Record<string, string[]> = {};

        errorList.forEach((err) => {
          if (err.entity && err.description) {
            if (!apiErrors[err.entity]) apiErrors[err.entity] = [];
            apiErrors[err.entity].push(err.description);
          } else if (err.description) {
            toaster.notify(err.description, { type: "error" });
          }
        });
        return { ok: false, resp: apiErrors };
      }
      if (response.status === 403 || response.status === 404) {
        toaster.notify(t("global.page.editVehicle.errors.availabilityAccess"), { type: "error" });
        return { ok: false, resp: {} };
      }
      throw new Error(t("global.page.editVehicle.errors.availabilityUpdate"));
    }

    toaster.notify(t("global.page.editVehicle.success.availabilityUpdate"), { type: "success" });
    return { ok: true, resp: await response.json() };
  },
  "edit-availability-action"
);

function AvailabilitySection(props: { vehicleId: number; url: string; zones: Zone[] | undefined }) {
  const availability = createAsync(() => availabilityQuery(props.url));
  const [appState] = useAppState();
  const api = useApi();
  const toaster = useToast();
  const t = useTranslator();

  const submission = useSubmission(editAvailabilityAction);
  const errors = () => (submission.result?.ok === false ? submission.result.resp : {});

  const [selectedZoneIds, setSelectedZoneIds] = createSignal<number[]>([]);
  const [zoneToAdd, setZoneToAdd] = createSignal<number | undefined>();

  let isInitialized = false;
  createEffect(() => {
    const av = availability();
    if (av && !isInitialized) {
      setSelectedZoneIds(av.zoneIds);
      isInitialized = true;
    }
  });

  const handleAddZone = () => {
    const id = zoneToAdd();
    if (id && !selectedZoneIds().includes(id)) {
      setSelectedZoneIds([...selectedZoneIds(), id]);
      setZoneToAdd(undefined);
    }
  };

  const handleRemoveZone = (idToRemove: number) => {
    setSelectedZoneIds(selectedZoneIds().filter((id) => id !== idToRemove));
  };

  const availableZoneOptions = createMemo(() => {
    if (!props.zones) return [];
    return props.zones
      .filter((z) => !selectedZoneIds().includes(z.id))
      .map((z) => ({
        value: z.id,
        label: `${z.neighborhood.name}, ${z.province.name}`,
      }));
  });

  const DAYS = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"];
  const SHIFTS = ["MORNING", "AFTERNOON", "EVENING"];

  const hasSlot = (day: string, shift: string) => {
    return availability()?.timeSlots.some((ts) => ts.day === day && ts.shift === shift);
  };

  return (
    <form
      action={editAvailabilityAction.with(api, appState.user!.id, props.vehicleId, toaster, t)}
      method="post"
      class="space-y-8"
    >
      <div>
        <h3 class="mb-4 text-lg font-semibold text-on-surface dark:text-on-surface-dark">
          {t("global.page.editVehicle.workZones")}
        </h3>

        <div class="mb-4 flex items-end gap-2">
          <div class="flex-1">
            <Combobox
              name="zoneSelect"
              label={t("global.page.editVehicle.labels.addZone")}
              options={availableZoneOptions()}
              value={zoneToAdd()}
              onChange={(val: any) => setZoneToAdd(Number(val))}
              placeholder={t("global.page.editVehicle.placeholders.searchNeighborhood")}
            />
          </div>
          <Button
            type="button"
            category="secondary"
            text={t("global.page.editVehicle.buttons.add")}
            onClick={handleAddZone}
          />
        </div>

        <Show
          when={selectedZoneIds().length > 0}
          fallback={<p class="text-sm text-on-surface/50">{t("global.page.editVehicle.noZonesAssigned")}</p>}
        >
          <ul class="flex flex-wrap gap-2 mt-2">
            <For each={selectedZoneIds()}>
              {(id) => {
                const zone = props.zones?.find((z) => z.id === id);

                return zone ? (
                  <li class="flex items-center gap-1 rounded-full bg-primary/10 pl-3 pr-1 py-1 text-sm text-primary border border-primary/20 dark:bg-primary-dark/10 dark:text-primary-dark">
                    {zone.neighborhood.name}
                    <button
                      type="button"
                      onClick={() => handleRemoveZone(id)}
                      class="rounded-full p-1 hover:bg-primary/20 dark:hover:bg-primary-dark/20 transition-colors"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="currentColor" class="size-4">
                        <path d="M5.28 4.22a.75.75 0 0 0-1.06 1.06L6.94 8l-2.72 2.72a.75.75 0 1 0 1.06 1.06L8 9.06l2.72 2.72a.75.75 0 1 0 1.06-1.06L9.06 8l2.72-2.72a.75.75 0 0 0-1.06-1.06L8 6.94 5.28 4.22Z" />
                      </svg>
                    </button>

                    <input type="hidden" name="zones" value={`api/zones/${id}`} />
                  </li>
                ) : null;
              }}
            </For>
          </ul>
        </Show>
        <Show when={errors()?.["zones"]}>
          <small class="text-danger dark:text-red-400 mt-2 block">• {errors()?.["zones"]?.[0]}</small>
        </Show>
      </div>

      <hr class="border-outline dark:border-outline-dark" />

      <div>
        <h3 class="mb-4 text-lg font-semibold text-on-surface dark:text-on-surface-dark">
          {t("global.page.editVehicle.availableSchedules")}
        </h3>
        <div class="overflow-x-auto rounded-radius border border-outline dark:border-outline-dark">
          <table class="w-full text-left text-sm text-on-surface dark:text-on-surface-dark">
            <thead class="bg-surface-alt dark:bg-surface-dark-alt">
              <tr class="border-b border-outline dark:border-outline-dark">
                <th class="py-2 px-4 font-medium">{t("global.page.editVehicle.table.day")}</th>
                <th class="py-2 px-4 font-medium text-center">{t("global.page.editVehicle.table.morning")}</th>
                <th class="py-2 px-4 font-medium text-center">{t("global.page.editVehicle.table.afternoon")}</th>
                <th class="py-2 px-4 font-medium text-center">{t("global.page.editVehicle.table.evening")}</th>
              </tr>
            </thead>
            <tbody>
              <For each={DAYS}>
                {(day) => (
                  <tr class="border-b border-outline/50 dark:border-outline-dark/50 last:border-0 hover:bg-surface-alt/50 dark:hover:bg-surface-dark-alt/50">
                    <td class="py-3 px-4 capitalize font-medium">{t(`global.page.editVehicle.days.${day}`)}</td>
                    <For each={SHIFTS}>
                      {(shift) => (
                        <td class="py-3 px-4 text-center">
                          <input
                            type="checkbox"
                            name="timeSlots"
                            value={`${day}-${shift}`}
                            checked={hasSlot(day, shift)}
                            class="size-4 cursor-pointer rounded border-outline text-primary focus:ring-primary dark:border-outline-dark dark:bg-surface-dark dark:checked:bg-primary-dark"
                          />
                        </td>
                      )}
                    </For>
                  </tr>
                )}
              </For>
            </tbody>
          </table>
        </div>
        <Show when={errors()?.["shift"] || errors()?.["day"]}>
          <small class="text-danger dark:text-red-400 mt-2 block">
            • {t("global.page.editVehicle.errors.verifyShifts")}
          </small>
        </Show>
      </div>

      <div class="flex justify-end pt-2">
        <Button
          type="submit"
          category="primary"
          text={
            submission.pending
              ? t("global.page.editVehicle.buttons.saving")
              : t("global.page.editVehicle.buttons.saveAvailability")
          }
          disabled={submission.pending}
        />
      </div>
    </form>
  );
}

export default function VehicleEdit() {
  const params = useParams();
  const vehicleId = parseInt(params.vehicleId);

  return <VehicleSection vehicleId={vehicleId} />;
}
