import {JSX} from "solid-js";
import {Booking, BookingState} from "@/api/types";
import {useApi, useTranslator} from "@/app-context/AppContext.tsx";

export type UserInBookingRole = "driver" | "client";

export type BookingActionContext = {
    api: ReturnType<typeof useApi>
    t: ReturnType<typeof useTranslator>;
    closeCurrentModal: () => void;

    proofOfPaymentExists: boolean;
    reviewExists: boolean;

    openUploadProofModal: () => void;
    openViewProofModal: () => void;
    openUploadReviewModal: () => void;
    openViewReviewModal: () => void;
    refreshBookings: (newStateTab: BookingState) => void;
};

export type BookingActionRenderer = (booking: Booking, ctx: BookingActionContext) => JSX.Element;

const PrimaryButton = (props: JSX.ButtonHTMLAttributes<HTMLButtonElement>) => (
    <button
        {...props}
        class={`w-full rounded-radius bg-primary px-4 py-2 text-sm font-medium text-on-primary
            transition hover:opacity-80
            dark:bg-primary-dark dark:text-on-primary-dark
            ${props.class ?? ""}`}
    />
);

const SecondaryButton = (props: JSX.ButtonHTMLAttributes<HTMLButtonElement>) => (
    <button
        {...props}
        class={`w-full rounded-radius border border-outline bg-surface px-4 py-2
            text-sm font-medium text-on-surface transition
            hover:bg-surface-alt
            dark:border-outline-dark dark:bg-surface-dark
            dark:text-on-surface-dark dark:hover:bg-surface-dark-alt
            ${props.class ?? ""}`}
    />
);

const InfoText = (props: { children: JSX.Element }) => (
    <span class="col-span-full text-center text-sm text-on-surface-weak dark:text-on-surface-dark-weak">
    {props.children}
  </span>
);

export const bookingActions: Record<
    UserInBookingRole,
    Partial<Record<BookingState, BookingActionRenderer>>
> = {
    driver: {
        PENDING: (_, {t}) =>
            <InfoText>{t("global.page.bookings.waitingReply")}</InfoText>,

        ACCEPTED: (booking, {api, t, closeCurrentModal, openViewProofModal, openUploadProofModal, refreshBookings, proofOfPaymentExists}) =>
            proofOfPaymentExists ? (
                <PrimaryButton
                    onClick={() => {
                        openViewProofModal()
                    }}
                >
                    {t("global.page.bookings.viewProofOfPayment")}
                </PrimaryButton>
            ) : (
                <>
                    <SecondaryButton
                        onClick={() => {
                            api.updateBookingState(booking.id, BookingState.Canceled)
                            // TODO manage errors
                            refreshBookings(BookingState.Canceled)
                        }}
                    >
                        {t("global.page.bookings.cancelBooking")}
                    </SecondaryButton>
                    <PrimaryButton
                        onClick={() => {
                            closeCurrentModal()
                            openUploadProofModal()
                        }}
                    >
                        {t("global.page.bookings.uploadProofOfPayment")}
                    </PrimaryButton>
                </>
            ),

        FINISHED: (_, {t, closeCurrentModal, openViewProofModal, openViewReviewModal, openUploadReviewModal, proofOfPaymentExists, reviewExists}) => (
            <>
                {proofOfPaymentExists && (
                    <SecondaryButton
                        onClick={() => {
                            closeCurrentModal()
                            openViewProofModal()
                        }}
                    >
                        {t("global.page.bookings.viewProofOfPayment")}
                    </SecondaryButton>
                )}
                {reviewExists ? (
                    <PrimaryButton
                        onClick={() => {
                            closeCurrentModal()
                            openViewReviewModal()
                        }}
                    >
                        {t("global.page.bookings.viewReview")}
                    </PrimaryButton>
                ) : (
                    <PrimaryButton
                        onClick={() => {
                            closeCurrentModal()
                            openUploadReviewModal()
                        }}
                    >
                        {t("global.page.bookings.leaveReview")}
                    </PrimaryButton>
                )}
            </>
        ),

        REJECTED: (_, {t}) => (
            <InfoText>{t("global.page.bookings.rejectedBookingByDriver")}</InfoText>
        ),

        CANCELED: (_, {t}) =>
            <InfoText>{t("global.page.bookings.canceledBooking")}</InfoText>,
    },

    client: {
        PENDING: (booking, {api, t, refreshBookings}) => (
            <>
                <PrimaryButton
                    onClick={() => {
                        api.updateBookingState(booking.id, BookingState.Accepted)
                        // TODO manage errors
                        refreshBookings(BookingState.Accepted)
                    }}
                >
                    {t("global.page.bookings.acceptBooking")}
                </PrimaryButton>
                <SecondaryButton
                    onClick={() => {
                        api.updateBookingState(booking.id, BookingState.Rejected)
                        // TODO manage errors
                        refreshBookings(BookingState.Rejected)
                    }}
                >
                    {t("global.page.bookings.rejectBooking")}
                </SecondaryButton>
            </>
        ),

        ACCEPTED: (booking,{api, t, closeCurrentModal, openViewProofModal, refreshBookings, proofOfPaymentExists}) =>
            proofOfPaymentExists ? (
                booking.date <= new Intl.DateTimeFormat("en-CA", {
                    year: "numeric", month: "2-digit", day: "2-digit"
                }).format(new Date()) ? (
                    <>
                        <SecondaryButton
                            onClick={() => {
                                closeCurrentModal()
                                openViewProofModal()
                            }}
                        >
                            {t("global.page.bookings.viewProofOfPayment")}
                        </SecondaryButton>
                        <PrimaryButton
                            onClick={() => {
                                api.updateBookingState(booking.id, BookingState.Finished)
                                // TODO manage errors
                                refreshBookings(BookingState.Finished)
                            }}
                        >
                            {t("global.page.bookings.finishBooking")}
                        </PrimaryButton>
                    </>
                ) : (
                    <SecondaryButton
                        onClick={() => {
                            closeCurrentModal()
                            openViewProofModal()
                        }}
                    >
                        {t("global.page.bookings.viewProofOfPayment")}
                    </SecondaryButton>
                )
            ) : (
                <SecondaryButton
                    onClick={() => {
                        api.updateBookingState(booking.id, BookingState.Canceled)
                        // TODO manage errors
                        refreshBookings(BookingState.Canceled)
                    }}
                >
                    {t("global.page.bookings.cancelBooking")}
                </SecondaryButton>
            ),

        FINISHED: (_, {t, closeCurrentModal, openViewProofModal, openViewReviewModal, proofOfPaymentExists, reviewExists}) => (
            <>
                {proofOfPaymentExists && (
                    <SecondaryButton
                        onClick={() => {
                            closeCurrentModal()
                            openViewProofModal()
                        }}
                    >
                        {t("global.page.bookings.viewProofOfPayment")}
                    </SecondaryButton>
                )}
                {reviewExists && (
                    <SecondaryButton
                        onClick={() => {
                            closeCurrentModal()
                            openViewReviewModal()
                        }}
                    >
                        {t("global.page.bookings.viewReview")}
                    </SecondaryButton>
                )}
            </>
        ),

        REJECTED: (_, {t}) => (
            <InfoText>{t("global.page.bookings.rejectedBooking")}</InfoText>
        ),

        CANCELED: (_, {t}) =>
            <InfoText>{t("global.page.bookings.canceledBooking")}</InfoText>,
    },
};