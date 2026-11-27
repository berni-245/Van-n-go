import { JSX } from "solid-js";
import { action } from "@solidjs/router";
import { Booking, BookingState } from "@/api/types";
import { useTranslator } from "@/app-context/AppContext.tsx";
import { Api } from "@/app-context/api.ts";

export type UserInBookingRole = "driver" | "client";

export type BookingActionContext = {
  t: ReturnType<typeof useTranslator>;
  closeCurrentModal: () => void;

  proofOfPaymentExists: boolean;
  reviewExists: boolean;

  openUploadProofModal: () => void;
  openViewProofModal: () => void;
  openUploadReviewModal: () => void;
  openViewReviewModal: () => void;
  transitionBooking: (booking: Booking, newState: BookingState) => void;
};

export type BookingActionRenderer = (booking: Booking, ctx: BookingActionContext) => JSX.Element;

// Mutating booking state through an action lets Solid Router track the request and
// automatically revalidate the cached bookings queries on success — so the moved booking
// shows up in its new tab without a manual refresh.
export const updateBookingStateAction = action(
  async (api: Api, bookingId: number, newState: BookingState, t: any, toaster: any) => {
    const res = await api.updateBookingState(bookingId, newState);
    if (!res.ok) {
      toaster.notify(t("global.error.unknown"), { type: "error" });
      return { ok: false as const };
    }
    return { ok: true as const };
  },
  "update-booking-state"
);

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

// A booking can only be marked "finished" once its scheduled date has arrived (today counts).
// `bookingDate` is the API's "YYYY-MM-DD" string; `now` defaults to the real current time but
// is overridable for testing.
export function canFinishBooking(bookingDate: string, now: Date = new Date()): boolean {
  const today = new Intl.DateTimeFormat("en-CA", { year: "numeric", month: "2-digit", day: "2-digit" }).format(now);
  return bookingDate <= today;
}

export const bookingActions: Record<UserInBookingRole, Partial<Record<BookingState, BookingActionRenderer>>> = {
  driver: {
    PENDING: (_, { t }) => <InfoText>{t("global.page.bookings.waitingReply")}</InfoText>,

    ACCEPTED: (
      booking,
      { t, closeCurrentModal, openViewProofModal, openUploadProofModal, transitionBooking, proofOfPaymentExists }
    ) =>
      proofOfPaymentExists ? (
        <PrimaryButton
          onClick={() => {
            openViewProofModal();
          }}
        >
          {t("global.page.bookings.viewProofOfPayment")}
        </PrimaryButton>
      ) : (
        <>
          <SecondaryButton onClick={() => transitionBooking(booking, BookingState.Canceled)}>
            {t("global.page.bookings.cancelBooking")}
          </SecondaryButton>
          <PrimaryButton
            onClick={() => {
              closeCurrentModal();
              openUploadProofModal();
            }}
          >
            {t("global.page.bookings.uploadProofOfPayment")}
          </PrimaryButton>
        </>
      ),

    FINISHED: (
      _,
      {
        t,
        closeCurrentModal,
        openViewProofModal,
        openViewReviewModal,
        openUploadReviewModal,
        proofOfPaymentExists,
        reviewExists,
      }
    ) => (
      <>
        {proofOfPaymentExists && (
          <SecondaryButton
            onClick={() => {
              closeCurrentModal();
              openViewProofModal();
            }}
          >
            {t("global.page.bookings.viewProofOfPayment")}
          </SecondaryButton>
        )}
        {reviewExists ? (
          <PrimaryButton
            onClick={() => {
              closeCurrentModal();
              openViewReviewModal();
            }}
          >
            {t("global.page.bookings.viewReview")}
          </PrimaryButton>
        ) : (
          <PrimaryButton
            onClick={() => {
              closeCurrentModal();
              openUploadReviewModal();
            }}
          >
            {t("global.page.bookings.leaveReview")}
          </PrimaryButton>
        )}
      </>
    ),

    REJECTED: (_, { t }) => <InfoText>{t("global.page.bookings.rejectedBookingByDriver")}</InfoText>,

    CANCELED: (_, { t }) => <InfoText>{t("global.page.bookings.canceledBooking")}</InfoText>,
  },

  client: {
    PENDING: (booking, { t, transitionBooking }) => (
      <>
        <PrimaryButton onClick={() => transitionBooking(booking, BookingState.Accepted)}>
          {t("global.page.bookings.acceptBooking")}
        </PrimaryButton>
        <SecondaryButton onClick={() => transitionBooking(booking, BookingState.Rejected)}>
          {t("global.page.bookings.rejectBooking")}
        </SecondaryButton>
      </>
    ),

    ACCEPTED: (booking, { t, closeCurrentModal, openViewProofModal, transitionBooking, proofOfPaymentExists }) =>
      proofOfPaymentExists ? (
        canFinishBooking(booking.date) ? (
          <>
            <SecondaryButton
              onClick={() => {
                closeCurrentModal();
                openViewProofModal();
              }}
            >
              {t("global.page.bookings.viewProofOfPayment")}
            </SecondaryButton>
            <PrimaryButton onClick={() => transitionBooking(booking, BookingState.Finished)}>
              {t("global.page.bookings.finishBooking")}
            </PrimaryButton>
          </>
        ) : (
          <SecondaryButton
            onClick={() => {
              closeCurrentModal();
              openViewProofModal();
            }}
          >
            {t("global.page.bookings.viewProofOfPayment")}
          </SecondaryButton>
        )
      ) : (
        <SecondaryButton onClick={() => transitionBooking(booking, BookingState.Canceled)}>
          {t("global.page.bookings.cancelBooking")}
        </SecondaryButton>
      ),

    FINISHED: (
      _,
      { t, closeCurrentModal, openViewProofModal, openViewReviewModal, proofOfPaymentExists, reviewExists }
    ) => (
      <>
        {proofOfPaymentExists && (
          <SecondaryButton
            onClick={() => {
              closeCurrentModal();
              openViewProofModal();
            }}
          >
            {t("global.page.bookings.viewProofOfPayment")}
          </SecondaryButton>
        )}
        {reviewExists && (
          <SecondaryButton
            onClick={() => {
              closeCurrentModal();
              openViewReviewModal();
            }}
          >
            {t("global.page.bookings.viewReview")}
          </SecondaryButton>
        )}
      </>
    ),

    REJECTED: (_, { t }) => <InfoText>{t("global.page.bookings.rejectedBooking")}</InfoText>,

    CANCELED: (_, { t }) => <InfoText>{t("global.page.bookings.canceledBooking")}</InfoText>,
  },
};
