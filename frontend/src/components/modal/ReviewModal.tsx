import {createEffect, createResource, Show} from "solid-js";
import {action, useSubmission} from "@solidjs/router";
import {useApi, useTranslator} from "@/app-context/AppContext.tsx";
import {Api} from "@/app-context/api.ts";
import {ApiError} from "@/api/types.ts";
import {useToast} from "solid-notifications";
import Button from "@components/Button.tsx";
import Stars from "@components/inputs/Stars.tsx";

interface ReviewBookingModalProps {
    open: boolean;
    bookingId: number;
    onClose: () => void;
}

export const submitReviewAction = action(
    async (
        api: Api,
        bookingId: number,
        t: any,
        toaster: any,
        formData: FormData
    ) => {
        const ratingString = formData.get("rating");

        if (!ratingString) {
            return { ok: false, resp: { rating: [t("global.error.ratingRequired")] } };
        }

        const rating = Number(ratingString);
        const comments = formData.get("review") as string;

        const response = await api.postReview(bookingId, rating, comments);

        if (!response.ok) {
            if (response.status === 400) {
                const errors: ApiError[] = await response.json();
                const fieldErrors: Record<string, string[]> = {};

                errors.forEach(err => {
                    if (!err.description) return;

                    toaster.notify(err.description, { type: "error" });

                    if (err.entity) {
                        fieldErrors[err.entity] ??= [];
                        fieldErrors[err.entity].push(err.description);
                    }
                });

                return { ok: false, resp: fieldErrors };
            }

            toaster.notify(
                t(
                    response.status === 401
                        ? "global.error.unauthorizedReview"
                        : "global.error.generic"
                ),
                { type: "error" }
            );

            return { ok: false, resp: {} };
        }

        toaster.notify(t("global.review.success"), { type: "success" });
        return { ok: true };
    },
    "submit-review-action"
);

export function ReviewBookingModal(props: ReviewBookingModalProps) {
    const t = useTranslator();
    const api = useApi();
    const toaster = useToast();

    const submission = useSubmission(submitReviewAction);

    createEffect((wasPending) => {
        if (wasPending && !submission.pending && submission.result?.ok) {
            props.onClose();
        }
        return submission.pending;
    }, false);

    const errors = () =>
        submission.result?.ok === false ? submission.result.resp : {};

    const reviewForm = t("global.reviewForm") as any;

    return (
        <Show when={props.open}>
            <div
                class="fixed inset-0 z-40 flex items-end justify-center bg-black/20 p-4 pb-8 backdrop-blur-md sm:items-center"
                onClick={(e) => e.target === e.currentTarget && props.onClose()}
            >
                <div class="w-full max-w-lg overflow-hidden rounded-radius border border-outline bg-surface text-on-surface dark:border-outline-dark dark:bg-surface-dark dark:text-on-surface-dark">

                    <div class="flex items-center justify-between border-b border-outline bg-surface-alt/60 p-4 dark:border-outline-dark dark:bg-surface-dark/20">
                        <h4 class="font-semibold tracking-wide">
                            {reviewForm.title}
                        </h4>

                        <button onClick={props.onClose} aria-label="close">
                            ✕
                        </button>
                    </div>

                    <form
                        action={submitReviewAction.with(
                            api,
                            props.bookingId,
                            t,
                            toaster
                        )}
                        method="post"
                        class="flex flex-col gap-6 px-5 py-6"
                    >
                        <Stars
                            name="rating"
                            label={reviewForm.title}
                            error={errors()?.rating}
                        />

                        <div class="flex flex-col gap-1">
                            <label for="review" class="text-sm">
                                {reviewForm.yourComment}
                            </label>

                            <textarea
                                id="review"
                                name="review"
                                rows={4}
                                maxLength={255}
                                placeholder={reviewForm.ratingPlaceholder}
                                disabled={submission.pending}
                                class="resize-none rounded-radius border bg-surface-alt px-4 py-2 text-sm
                       focus-visible:outline-primary disabled:opacity-70
                       dark:bg-surface-dark-alt"
                                classList={{
                                    "border-danger": !!errors()?.["review"],
                                    "border-outline dark:border-outline-dark": !errors()?.["review"]
                                }}
                            />

                            <Show when={errors()?.["review"]}>
                                <small class="text-danger">
                                    • {errors()?.["review"]?.[0]}
                                </small>
                            </Show>
                        </div>

                        <div class="grid grid-cols-2 gap-3 border-t border-outline pt-4 dark:border-outline-dark">
                            <Button
                                type="button"
                                category="secondary"
                                text={t("global.generic.word.cancel")}
                                onClick={props.onClose}
                                disabled={submission.pending}
                            />

                            <Button
                                type="submit"
                                category="primary"
                                text={submission.pending ? "…" : t("global.generic.word.send")}
                                disabled={submission.pending}
                            />
                        </div>
                    </form>
                </div>
            </div>
        </Show>
    );
}

export function ViewReviewModal(props: ReviewBookingModalProps) {
    const api = useApi();
    const t = useTranslator();

    const [review] = createResource(
        () => (props.open ? props.bookingId : false),
        (id) => api.getReview(id)
    );

    return (
        <Show when={props.open}>
            <div
                class="fixed inset-0 z-40 flex items-end justify-center bg-black/20 p-4 pb-8 backdrop-blur-md sm:items-center"
                onClick={(e) => e.target === e.currentTarget && props.onClose()}
            >
                <div class="w-full max-w-lg overflow-hidden rounded-radius border border-outline bg-surface text-on-surface dark:border-outline-dark dark:bg-surface-dark dark:text-on-surface-dark">
                    <div class="flex items-center justify-between border-b border-outline bg-surface-alt/60 p-4 dark:border-outline-dark dark:bg-surface-dark/20">
                        <h4 class="font-semibold tracking-wide">
                            {t("global.review.viewTitle")}
                        </h4>
                        <button onClick={props.onClose} aria-label="close">
                            ✕
                        </button>
                    </div>

                    <div class="flex flex-col gap-6 px-5 py-6">
                        <Show when={review.loading}>
                            <div class="animate-pulse py-4 text-center text-sm text-on-surface-weak dark:text-on-surface-dark-weak">
                                {t("global.review.loading")}
                            </div>
                        </Show>

                        <Show when={review()}>
                            {(rev) => (
                                <>
                                    <div class="pointer-events-none w-fit">
                                        <Stars name="view-rating" initialValue={rev().rating} />
                                    </div>

                                    <div class="rounded-radius bg-surface-alt p-4 text-sm dark:bg-surface-dark-alt">
                                        <Show
                                            when={rev().comments}
                                            fallback={<span class="italic text-on-surface-weak dark:text-on-surface-dark-weak">{t("global.review.noComments")}</span>}
                                        >
                                            {rev().comments}
                                        </Show>
                                    </div>
                                </>
                            )}
                        </Show>

                        <div class="border-t border-outline pt-4 dark:border-outline-dark">
                            <Button
                                type="button"
                                category="secondary"
                                text={t("global.generic.word.close")}
                                onClick={props.onClose}
                                class="w-full"
                            />
                        </div>
                    </div>
                </div>
            </div>
        </Show>
    );
}