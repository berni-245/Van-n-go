import { action, createAsync, query, useNavigate, useParams, useSubmission } from "@solidjs/router";
import { createEffect, createMemo, createResource, createSignal, For, Index, Show, Suspense } from "solid-js";
import { useApi, useTranslator } from "../app-context/AppContext";
import { useToast } from "solid-notifications";
import { ApiError, Availability, Zone } from "../api/types";
import { Api } from "../app-context/api";
import Button from "../components/Button";
import Combobox from "../components/inputs/Combobox";

export const createBookingAction = action(async (api: Api, t: any, toaster: any, formData: FormData) => {
  const fieldErrors: Record<string, string[]> = {};

  const vehicleId = Number(formData.get("vehicleId"));
  const date = formData.get("date") as string;
  const shiftPeriod = formData.get("shiftPeriod") as string;
  const originZoneIdStr = formData.get("originZoneId") as string;
  const destinationZoneIdStr = formData.get("destinationZoneId") as string;
  const jobDescription = formData.get("jobDescription") as string;

  if (!originZoneIdStr) fieldErrors["originZoneId"] = [t("global.page.bookingCreate.errors.originRequired")];
  if (!destinationZoneIdStr)
    fieldErrors["destinationZoneId"] = [t("global.page.bookingCreate.errors.destinationRequired")];
  if (!jobDescription || jobDescription.length < 5 || jobDescription.length > 500) {
    fieldErrors["jobDescription"] = [t("global.page.bookingCreate.errors.descriptionLength")];
  }
  if (!date) fieldErrors["date"] = [t("global.page.bookingCreate.errors.dateRequired")];

  if (Object.keys(fieldErrors).length > 0) return { ok: false, resp: fieldErrors };

  const payload = {
    vehicleId,
    originZoneId: Number(originZoneIdStr),
    destinationZoneId: Number(destinationZoneIdStr),
    date,
    shiftPeriod,
    jobDescription,
  };

  const response = await api.createBooking(payload);

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
      toaster.notify(t("global.page.bookingCreate.errors.conflict"), { type: "error" });
      return { ok: false, resp: {} };
    }
    throw new Error(t("global.page.bookingCreate.errors.bookingFailed"));
  }

  toaster.notify(t("global.page.bookingCreate.success"), { type: "success" });
  return { ok: true, resp: await response.json() };
}, "create-booking-action");

const driverDataQuery = query(async (driverId: number) => {
  const api = useApi();
  const vehicles = await api.getDriverVehicles(driverId);

  const vehiclesWithAvail = await Promise.all(
    vehicles.map(async (v) => {
      const availability = await api.getVehicleAvailability(v.availability);
      return { ...v, availabilityDetails: availability as Availability };
    })
  );

  return vehiclesWithAvail;
}, "driver-full-data");

const zonesQuery = query(() => useApi().getZones(), "zones");

const MONTHS = [
  "JANUARY",
  "FEBRUARY",
  "MARCH",
  "APRIL",
  "MAY",
  "JUNE",
  "JULY",
  "AUGUST",
  "SEPTEMBER",
  "OCTOBER",
  "NOVEMBER",
  "DECEMBER",
];
const DAYS_OF_WEEK = ["SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"];

function getDaysInMonth(year: number, monthIndex: number) {
  return new Date(year, monthIndex + 1, 0).getDate();
}

function getFirstDayOfMonth(year: number, monthIndex: number) {
  return new Date(year, monthIndex, 1).getDay();
}

export default function BookingCreate() {
  const params = useParams();
  const driverId = parseInt(params.driverId);

  const api = useApi();
  const t = useTranslator();
  const toaster = useToast();
  const navigate = useNavigate();

  const driverVehicles = createAsync(() => driverDataQuery(driverId));
  const allZones = createAsync(() => zonesQuery());

  const [currentMonthDate, setCurrentMonthDate] = createSignal(new Date());
  const [monthsAdvanced, setMonthsAdvanced] = createSignal(0);
  const [selectedDateStr, setSelectedDateStr] = createSignal<string | null>(null);
  const [selectedVehicleSlot, setSelectedVehicleSlot] = createSignal<{ vehicle: any; shift: string } | null>(null);

  const [bookedTimesData] = createResource(
    () => ({ date: currentMonthDate(), vehicles: driverVehicles() }),
    async (source) => {
      if (!source.vehicles) return {};
      const monthStr = MONTHS[source.date.getMonth()];
      const year = source.date.getFullYear();

      const results: Record<number, { date: string; shift: string }[]> = {};
      await Promise.all(
        source.vehicles.map(async (v: any) => {
          results[v.id] = await api.getBookedTimes(driverId, v.id, monthStr, year);
        })
      );
      return results;
    }
  );

  const getAvailableShifts = (dateStr: string, dayOfWeekStr: string, vehicleId?: number) => {
    const vehicles = driverVehicles();
    const bookedData = bookedTimesData();
    if (!vehicles || !bookedData) return [];

    let availableSlots: { vehicleId: number; shift: string }[] = [];

    vehicles.forEach((v) => {
      if (vehicleId && v.id !== vehicleId) return;
      if (!v.availabilityDetails) return;

      const vehicleBooked = bookedData[v.id] || [];

      const shiftsForDay = v.availabilityDetails.timeSlots
        .filter((ts: any) => ts.day === dayOfWeekStr)
        .map((ts: any) => ts.shift);

      shiftsForDay.forEach((shift: string) => {
        const isBooked = vehicleBooked.some((bt) => bt.date === dateStr && bt.shift === shift);
        if (!isBooked) {
          availableSlots.push({ vehicleId: v.id, shift });
        }
      });
    });

    return availableSlots;
  };

  createEffect(() => {
    if (!driverVehicles() || !bookedTimesData() || bookedTimesData.loading) return;

    const year = currentMonthDate().getFullYear();
    const month = currentMonthDate().getMonth();
    const daysInMonth = getDaysInMonth(year, month);

    let hasAnyAvailability = false;
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    for (let i = 1; i <= daysInMonth; i++) {
      const checkDate = new Date(year, month, i);
      if (checkDate < today) continue;

      const dateStr = checkDate.toISOString().split("T")[0];
      const dayOfWeekStr = DAYS_OF_WEEK[checkDate.getDay()];

      if (getAvailableShifts(dateStr, dayOfWeekStr).length > 0) {
        hasAnyAvailability = true;
        break;
      }
    }

    if (!hasAnyAvailability && monthsAdvanced() < 6) {
      setMonthsAdvanced((prev) => prev + 1);
      setCurrentMonthDate((prev) => new Date(prev.getFullYear(), prev.getMonth() + 1, 1));
    }
  });

  const calendarDays = createMemo(() => {
    const year = currentMonthDate().getFullYear();
    const month = currentMonthDate().getMonth();
    const daysInMonth = getDaysInMonth(year, month);
    const firstDay = getFirstDayOfMonth(year, month);

    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const days = [];
    for (let i = 0; i < firstDay; i++) days.push(null);

    for (let i = 1; i <= daysInMonth; i++) {
      const dateObj = new Date(year, month, i);
      const isPast = dateObj < today;
      const dateStr = dateObj.toISOString().split("T")[0];
      const dayOfWeekStr = DAYS_OF_WEEK[dateObj.getDay()];

      const hasAvail = !isPast && getAvailableShifts(dateStr, dayOfWeekStr).length > 0;

      days.push({
        num: i,
        dateStr,
        dayOfWeekStr,
        disabled: isPast || !hasAvail,
        isToday: dateObj.getTime() === today.getTime(),
      });
    }
    return days;
  });

  const handleNextMonth = () => setCurrentMonthDate((prev) => new Date(prev.getFullYear(), prev.getMonth() + 1, 1));
  const handlePrevMonth = () => {
    const today = new Date();
    const newDate = new Date(currentMonthDate().getFullYear(), currentMonthDate().getMonth() - 1, 1);
    if (
      newDate.getFullYear() > today.getFullYear() ||
      (newDate.getFullYear() === today.getFullYear() && newDate.getMonth() >= today.getMonth())
    ) {
      setCurrentMonthDate(newDate);
    }
  };

  const handleDayClick = (day: any) => {
    if (day.disabled) return;
    setSelectedDateStr(day.dateStr);
    setSelectedVehicleSlot(null);
  };

  const vehiclesForSelectedDate = createMemo(() => {
    if (!selectedDateStr()) return [];
    const dateObj = new Date(selectedDateStr() + "T00:00:00");
    const dayOfWeekStr = DAYS_OF_WEEK[dateObj.getDay()];

    const availableSlots = getAvailableShifts(selectedDateStr()!, dayOfWeekStr);
    const vehiclesWithSlots: Record<number, string[]> = {};

    availableSlots.forEach((slot) => {
      if (!vehiclesWithSlots[slot.vehicleId]) vehiclesWithSlots[slot.vehicleId] = [];
      vehiclesWithSlots[slot.vehicleId].push(slot.shift);
    });

    return driverVehicles()!
      .filter((v) => vehiclesWithSlots[v.id])
      .map((v) => ({
        ...v,
        shifts: vehiclesWithSlots[v.id],
      }));
  });

  const submission = useSubmission(createBookingAction);
  const errors = () => (submission.result?.ok === false ? submission.result.resp : {});

  createEffect((wasPending) => {
    const isPending = submission.pending;
    if (wasPending && !isPending && submission.result?.ok) {
      navigate("/client/bookings", { replace: true });
    }
    return isPending;
  }, false);

  const currentZoneOptions = createMemo(() => {
    if (!selectedVehicleSlot() || !allZones()) return [];
    const uris = selectedVehicleSlot()!.vehicle.availabilityDetails.zones as string[];
    const validIds = uris.map((uri) => Number(uri.split("/").pop()));

    return validIds
      .map((id) => {
        const z = allZones()!.find((zone: Zone) => zone.id === id);
        return z ? { value: z.id, label: `${z.neighborhood.name}, ${z.province.name}` } : null;
      })
      .filter(Boolean) as { value: number; label: string }[];
  });

  return (
    <div class="mx-auto max-w-5xl p-4 md:p-8 space-y-8">
      <div class="mb-6">
        <h1 class="text-3xl font-bold text-on-surface dark:text-on-surface-dark">
          {t("global.page.bookingCreate.title")}
        </h1>
        <p class="text-on-surface/70 dark:text-on-surface-dark/70">{t("global.page.bookingCreate.subtitle")}</p>
      </div>

      <Suspense fallback={<div class="animate-pulse">{t("global.page.bookingCreate.emptyMonth")}</div>}>
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <div class="rounded-2xl border border-outline bg-surface-alt p-6 shadow-sm dark:border-outline-dark dark:bg-surface-dark-alt h-fit">
            <div class="flex items-center justify-between mb-4">
              <button
                onClick={handlePrevMonth}
                class="p-2 hover:bg-surface dark:hover:bg-surface-dark rounded-full transition-colors text-on-surface dark:text-on-surface-dark"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke-width="2"
                  stroke="currentColor"
                  class="size-5"
                >
                  <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 19.5 8.25 12l7.5-7.5" />
                </svg>
              </button>
              <h2 class="text-lg font-semibold capitalize text-on-surface dark:text-on-surface-dark">
                {t(`global.page.bookingCreate.months.${MONTHS[currentMonthDate().getMonth()]}`)}{" "}
                {currentMonthDate().getFullYear()}
              </h2>
              <button
                onClick={handleNextMonth}
                class="p-2 hover:bg-surface dark:hover:bg-surface-dark rounded-full transition-colors text-on-surface dark:text-on-surface-dark"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke-width="2"
                  stroke="currentColor"
                  class="size-5"
                >
                  <path stroke-linecap="round" stroke-linejoin="round" d="m8.25 4.5 7.5 7.5-7.5 7.5" />
                </svg>
              </button>
            </div>

            <div class="grid grid-cols-7 gap-1 text-center text-sm font-medium text-on-surface/60 dark:text-on-surface-dark/60 mb-2">
              <For each={t("global.page.bookingCreate.weekDays")}>{(wd) => <div>{wd}</div>}</For>
            </div>

            <div class="grid grid-cols-7 gap-1">
              <Index each={calendarDays()}>
                {(day) => (
                  <div class="aspect-square p-0.5">
                    <Show when={day()}>
                      <button
                        onClick={() => handleDayClick(day())}
                        disabled={day()?.disabled}
                        class="w-full h-full flex items-center justify-center rounded-full text-sm transition-colors"
                        classList={{
                          "bg-primary text-on-primary font-bold dark:bg-primary-dark dark:text-on-primary-dark":
                            selectedDateStr() === day().dateStr,
                          "hover:bg-primary/20 dark:hover:bg-primary-dark/20 text-on-surface dark:text-on-surface-dark":
                            !day().disabled && selectedDateStr() !== day().dateStr,
                          "opacity-30 cursor-not-allowed text-on-surface dark:text-on-surface-dark": day().disabled,
                          "border border-primary dark:border-primary-dark":
                            day().isToday && selectedDateStr() !== day().dateStr,
                        }}
                      >
                        {day().num}
                      </button>
                    </Show>
                  </div>
                )}
              </Index>
            </div>
          </div>

          <div class="space-y-6">
            <Show when={selectedDateStr()}>
              <div class="rounded-2xl border border-outline bg-surface-alt p-6 shadow-sm dark:border-outline-dark dark:bg-surface-dark-alt">
                <h3 class="mb-4 text-lg font-semibold text-on-surface dark:text-on-surface-dark">
                  {t("global.page.bookingCreate.availableVehicles")} {selectedDateStr()}
                </h3>

                <div class="space-y-4">
                  <Show
                    when={vehiclesForSelectedDate().length > 0}
                    fallback={<p class="text-sm text-on-surface/60">No hay vehículos disponibles.</p>}
                  >
                    <For each={vehiclesForSelectedDate()}>
                      {(v) => (
                        <div class="border border-outline/50 rounded-radius p-4 dark:border-outline-dark/50 bg-surface dark:bg-surface-dark">
                          <div class="flex justify-between items-start mb-3">
                            <div>
                              <div class="font-bold text-on-surface dark:text-on-surface-dark uppercase">
                                {v.plateNumber}
                              </div>
                              <div class="text-sm text-on-surface/70 dark:text-on-surface-dark/70">
                                {t("global.page.bookingCreate.vehicleDetails.volume")}: {v.volumeM3} m³ |{" "}
                                {t("global.page.bookingCreate.vehicleDetails.rate")}: ${v.hourlyRate}
                              </div>
                            </div>
                          </div>
                          <div class="flex flex-wrap gap-2">
                            <For each={v.shifts}>
                              {(shift) => (
                                <button
                                  onClick={() => setSelectedVehicleSlot({ vehicle: v, shift })}
                                  class="px-4 py-1.5 rounded-full text-sm border transition-colors"
                                  classList={{
                                    "bg-primary text-on-primary border-primary font-medium dark:bg-primary-dark dark:text-on-primary-dark dark:border-primary-dark":
                                      selectedVehicleSlot()?.vehicle.id === v.id &&
                                      selectedVehicleSlot()?.shift === shift,
                                    "bg-surface-alt border-outline text-on-surface hover:border-primary dark:bg-surface-dark-alt dark:border-outline-dark dark:text-on-surface-dark dark:hover:border-primary-dark":
                                      !(
                                        selectedVehicleSlot()?.vehicle.id === v.id &&
                                        selectedVehicleSlot()?.shift === shift
                                      ),
                                  }}
                                >
                                  {t(`global.page.bookingCreate.shifts.${shift}`)}
                                </button>
                              )}
                            </For>
                          </div>
                        </div>
                      )}
                    </For>
                  </Show>
                </div>
              </div>
            </Show>

            <Show when={selectedVehicleSlot()}>
              <div class="rounded-2xl border border-primary bg-primary/5 p-6 shadow-sm dark:border-primary-dark dark:bg-primary-dark/5">
                <h3 class="mb-4 text-xl font-bold text-on-surface dark:text-on-surface-dark">
                  {t("global.page.bookingCreate.confirmation.title")}
                </h3>

                <form action={createBookingAction.with(api, t, toaster)} method="post" class="space-y-4">
                  <input type="hidden" name="vehicleId" value={selectedVehicleSlot()!.vehicle.id} />
                  <input type="hidden" name="date" value={selectedDateStr()!} />
                  <input type="hidden" name="shiftPeriod" value={selectedVehicleSlot()!.shift} />

                  <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    <Combobox
                      name="originZoneId"
                      label={t("global.page.bookingCreate.confirmation.origin")}
                      options={currentZoneOptions()}
                      error={errors()?.["originZoneId"]}
                    />
                    <Combobox
                      name="destinationZoneId"
                      label={t("global.page.bookingCreate.confirmation.destination")}
                      options={currentZoneOptions()}
                      error={errors()?.["destinationZoneId"]}
                    />
                  </div>

                  <div class="flex flex-col gap-1 text-on-surface dark:text-on-surface-dark">
                    <label class="w-fit pl-0.5 text-sm">
                      {t("global.page.bookingCreate.confirmation.description")}
                    </label>
                    <textarea
                      name="jobDescription"
                      rows="3"
                      maxLength={500}
                      class="w-full resize-none rounded-radius border border-outline bg-surface px-4 py-2 text-sm focus-visible:outline-2 focus-visible:outline-primary dark:border-outline-dark dark:bg-surface-dark dark:focus-visible:outline-primary-dark"
                      classList={{ "border-danger dark:border-red-400": !!errors()?.["jobDescription"] }}
                    />
                    <Show when={errors()?.["jobDescription"]}>
                      <small class="pl-0.5 text-danger dark:text-red-400">• {errors()?.["jobDescription"]?.[0]}</small>
                    </Show>
                  </div>

                  <div class="pt-4 flex justify-end">
                    <Button
                      type="submit"
                      category="primary"
                      text={
                        submission.pending
                          ? t("global.page.bookingCreate.confirmation.submitting")
                          : t("global.page.bookingCreate.confirmation.submit")
                      }
                      disabled={submission.pending}
                    />
                  </div>
                </form>
              </div>
            </Show>
          </div>
        </div>
      </Suspense>
    </div>
  );
}
