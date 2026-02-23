import { action, useSubmission } from "@solidjs/router";
import { Api } from "@/app-context/api.ts";
import { ApiError } from "@/api/types.ts";
import { useApi, useTranslator } from "@/app-context/AppContext.tsx";
import { useModalContext } from "@components/modal/Context.tsx";
import { useToast } from "solid-notifications";
import {createEffect, Show} from "solid-js";
import { Modal } from "@components/modal/Modal.tsx";
import Button from "@components/Button.tsx";
import Stars from "@components/inputs/Stars.tsx";


export const submitReviewAction = action(
    async (api: Api, bookingId: number, t: any, toaster: any, formData: FormData) => {

        const ratingString = formData.get("rating");

        if (!ratingString) {
            return { ok: false, resp: { rating: [t("global.error.ratingRequired")] } };
        }

        const rating = Number(ratingString);

        const commentsText = formData.get("review") as string;

        const response = await api.postReview(bookingId, rating, commentsText);

        if (!response.ok) {
            if (response.status === 400) {
                const errorList: ApiError[] = await response.json();
                const fieldErrors: Record<string, string[]> = {};

                errorList.forEach(err => {
                    if (err.description) {

                        toaster.notify(err.description, { type: "error" });

                        if (err.entity) {
                            if (!fieldErrors[err.entity]) fieldErrors[err.entity] = [];
                            fieldErrors[err.entity].push(err.description);
                        }
                    }
                });
                return { ok: false, resp: fieldErrors };
            }
            if (response.status === 401) {
                toaster.notify(t("global.error.unauthorizedReview"), { type: "error" });
            }
            toaster.notify(t("global.error.generic"), { type: "error" });
            return { ok: false, resp: {} };
        }

        toaster.notify(t("global.review.success"), { type: "success" });
        return { ok: true, resp: await response.json() };
    },
    "submit-review-action"
);


export function ReviewBookingForm(props: { bookingId: number }) {
    const t = useTranslator();
    const modalContext = useModalContext();
    const [_, setIsOpen] = modalContext.open;
    const api = useApi();
    const toaster = useToast();

    const submission = useSubmission(submitReviewAction);

    createEffect((wasPending) => {
        const isPending = submission.pending;
        if (wasPending && !isPending && submission.result?.ok) {
            setIsOpen(false);
        }
        return isPending;
    }, false);

    const errors = () => (submission.result?.ok === false ? submission.result.resp : {});


    const reviewForm = t("global.reviewForm") as any;

    return (
        <form
            action={submitReviewAction.with(api, props.bookingId, t, toaster)}
            method="post"
            class="flex flex-col gap-4"
        >
            <Modal.Dialog.Body>
                <div class="space-y-6">

                    <Stars
                        name="rating"
                        label={reviewForm.title}
                        error={errors()?.["rating"]}
                    />

                    <div class="flex flex-col gap-1 text-on-surface dark:text-on-surface-dark">
                        <label for="review" class="w-fit pl-0.5 text-sm">
                            {reviewForm.yourComment}
                        </label>
                        <textarea
                            id="review"
                            name="review"
                            maxLength={255}
                            rows={4}
                            placeholder={reviewForm.ratingPlaceholder}
                            class="w-full resize-none rounded-radius border bg-surface-alt px-4 py-2 text-sm focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary disabled:cursor-not-allowed disabled:opacity-75 dark:bg-surface-dark-alt/50 dark:focus-visible:outline-primary-dark"
                            classList={{
                                "border-danger": !!errors()?.["review"],
                                "border-outline dark:border-outline-dark": !errors()?.["review"]
                            }}
                            disabled={submission.pending}
                        />

                        <Show when={errors()?.["review"]}>
                            <small class="pl-0.5 text-danger">• {errors()?.["review"]?.[0]}</small>
                        </Show>
                    </div>

                </div>
            </Modal.Dialog.Body>

            <Modal.Dialog.Footer>
                <Button
                    type="button"
                    category="secondary"
                    text={t("global.generic.word.cancel")}
                    onClick={() => setIsOpen(false)}
                    disabled={submission.pending}
                />
                <Button
                    type="submit"
                    category="primary"
                    text={submission.pending ? "..." : t("global.generic.word.send")}
                    disabled={submission.pending}
                />
            </Modal.Dialog.Footer>
        </form>
    );
}