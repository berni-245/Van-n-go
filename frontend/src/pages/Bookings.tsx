import {For, Suspense} from "solid-js";
import {createAsync, query} from "@solidjs/router";
import {useApi, useAppState} from "@/app-context/AppContext";
import BookingCardDriver from "@components/cards/BookingCardDriver";
import BookingCardClient from "@components/cards/BookingCardClient";
import VehicleGridSkeleton from "@components/skeletons/VehicleGridSkeleton";
import Pagination from "@components/Pagination";
import {Title} from "@solidjs/meta";
import type {Booking, Client, Driver} from "@/api/types";
import {BookingState} from "@/api/types";
import {API_ROOT} from "@/app-context/api";

type BookingWithTarget = { booking: Booking; targetUser: Client | Driver }

const bookingsQuery = query(async (): Promise<BookingWithTarget[]> => {
    const api = useApi();
    const [appState] = useAppState();

    const isDriver = appState.user!.type === "driver";

    const bookings = await api.getBookings(appState.user!.id, BookingState.Rejected, 1);

    return Promise.all(
        bookings.map(async (booking) => {
            const targetUser = await (isDriver ? api.getClientFromURI(booking.client) : api.getDriverFromURI(booking.driver));
            if (!targetUser) throw new Error("Target user should never be null");

            return {booking, targetUser};
        })
    );
}, "bookingsInfo");

export default function Bookings() {
    const [appState] = useAppState();
    const isDriver = appState.user!.type === "driver";

    const bookingItems = createAsync(() => bookingsQuery());

    return (
        <>
            <Title>Bookings - Van N' Go</Title>

            <section class="mx-auto w-full max-w-7xl px-4 py-8">
                <div class="mb-6 flex items-center justify-between">
                    <h2 class="text-2xl font-bold text-on-surface-strong dark:text-on-surface-dark-strong">
                        My bookings
                    </h2>

                    <Pagination totalPages={10} currentPage={1}
                                baseHref={`${API_ROOT}bookings?state=${BookingState.Pending}`}/>
                </div>

                <Suspense fallback={<VehicleGridSkeleton/>}>
                    <div class="grid gap-6 grid-cols-1 sm:grid-cols-2 xl:grid-cols-3">
                        <For each={bookingItems()}>
                            {({booking, targetUser}) =>
                                isDriver ?
                                    <BookingCardClient booking={booking} client={targetUser as Client}/>
                                    :
                                    <BookingCardDriver booking={booking} driver={targetUser as Driver}/>
                            }
                        </For>
                    </div>
                </Suspense>
            </section>
        </>
    );
}