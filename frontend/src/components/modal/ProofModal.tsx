import { createEffect, createResource, onCleanup, Show } from "solid-js";
import { action, useSubmission } from "@solidjs/router";
import { useApi, useTranslator } from "@/app-context/AppContext";
import { Api } from "@/app-context/api";
import { useToast } from "solid-notifications";
import Button from "@components/Button";
import FileInputDragDrop from "@components/inputs/FileInputDragDrop";

interface UploadProofModalProps {
    open: boolean;
    bookingId: number;
    onClose: () => void;
}

export const submitProofAction = action(
    async (api: Api, bookingId: number, t: any, toaster: any, formData: FormData) => {
        const file = formData.get("proof");

        if (!(file instanceof File) || file.size === 0) {
            toaster.notify(t("global.proofOfPayment.errors.selectFirst"), { type: "warning" });
            return { ok: false };
        }

        if (file.type !== "application/pdf") {
            toaster.notify(t("global.proofOfPayment.errors.invalidType"), { type: "error" });
            return { ok: false };
        }

        const response = await api.putProofOfPayment(bookingId, file);

        if (!response.ok) {
            toaster.notify(t("global.proofOfPayment.errors.uploadFailed"), { type: "error" });
            return { ok: false };
        }

        toaster.notify(t("global.proofOfPayment.success"), { type: "success" });
        return { ok: true };
    },
    "submit-proof-action"
);

export function UploadProofModal(props: UploadProofModalProps) {
    const t = useTranslator();
    const api = useApi();
    const toaster = useToast();

    const submission = useSubmission(submitProofAction);

    createEffect((wasPending) => {
        if (wasPending && !submission.pending && submission.result?.ok) {
            props.onClose();
        }
        return submission.pending;
    }, false);

    return (
        <Show when={props.open}>
            <div
                class="fixed inset-0 z-40 flex items-end justify-center bg-black/20 p-4 pb-8 backdrop-blur-md sm:items-center"
                onClick={(e) => e.target === e.currentTarget && props.onClose()}
            >
                <div class="w-full max-w-lg overflow-hidden rounded-radius border border-outline bg-surface text-on-surface dark:border-outline-dark dark:bg-surface-dark dark:text-on-surface-dark">
                    <div class="flex items-center justify-between border-b border-outline bg-surface-alt/60 p-4 dark:border-outline-dark dark:bg-surface-dark/20">
                        <h4 class="font-semibold tracking-wide">
                            {t("global.proofOfPayment.uploadTitle")}
                        </h4>
                        <button onClick={props.onClose} aria-label="close">✕</button>
                    </div>

                    <form
                        action={submitProofAction.with(api, props.bookingId, t, toaster)}
                        method="post"
                        enctype="multipart/form-data"
                        class="flex flex-col gap-6 px-5 py-6"
                    >
                        <div class="flex justify-center">
                            <FileInputDragDrop
                                name="proof"
                                label={t("global.proofOfPayment.uploadTitle")}
                                fileTypes={["PDF"]}
                                maxSize={10}
                                maxSizeUnit={"MB"}
                                putEndpoint=""
                            />
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
                                text={submission.pending ? t("global.proofOfPayment.uploading") : t("global.proofOfPayment.save")}
                                disabled={submission.pending}
                            />
                        </div>
                    </form>
                </div>
            </div>
        </Show>
    );
}

interface ViewProofModalProps {
    open: boolean;
    proofUrl?: string;
    onClose: () => void;
}

export function ViewProofModal(props: ViewProofModalProps) {
    const api = useApi();
    const t = useTranslator();

    const [pdfUrl] = createResource(
        () => (props.open && props.proofUrl ? props.proofUrl : false),
        async (url) => {
            const absoluteUrl = url.startsWith("http") || url.startsWith("/") ? url : "/" + url;
            const res = await api.authFetch(absoluteUrl);
            if (!res.ok) throw new Error("Failed to fetch");
            const blob = await res.blob();
            return URL.createObjectURL(blob);
        }
    );

    onCleanup(() => {
        if (pdfUrl()) URL.revokeObjectURL(pdfUrl()!);
    });

    return (
        <Show when={props.open}>
            <div
                class="fixed inset-0 z-40 flex items-end justify-center bg-black/20 p-4 pb-8 backdrop-blur-md sm:items-center"
                onClick={(e) => e.target === e.currentTarget && props.onClose()}
            >
                <div class="w-full max-w-3xl overflow-hidden rounded-radius border border-outline bg-surface text-on-surface dark:border-outline-dark dark:bg-surface-dark dark:text-on-surface-dark">
                    <div class="flex items-center justify-between border-b border-outline bg-surface-alt/60 p-4 dark:border-outline-dark dark:bg-surface-dark/20">
                        <h4 class="font-semibold tracking-wide">
                            {t("global.proofOfPayment.viewTitle")}
                        </h4>
                        <button onClick={props.onClose} aria-label="close">✕</button>
                    </div>

                    <div class="flex flex-col gap-6 px-5 py-6">
                        <Show when={pdfUrl.loading}>
                            <div class="animate-pulse py-12 text-center text-sm text-on-surface-weak dark:text-on-surface-dark-weak">
                                {t("global.proofOfPayment.loadingView")}
                            </div>
                        </Show>

                        <Show when={pdfUrl()}>
                            {(url) => (
                                <iframe
                                    src={url()}
                                    class="w-full h-[60vh] rounded-radius border border-outline dark:border-outline-dark"
                                />
                            )}
                        </Show>

                        <div class="border-t border-outline pt-4 dark:border-outline-dark">
                            <Button
                                type="button"
                                category="secondary"
                                text={t("global.proofOfPayment.close")}
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