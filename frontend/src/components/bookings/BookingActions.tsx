import {JSX} from "solid-js";
import {Booking} from "@/api/types";

export type BookingRole = "driver" | "client";
export type BookingState =
    | "PENDING"
    | "ACCEPTED"
    | "FINISHED"
    | "REJECTED"
    | "CANCELED";

export type BookingActionRenderer = (booking: Booking) => JSX.Element;

/**
 * Helpers visuales reutilizables
 */
const PrimaryButton = (props: JSX.ButtonHTMLAttributes<HTMLButtonElement>) => (
    <button
        {...props}
        class={`rounded-radius bg-primary px-4 py-2 text-sm font-medium text-on-primary
            hover:opacity-80 transition
            dark:bg-primary-dark dark:text-on-primary-dark ${props.class ?? ""}`}
    />
);

const SecondaryButton = (props: JSX.ButtonHTMLAttributes<HTMLButtonElement>) => (
    <button
        {...props}
        class={`rounded-radius border border-outline bg-surface px-4 py-2 text-sm font-medium text-on-surface transition 
                hover:bg-surface-alt dark:border-outline-dark dark:bg-surface-dark dark:text-on-surface-dark dark:hover:bg-surface-dark-alt
                ${props.class ?? ""}`}
    />
);

const InfoText = (props: { children: JSX.Element }) => (
    <span class="text-sm text-on-surface-weak dark:text-on-surface-dark-weak">
        {props.children}
        </span>
);

/**
 * Acciones por rol + estado
 */
export const bookingActions: Record<
    BookingRole,
    Partial<Record<BookingState, BookingActionRenderer>>
> = {
    driver: {
        PENDING: () => <InfoText>Esperando respuesta del driver</InfoText>,

        ACCEPTED: (booking) =>
            booking.proofOfPayment ? (
                <PrimaryButton>Ver comprobante</PrimaryButton>
            ) : (
                <>
                    <SecondaryButton>Cancelar reserva</SecondaryButton>
                    <PrimaryButton>Subir comprobante</PrimaryButton>
                </>
            ),

        FINISHED: () => (
            <>
                <SecondaryButton>Ver comprobante</SecondaryButton>
                <PrimaryButton>Dejar reseña</PrimaryButton>
            </>
        ),

        REJECTED: () => (
            <InfoText>La reserva fue rechazada por el driver</InfoText>
        ),

        CANCELED: () => <InfoText>La reserva fue cancelada</InfoText>,
    },

    client: {
        PENDING: () => (
            <>
                <PrimaryButton>Aceptar reserva</PrimaryButton>
                <SecondaryButton>Rechazar reserva</SecondaryButton>
            </>
        ),

        ACCEPTED: (booking) =>
            booking.proofOfPayment ? (
                new Date(booking.date) <= new Date() ? (
                    <>
                        <SecondaryButton>Ver comprobante</SecondaryButton>
                        <PrimaryButton>Finalizar reserva</PrimaryButton>
                    </>
                ) : (
                    <SecondaryButton>Ver comprobante</SecondaryButton>
                )
            ) : (
                <SecondaryButton>Cancelar reserva</SecondaryButton>
            ),

        FINISHED: () => (
            <>
                <SecondaryButton>Ver comprobante</SecondaryButton>
                <SecondaryButton>Ver reseña</SecondaryButton>
            </>
        ),

        REJECTED: () => (
            <InfoText>La reserva fue rechazada por el driver</InfoText>
        ),

        CANCELED: () => <InfoText>La reserva fue cancelada</InfoText>,
    },
};