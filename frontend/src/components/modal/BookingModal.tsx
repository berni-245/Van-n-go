// components/modals/BookingModal.tsx
import {JSX, Show} from "solid-js";
import {Booking, Client, Driver, Vehicle, Zone} from "@/api/types.ts";
import {useTranslator} from "@/app-context/AppContext.tsx";

interface BookingModalProps {
    open: boolean;
    title: string;
    onClose: () => void;
    children: JSX.Element; // footer variable
    booking: Booking;
    vehicle: Vehicle;
    targetUser: Client | Driver;
    originZone: Zone;
    destinationZone: Zone;
}

export function BookingModal(props: BookingModalProps) {
    const t = useTranslator();

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

                    {/* HEADER */}
                    <div
                        class="flex items-center justify-between border-b border-outline bg-surface-alt/60 p-4 dark:border-outline-dark dark:bg-surface-dark/20">
                        <h3 class="font-semibold tracking-wide">
                            {props.title}
                        </h3>
                        <button onClick={props.onClose} aria-label="close modal">
                            ✕
                        </button>
                    </div>

                    {/* BODY */}
                    <div class="flex flex-col gap-5 px-5 py-6 text-sm">

                        {/* Usuario */}
                        <div class="flex flex-col gap-1">
    <span class="text-xs uppercase tracking-wide text-on-surface-weak dark:text-on-surface-dark-weak">
      {/*{t("global.page.bookings.client")}*/}
        cliente falta localizar
    </span>
                            <span class="text-base font-semibold">
      {props.targetUser.username}
    </span>
                        </div>

                        {/* Trayecto */}
                        <div class="rounded-radius bg-surface px-4 py-3 dark:bg-surface-dark">
    <span class="block text-xs uppercase tracking-wide text-on-surface-weak dark:text-on-surface-dark-weak mb-1">
      {/*{t("global.page.bookings.route")}*/}
        ruta falta localizar
    </span>
                            <span class="text-base font-medium">
      {t("global.page.bookings.travelZones", {
          originNeighborhood: props.originZone.neighborhood.name,
          destinationNeighborhood: props.destinationZone.neighborhood.name
      })}
    </span>
                        </div>

                        {/* Descripción */}
                        <div class="flex flex-col gap-1">
    <span class="text-xs uppercase tracking-wide text-on-surface-weak dark:text-on-surface-dark-weak">
      {/*{t("global.page.bookings.description")}*/}
        descripción falta localizar
    </span>
                            <p class="text-sm leading-relaxed">
                                {/*{props.booking.jobDescription || t("global.page.bookings.noDescription")}*/}
                                {props.booking.jobDescription}
                            </p>
                        </div>

                        {/* Datos del vehículo */}
                        <div class="grid grid-cols-2 gap-4 rounded-radius bg-surface px-4 py-3 dark:bg-surface-dark">
                            <div class="flex flex-col gap-1">
      <span class="text-xs uppercase tracking-wide text-on-surface-weak dark:text-on-surface-dark-weak">
        {/*{t("global.page.bookings.plate")}*/}
          plate number falta localizar
      </span>
                                <span class="font-medium">
        {props.vehicle.plateNumber}
      </span>
                            </div>

                            <div class="flex flex-col gap-1 text-right">
      <span class="text-xs uppercase tracking-wide text-on-surface-weak dark:text-on-surface-dark-weak">
        {/*{t("global.page.bookings.hourlyRate")}*/}
          costo por hora falta localizar
      </span>
                                <span class="text-lg font-semibold text-primary dark:text-primary-dark">
        {t("global.page.bookings.hourlyRate", {
            price: props.vehicle.hourlyRate
        })}
      </span>
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