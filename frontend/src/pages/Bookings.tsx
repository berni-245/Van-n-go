import {createSignal, For, Suspense} from "solid-js";
import {createAsync, query} from "@solidjs/router";
import {useApi, useAppState, useTranslator} from "@/app-context/AppContext";
import BookingCard from "@components/cards/BookingCard";
import VehicleGridSkeleton from "@components/skeletons/VehicleGridSkeleton";
import Pagination from "@components/Pagination";
import {Title} from "@solidjs/meta";
import type {Booking, Client, Driver, Vehicle, Zone} from "@/api/types";
import {BookingState} from "@/api/types";
import {API_ROOT} from "@/app-context/api";

type BookingWithTarget = {
    booking: Booking;
    targetUser: Client | Driver;
    vehicle: Vehicle;
    originZone: Zone;
    destinationZone: Zone;
};

const BOOKING_TABS = [
    BookingState.Pending, BookingState.Accepted, BookingState.Rejected, BookingState.Finished, BookingState.Canceled
] as const;

const bookingsQuery = query(async (state: BookingState): Promise<BookingWithTarget[]> => {
    const api = useApi();
    const [appState] = useAppState();

    const isDriver = appState.user!.type === "driver";

    const bookings = await api.getBookings(appState.user!.id, state, 1);

    return Promise.all(
        bookings.map(async (booking) => {
            const targetUser = await (isDriver ? api.getClientByUri(booking.client) : api.getDriverByUri(booking.driver));
            const vehicle = await api.getVehicleByUri(booking.vehicle)
            const originZone = await api.getZoneByUri(booking.originZone)
            const destinationZone = await api.getZoneByUri(booking.destinationZone)

            if (!targetUser || !vehicle || !originZone || !destinationZone) throw new Error("BookingUser, vehicle, originZone and destinationZone should never be null");

            return {booking, targetUser, vehicle, originZone, destinationZone};
        })
    );
}, "bookingsInfo");

export default function Bookings() {
    const [appState] = useAppState();
    const isDriver = appState.user!.type === "driver";
    const t = useTranslator()

    const [selectedState, setSelectedState] = createSignal<BookingState>(BookingState.Pending);

    const bookingItems = createAsync(async () => {
        const state = selectedState();
        return bookingsQuery(state);
    });

    return (
        <>
            <Title>Bookings - Van N&apos; Go</Title>

            <section class="mx-auto w-full max-w-7xl px-4 py-8">
                <div class="mb-6 flex items-center justify-between">
                    <h2 class="text-2xl font-bold text-on-surface-strong dark:text-on-surface-dark-strong">
                        {t("global.page.bookings.myBookings")}
                    </h2>
                </div>

                <div class="flex gap-2 overflow-x-auto border-b border-outline dark:border-outline-dark"
                     role="tablist">
                    <For each={BOOKING_TABS}>
                        {(tab) => {
                            const active = () => selectedState() === tab;

                            return (
                                <button
                                    type="button"
                                    role="tab"
                                    aria-selected={active()}
                                    tabindex={active() ? 0 : -1}
                                    onClick={() => setSelectedState(tab)}
                                    class={`h-min px-4 py-2 text-sm transition
                    ${
                                        active()
                                            ? "font-bold text-primary border-b-2 border-primary dark:border-primary-dark dark:text-primary-dark"
                                            : `text-on-surface font-medium hover:border-b-2 hover:border-outline-strong hover:text-on-surface-strong dark:text-on-surface-dark
                                               dark:hover:border-b-2 dark:hover:border-outline-dark-strong
                                               dark:hover:text-on-surface-dark-strong`
                                    }`}
                                >
                                    {t(`global.page.bookings.${tab.toLowerCase()}` as any)}
                                </button>
                            );
                        }}
                    </For>
                </div>

                <Suspense fallback={<VehicleGridSkeleton/>}>
                    <div class="mt-6 grid gap-6 grid-cols-1 sm:grid-cols-2 xl:grid-cols-3">
                        <For each={bookingItems()}>
                            {({booking, targetUser, vehicle, originZone, destinationZone}) =>
                                isDriver ? (
                                    <BookingCard
                                        booking={booking}
                                        user={targetUser as Client}
                                        userType="client"
                                        vehicle={vehicle}
                                        originZone={originZone}
                                        destinationZone={destinationZone}
                                    />
                                ) : (
                                    <BookingCard
                                        booking={booking}
                                        user={targetUser as Driver}
                                        userType={"driver"}
                                        vehicle={vehicle}
                                        originZone={originZone}
                                        destinationZone={destinationZone}
                                    />
                                )
                            }
                        </For>
                    </div>
                </Suspense>
                <Pagination totalPages={10} currentPage={1} baseHref={`${API_ROOT}bookings`}/>
            </section>
        </>
    );
}