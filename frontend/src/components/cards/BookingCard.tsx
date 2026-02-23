import {Component, createSignal, onCleanup, onMount} from "solid-js";
import {BsChatDots, BsJournalText} from "solid-icons/bs";
import {useApi, useAppState, useTranslator} from "@/app-context/AppContext";
import {Booking, BookingState, Client, Driver, Vehicle, Zone} from "@/api/types";
import ImageWithFallback from "../ImageWithFallback";
import {useNavigate} from "@solidjs/router";
import {BookingModal} from "@components/modal/BookingModal";
import {ReviewBookingModal} from "@components/modal/ReviewModal";
import {BookingActionContext, bookingActions} from "@components/bookings/BookingActions";

type CardProps = {
    booking: Booking;
    cardUser: Driver | Client;
    cardUserType: "driver" | "client";
    vehicle: Vehicle;
    originZone: Zone;
    destinationZone: Zone;
};

const BookingCard: Component<CardProps> = (props) => {
    const t = useTranslator();
    const api = useApi();
    const [appState] = useAppState();
    const navigate = useNavigate();
    const [modalIsOpen, setModalIsOpen] = createSignal(false);
    const [reviewOpen, setReviewOpen] = createSignal(false);

    const handleKeyDown = (e: KeyboardEvent) => {
        if (e.key === "Escape") setModalIsOpen(false);
    };

    onMount(() => {
        document.addEventListener("keydown", handleKeyDown);
        onCleanup(() => document.removeEventListener("keydown", handleKeyDown));
    });

    function formatLocalDate(date: string, locale: string) {
        return new Intl.DateTimeFormat(locale, {
            day: "2-digit",
            month: "short",
            year: "numeric"
        }).format(new Date(date));
    }

    function renderModalFooter() {
        const ctx: BookingActionContext = {
            api,
            t,
            closeCurrentModal: () => {
                setModalIsOpen(false)
                setReviewOpen(false)
            },

            openViewProofModal: () => {

            },

            openUploadProofModal: () => {

            },

            openViewReviewModal: () => {

            },

            openUploadReviewModal: () => {
                ctx.closeCurrentModal()
                setReviewOpen(true)
            },

            refreshBookings: (newStateTab: BookingState) => {
                ctx.closeCurrentModal()
                navigate(`?state=${newStateTab}&page=1`, {replace: true})
            }
        };

        const action =
            bookingActions[props.cardUserType]?.[
                props.booking.state as keyof typeof bookingActions[typeof props.cardUserType]
                ];

        return action ? action(props.booking, ctx) : null;
    }

    const handleChat = () => {
        const isDriver = appState.user!.type === "driver";
        const driverId = isDriver ? appState.user!.id : props.cardUser.id;
        const clientId = isDriver ? props.cardUser.id : appState.user!.id;
        navigate(`/${appState.user!.type}/chat?driverId=${driverId}&clientId=${clientId}`);
    };

    return (
        <>
            <article
                class="group max-w-sm overflow-hidden rounded-radius border border-outline bg-surface-alt text-on-surface shadow-sm transition hover:shadow-md dark:border-outline-dark dark:bg-surface-dark-alt dark:text-on-surface-dark">
                <div class="grid grid-cols-[1fr_auto] gap-4 p-4 items-center">
                    <div class="flex flex-col gap-1">
                  <span class="text-m text-on-surface-weak dark:text-on-surface-dark-weak">
                    {t(
                        `global.page.bookings.${props.booking.shiftPeriod.toLowerCase()}DateTime` as any,
                        {date: formatLocalDate(props.booking.date, appState.preferences.locale)}
                    )}
                  </span>
                        <span class="text-sm font-semibold">
                    {props.cardUser.username}
                  </span>
                    </div>

                    <div class="h-16 w-16 overflow-hidden rounded-full border border-outline dark:border-outline-dark">
                        <ImageWithFallback
                            class="h-full w-full object-cover transition group-hover:scale-105"
                            alt={t("global.sr.vehiclePicture")}
                            src={props.cardUser.profilePic}
                            fallbackSrc="/images/blank-user.jpg"
                            skipSrc={!props.cardUser.hasProfilePic}
                        />
                    </div>
                </div>


                <div class="px-4 pb-4">
                    <div class="rounded-radius bg-surface px-3 py-2 text-center dark:bg-surface-dark">
                  <span class="text-base font-semibold">
                    {t("global.page.bookings.travelZones", {
                        originNeighborhood: props.originZone.neighborhood.name,
                        destinationNeighborhood: props.destinationZone.neighborhood.name
                    })}
                  </span>
                    </div>
                </div>

                <div class="grid grid-cols-2 gap-3 border-t border-outline p-4 dark:border-outline-dark">
                    <button type="button"
                            onClick={() => setModalIsOpen(true)}
                            class="inline-flex items-center justify-center gap-2 rounded-radius bg-primary px-4 py-3 text-sm font-medium text-on-primary transition hover:opacity-80 dark:bg-primary-dark dark:text-on-primary-dark"
                    >
                        <BsJournalText class="text-lg"/>
                        <span class="hidden sm:inline">
              {t("global.page.bookings.details")}
            </span>
                    </button>

                    <button
                        type="button"
                        onClick={handleChat}
                        class="inline-flex items-center justify-center gap-2 rounded-radius bg-primary px-4 py-3 text-sm font-medium text-on-primary transition hover:opacity-80 dark:bg-primary-dark dark:text-on-primary-dark"
                    >
                        <BsChatDots class="text-lg"/>
                        <span class="hidden sm:inline">
              {t("global.page.bookings.chat")}
            </span>
                    </button>
                </div>
            </article>

            <BookingModal
                booking={props.booking}
                targetUser={props.cardUser}
                targetUserType={props.cardUserType}
                vehicle={props.vehicle}
                originZone={props.originZone}
                destinationZone={props.destinationZone}
                title={t(
                    `global.page.bookings.${props.booking.shiftPeriod.toLowerCase()}DateTime` as any,
                    {date: formatLocalDate(props.booking.date, appState.preferences.locale)}
                )}
                open={modalIsOpen()}
                onClose={() => setModalIsOpen(false)}
            >
                <div class="flex justify-end gap-2">
                    {renderModalFooter()}
                </div>
            </BookingModal>

            <ReviewBookingModal
                bookingId={props.booking.id}
                open={reviewOpen()}
                onClose={() => setReviewOpen(false)}
            />

        </>
    );
};

export default BookingCard;