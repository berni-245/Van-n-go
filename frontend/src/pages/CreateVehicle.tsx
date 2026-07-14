import { action, useSubmission, useNavigate } from "@solidjs/router";
import { Show, createEffect } from "solid-js";
import { useApi, useAppState, useTranslator } from "../app-context/AppContext";
import { useToast } from "solid-notifications";
import { ApiError } from "../api/types";
import { Api } from "../app-context/api";
import TextInput from "../components/inputs/TextInput";
import Button from "../components/Button";
import {
  isValidPlateNumber,
  isValidVehicleVolume,
  isValidHourlyRate,
  isValidVehicleDescriptionLength,
} from "@/utils/validation";

export const createVehicleAction = action(
  async (api: Api, driverId: number, t: any, toaster: any, formData: FormData) => {
    const fieldErrors: Record<string, string[]> = {};

    const plateNumber = formData.get("plateNumber") as string;
    const volumeM3Str = formData.get("volumeM3") as string;
    const hourlyRateStr = formData.get("hourlyRate") as string;
    const description = formData.get("description") as string;

    if (!plateNumber) {
      fieldErrors["plateNumber"] = [t("global.page.createVehicle.errors.required")];
    } else if (!isValidPlateNumber(plateNumber)) {
      fieldErrors["plateNumber"] = [t("global.page.createVehicle.errors.plateRegex")];
    }

    let volumeM3: number | undefined;
    if (!volumeM3Str) {
      fieldErrors["volumeM3"] = [t("global.page.createVehicle.errors.required")];
    } else {
      volumeM3 = parseFloat(volumeM3Str);
      if (!isValidVehicleVolume(volumeM3)) {
        fieldErrors["volumeM3"] = [t("global.page.createVehicle.errors.volume")];
      }
    }

    let hourlyRate: number | undefined;
    if (!hourlyRateStr) {
      fieldErrors["hourlyRate"] = [t("global.page.createVehicle.errors.required")];
    } else {
      hourlyRate = parseFloat(hourlyRateStr);
      if (!isValidHourlyRate(hourlyRate)) {
        fieldErrors["hourlyRate"] = [t("global.page.createVehicle.errors.rate")];
      }
    }

    if (!description) {
      fieldErrors["description"] = [t("global.page.createVehicle.errors.required")];
    } else if (!isValidVehicleDescriptionLength(description)) {
      fieldErrors["description"] = [t("global.page.createVehicle.errors.descriptionLength")];
    }

    if (Object.keys(fieldErrors).length > 0) {
      return { ok: false, resp: fieldErrors };
    }

    const payload = {
      plateNumber,
      volumeM3: volumeM3!,
      hourlyRate: hourlyRate!,
      description,
    };

    const response = await api.createVehicle(driverId, payload);

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
      if (response.status === 409) {
        toaster.notify(t("global.page.createVehicle.errors.limitReached"), { type: "error" });
        return { ok: false, resp: {} };
      }
      if (response.status === 403 || response.status === 401) {
        toaster.notify(t("global.page.createVehicle.errors.access"), { type: "error" });
        return { ok: false, resp: {} };
      }
      throw new Error(t("global.page.createVehicle.errors.create"));
    }

    toaster.notify(t("global.page.createVehicle.success.create"), { type: "success" });
    return { ok: true, resp: await response.json() };
  },
  "create-vehicle-action"
);

export default function VehicleCreate() {
  const [appState] = useAppState();
  const api = useApi();
  const t = useTranslator();
  const toaster = useToast();
  const navigate = useNavigate();

  const submission = useSubmission(createVehicleAction);
  const errors = () => (submission.result?.ok === false ? submission.result.resp : {});

  createEffect((wasPending) => {
    const isPending = submission.pending;
    if (wasPending && !isPending && submission.result?.ok) {
      navigate("/driver/vehicles", { replace: true });
    }
    return isPending;
  }, false);

  return (
    <div class="mx-auto max-w-2xl p-4 md:p-8 space-y-8">
      <div class="mb-6">
        <h1 class="text-3xl font-bold text-on-surface dark:text-on-surface-dark">
          {t("global.page.createVehicle.title")}
        </h1>
        <p class="text-on-surface/70 dark:text-on-surface-dark/70">{t("global.page.createVehicle.subtitle")}</p>
      </div>

      <div class="rounded-2xl border border-outline bg-surface-alt p-6 shadow-sm dark:border-outline-dark dark:bg-surface-dark-alt">
        <h3 class="mb-4 text-xl font-semibold text-on-surface dark:text-on-surface-dark">
          {t("global.page.createVehicle.generalData")}
        </h3>

        <form action={createVehicleAction.with(api, appState.user!.id, t, toaster)} method="post" class="space-y-4">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <TextInput
              name="plateNumber"
              label={t("global.page.createVehicle.labels.plate")}
              error={errors()?.["plateNumber"]}
              disabled={submission.pending}
            />
            <TextInput
              name="hourlyRate"
              type="number"
              label={t("global.page.createVehicle.labels.rate")}
              error={errors()?.["hourlyRate"]}
              disabled={submission.pending}
              min="100"
              max="100000"
              step="0.01"
            />
          </div>

          <TextInput
            name="volumeM3"
            type="number"
            label={t("global.page.createVehicle.labels.volume")}
            error={errors()?.["volumeM3"]}
            disabled={submission.pending}
            min="1"
            max="100"
            step="0.1"
          />

          <div class="flex flex-col gap-1 text-on-surface dark:text-on-surface-dark">
            <label class="w-fit pl-0.5 text-sm">{t("global.page.createVehicle.labels.description")}</label>
            <textarea
              name="description"
              rows="3"
              maxLength={255}
              class="w-full resize-none rounded-radius border border-outline bg-surface px-4 py-2 text-sm focus-visible:outline-2 focus-visible:outline-primary dark:border-outline-dark dark:bg-surface-dark dark:focus-visible:outline-primary-dark"
              classList={{ "border-danger dark:border-red-400": !!errors()?.["description"] }}
              disabled={submission.pending}
            />
            <Show when={errors()?.["description"]}>
              <small class="pl-0.5 text-danger dark:text-red-400">• {errors()?.["description"]?.[0]}</small>
            </Show>
          </div>

          <div class="pt-4 flex justify-end gap-2">
            <Button
              type="button"
              category="secondary"
              text={t("global.page.createVehicle.buttons.cancel")}
              onClick={() => navigate(-1)}
              disabled={submission.pending}
            />
            <Button
              type="submit"
              category="primary"
              text={
                submission.pending
                  ? t("global.page.createVehicle.buttons.creating")
                  : t("global.page.createVehicle.buttons.create")
              }
              disabled={submission.pending}
            />
          </div>
        </form>
      </div>
    </div>
  );
}
