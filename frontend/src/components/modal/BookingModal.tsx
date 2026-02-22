// components/modals/BookingModal.tsx
import {JSX, Show} from "solid-js";
import {Booking, BookingState, Client, Driver, Vehicle, Zone} from "@/api/types.ts";
import {useTranslator} from "@/app-context/AppContext.tsx";

interface BookingModalProps {
    open: boolean;
    title: string;
    onClose: () => void;
    children: JSX.Element;
    booking: Booking;
    vehicle: Vehicle;
    targetUser: Client | Driver;
    originZone: Zone;
    destinationZone: Zone;
    targetUserType: "client" | "driver";
}

export function BookingModal(props: BookingModalProps) {
    const t = useTranslator();

    function getColorTagForBookingState(state: BookingState): string {
        switch (state) {
            case "PENDING":
                return "border-info text-info dark:border-info dark:text-info";
            case "ACCEPTED":
                return "border-success text-success dark:border-success dark:text-success";
            case "FINISHED":
                return "border-success/50 text-success dark:border-success/50 dark:text-success";
            case "REJECTED":
                return "border-warning text-warning dark:border-warning dark:text-warning";
            case "CANCELED":
                return "border-danger text-danger dark:border-danger dark:text-danger";
            default:
                throw new Error(`Invalid booking state: ${state}`);
        }
    }

    return (
        <Show when={props.open}>
            <div
                class="fixed inset-0 z-30 flex items-end justify-center bg-black/20 p-4 pb-8 backdrop-blur-md sm:items-center lg:p-8"
                role="dialog"
                aria-modal="true"
                onClick={(e) => e.target === e.currentTarget && props.onClose()}
            >
                <div
                    class="flex w-full max-w-lg flex-col overflow-hidden rounded-radius border border-outline bg-surface text-on-surface dark:border-outline-dark dark:bg-surface-dark-alt dark:text-on-surface-dark">

                    <div
                        class="flex items-center justify-between border-b border-outline bg-surface-alt/60 p-4 dark:border-outline-dark dark:bg-surface-dark/20"
                    >
                        <div class="flex items-center gap-3">
                            <h4 class="font-semibold tracking-wide">
                                {props.title}
                            </h4>

                            <span
                                class={`rounded-full px-2.5 py-0.5 text-xs font-semibold uppercase text-xs font-medium border bg-on-surface dark:bg-surface-dark ${getColorTagForBookingState(props.booking.state)}`}>
                              {t(`global.page.bookings.${props.booking.state.toLowerCase()}` as any)}
                            </span>

                        </div>

                        <button onClick={props.onClose} aria-label="close modal">
                            ✕
                        </button>
                    </div>

                    <div class="flex flex-col gap-6 px-5 py-6 text-sm">

                        <div class="flex flex-col gap-1">
    <span class="text-xs uppercase tracking-wide text-on-surface-weak dark:text-on-surface-dark-weak">
      {t(`global.page.bookings.${props.targetUserType}` as any)}
    </span>
                            <span class="text-lg font-semibold text-information dark:text-white">
      {props.targetUser.username}
    </span>
                        </div>

                        <div>
                            <span
                                class="mb-1 block text-xs uppercase tracking-wide text-on-surface-weak dark:text-on-surface-dark-weak">
      {t("global.page.bookings.route")}
    </span>
                            <span class="text-lg font-semibold text-information dark:text-white">
      {t("global.page.bookings.travelZones", {
          originNeighborhood: props.originZone.neighborhood.name,
          destinationNeighborhood: props.destinationZone.neighborhood.name
      })}
    </span>
                        </div>

                        {/* Descripción */}
                        <div class="flex flex-col gap-1">
    <span class="text-xs uppercase tracking-wide text-on-surface-weak dark:text-on-surface-dark-weak">
      {t("global.page.bookings.description")}
    </span>
                            <p class="text-lg font-semibold text-information dark:text-white">
                                {props.booking.jobDescription}
                            </p>
                        </div>


                        <div class="flex flex-col gap-3">
    <span class="text-xs font-semibold uppercase tracking-wide text-on-surface-weak dark:text-on-surface-dark-weak">
      {t("global.page.bookings.vehicleInformation")}
    </span>

                            <div
                                class="grid grid-cols-2 gap-4 rounded-radius bg-surface-alt px-4 py-3 dark:bg-surface-dark">
                                <div class="flex flex-col gap-1">
        <span class="text-xs uppercase tracking-wide text-on-surface-weak dark:text-on-surface-dark-weak">
          {t("global.page.bookings.plate")}
        </span>
                                    <span class="text-lg font-semibold text-primary dark:text-primary-dark">
          {props.vehicle.plateNumber}
        </span>
                                </div>

                                <div class="flex flex-col gap-1 text-right">
        <span class="text-xs uppercase tracking-wide text-on-surface-weak dark:text-on-surface-dark-weak">
          {t("global.page.bookings.hourlyRate")}
        </span>
                                    <span class="text-lg font-semibold text-primary dark:text-primary-dark">
          ${props.vehicle.hourlyRate}
        </span>
                                </div>
                            </div>
                        </div>

                    </div>
                    {/* FOOTER (variable) */}
                    <div
                        class="border-t border-outline bg-surface-alt/60 p-4 dark:border-outline-dark dark:bg-surface-dark/20">
                        {props.children}
                    </div>
                </div>
            </div>
        </Show>
    );
}