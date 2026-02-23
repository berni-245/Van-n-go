import {JSX} from "solid-js";
import {Booking} from "@/api/types";
import {useTranslator} from "@/app-context/AppContext.tsx";

export type UserInBookingRole = "driver" | "client";
export type BookingState =
    | "PENDING"
    | "ACCEPTED"
    | "FINISHED"
    | "REJECTED"
    | "CANCELED";

export type BookingActionRenderer = (booking: Booking) => JSX.Element;

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

const t = useTranslator()

export const bookingActions: Record<
    UserInBookingRole,
    Partial<Record<BookingState, BookingActionRenderer>>
> = {
    driver: {
        PENDING: () => <InfoText>{t("global.page.bookings.waitingReply")}</InfoText>,

        ACCEPTED: (booking) =>
            booking.proofOfPayment ? (
                <PrimaryButton>{t("global.page.bookings.viewProofOfPayment")}</PrimaryButton>
            ) : (
                <>
                    <SecondaryButton>{t("global.page.bookings.cancelBooking")}</SecondaryButton>
                    <PrimaryButton>{t("global.page.bookings.uploadProofOfPayment")}</PrimaryButton>
                </>
            ),

        FINISHED: (booking) => (
            booking.review ? (
                <>
                    <SecondaryButton>{t("global.page.bookings.viewProofOfPayment")}</SecondaryButton>
                    <PrimaryButton>{t("global.page.bookings.viewReview")}</PrimaryButton>
                </>
                ) : (
            <>
                <SecondaryButton>{t("global.page.bookings.viewProofOfPayment")}</SecondaryButton>
                <PrimaryButton>{t("global.page.bookings.leaveReview")}</PrimaryButton>
            </>
            )
        ),

        REJECTED: () => (
            <InfoText>{t("global.page.bookings.rejectedBookingByDriver")}</InfoText>
        ),

        CANCELED: () => <InfoText>{t("global.page.bookings.canceledBooking")}</InfoText>,
    },

    client: {
        PENDING: () => (
            <>
                <PrimaryButton>{t("global.page.bookings.acceptBooking")}</PrimaryButton>
                <SecondaryButton>{t("global.page.bookings.rejectBooking")}</SecondaryButton>
            </>
        ),

        ACCEPTED: (booking) =>
            booking.hasProofOfPayment ? (
                new Date(booking.date) <= new Date(Date.now()) ? (
                    <>
                        <SecondaryButton>{t("global.page.bookings.viewProofOfPayment")}</SecondaryButton>
                        <PrimaryButton>{t("global.page.bookings.finishBooking")}</PrimaryButton>
                    </>
                ) : (
                    <SecondaryButton>{t("global.page.bookings.viewProofOfPayment")}</SecondaryButton>
                )
            ) : (
                <SecondaryButton>{t("global.page.bookings.cancelBooking")}</SecondaryButton>
            ),

        FINISHED: (booking) => (
            booking.review ? (  // todo cambiar por hasReview, necesita cambiar en bookingDTO también
            <>
                <SecondaryButton>{t("global.page.bookings.viewProofOfPayment")}</SecondaryButton>
                <SecondaryButton>{t("global.page.bookings.viewReview")}</SecondaryButton>
            </>
            ) : (
                <SecondaryButton>{t("global.page.bookings.viewProofOfPayment")}</SecondaryButton>
            )
        ),

        REJECTED: () => (
            <InfoText>{t("global.page.bookings.rejectedBooking")}</InfoText>
        ),

        CANCELED: () => <InfoText>{t("global.page.bookings.canceledBooking")}</InfoText>,
    },
};