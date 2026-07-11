import { createComputed, createEffect, For, Show, Suspense } from "solid-js";
import { query, useNavigate, useSearchParams } from "@solidjs/router";
import { useApi, useAppState, useTranslator } from "@/app-context/AppContext";
import BookingCard from "@components/cards/BookingCard";
import VehicleGridSkeleton from "@components/skeletons/VehicleGridSkeleton";
import Pagination from "@components/pagination/Pagination";
import { Title } from "@solidjs/meta";
import type { Booking, Client, Driver, Vehicle, Zone } from "@/api/types";
import { BookingState } from "@/api/types";
import { PaginatedResource } from "../app-context/pagination";
import { createStore } from "solid-js/store";
import BookingCardSkeletonGrid from "../components/skeletons/BookingCardSkeletonGrid";

type BookingWithTarget = {
  booking: Booking;
  targetUser: Client | Driver;
  vehicle: Vehicle;
  originZone: Zone;
  destinationZone: Zone;
};

const BOOKING_TABS = [
  BookingState.Pending,
  BookingState.Accepted,
  BookingState.Finished,
  BookingState.Rejected,
  BookingState.Canceled,
] as const;

/** Icon and subtitle vary by booking state to give the empty state more context. */
const EMPTY_STATE_ICON: Record<BookingState, string> = {
  [BookingState.Pending]: "📭",
  [BookingState.Accepted]: "📅",
  [BookingState.Finished]: "🏁",
  [BookingState.Rejected]: "🚫",
  [BookingState.Canceled]: "↩️",
};

const bookingsQuery = query(
  async (state: BookingState, page: number): Promise<PaginatedResource<BookingWithTarget>> => {
    const api = useApi();
    const [appState] = useAppState();

    const isDriver = appState.user!.type === "driver";

    const bookings = await api.getBookings(appState.user!.id, state, page);

    const data = await Promise.all(
      bookings.data.map(async (booking) => {
        const [targetUser, vehicle, originZone, destinationZone] = await Promise.all([
          isDriver ? api.getClientByUri(booking.client) : api.getDriverByUri(booking.driver),
          api.getVehicleByUri(booking.vehicle),
          api.getZoneByUri(booking.originZone),
          api.getZoneByUri(booking.destinationZone),
        ]);

        if (!targetUser || !vehicle || !originZone || !destinationZone)
          throw new Error("BookingUser, vehicle, originZone and destinationZone should never be null");

        return { booking, targetUser, vehicle, originZone, destinationZone };
      })
    );
    return { data, pages: bookings.pages };
  },
  "bookingsInfo"
);

type StatePages = Record<BookingState, number>;

export default function Bookings() {
  const [appState] = useAppState();
  const oppositeUserType = appState.user!.type === "driver" ? "client" : "driver";
  const t = useTranslator();

  const [params, setParams] = useSearchParams();
  const navigate = useNavigate();

  createComputed(() => {
    if (
      !params.state ||
      typeof params.state !== "string" ||
      !Object.values(BookingState).includes(params.state as BookingState)
    ) {
      navigate("?state=PENDING&page=1", { replace: true });
    }

    const page = Number(params.page);
    if (!Number.isInteger(page) || page < 0) navigate(`?state=${params.state}&page=1`, { replace: true });
  });

  const state = () => params.state as BookingState;
  const page = () => Number(params.page || "1");

  const [statePages, setStatePages] = createStore<StatePages>({
    ACCEPTED: 1,
    CANCELED: 1,
    FINISHED: 1,
    PENDING: 1,
    REJECTED: 1,
  });
  createEffect(() => {
    setStatePages(state(), page());
  });

  const activeState = (bookingState: BookingState) => state() === bookingState;

  return (
    <div>
      <Title>Bookings - Van N&apos; Go</Title>
      <section class="mx-auto w-full max-w-7xl px-4 py-8 flex flex-col min-h-full">
        <div class="mb-6 flex items-center justify-between">
          <h2 class="text-2xl font-bold text-on-surface-strong dark:text-on-surface-dark-strong">
            {t("global.page.bookings.myBookings")}
          </h2>
        </div>

        {/* ── Tab bar ── */}
        <div class="flex gap-2 overflow-x-auto border-b border-outline dark:border-outline-dark" role="tablist">
          <For each={BOOKING_TABS}>
            {(tabState) => (
              <button
                onClick={() => setParams({ state: tabState, page: statePages[tabState] })}
                class="h-min px-4 py-2 text-sm transition"
                classList={{
                  "font-bold text-primary border-b-2 border-primary dark:border-primary-dark dark:text-primary-dark":
                    activeState(tabState),
                  "text-on-surface font-medium hover:border-b-2 hover:border-outline-strong hover:text-on-surface-strong dark:text-on-surface-dark dark:hover:border-b-2 dark:hover:border-outline-dark-strong dark:hover:text-on-surface-dark-strong":
                    !activeState(tabState),
                }}
              >
                {t(`global.page.bookings.${tabState.toLowerCase()}` as any)}
              </button>
            )}
          </For>
        </div>

        {/* ── Results ── */}
        <Suspense fallback={<BookingCardSkeletonGrid class="mt-6" />}>
          <Pagination fetcher={(page) => bookingsQuery(state(), page)}>
            {(bookingItemsPage) => (
              <Show
                when={bookingItemsPage && bookingItemsPage.length > 0}
                fallback={
                  <div class="flex flex-col items-center justify-center gap-3 py-24 text-center">
                    <span class="text-5xl">{EMPTY_STATE_ICON[state()]}</span>
                    <p class="text-lg font-semibold text-on-surface-strong dark:text-on-surface-dark-strong">
                      {t(`global.page.bookings.empty.${state().toLowerCase()}.title` as any)}
                    </p>
                    <p class="max-w-sm text-sm text-on-surface dark:text-on-surface-dark">
                      {t(`global.page.bookings.empty.${state().toLowerCase()}.subtitle` as any)}
                    </p>
                  </div>
                }
              >
                <div class="mt-6 grid gap-6 grid-cols-1 sm:grid-cols-2 xl:grid-cols-3">
                  <For each={bookingItemsPage}>
                    {({ booking, targetUser, vehicle, originZone, destinationZone }) => (
                      <BookingCard
                        booking={booking}
                        cardUser={targetUser}
                        cardUserType={oppositeUserType}
                        vehicle={vehicle}
                        originZone={originZone}
                        destinationZone={destinationZone}
                      />
                    )}
                  </For>
                </div>
              </Show>
            )}
          </Pagination>
        </Suspense>
      </section>
    </div>
  );
}
