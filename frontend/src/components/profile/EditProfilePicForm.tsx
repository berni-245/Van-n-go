import {action, useSubmission} from "@solidjs/router";
import {Api} from "@/app-context/api.ts";
import {ApiError} from "@/api/types.ts";
import {useApi, useAppState, useTranslator} from "@/app-context/AppContext.tsx";
import {useModalContext} from "@components/modal/Context.tsx";
import {createEffect} from "solid-js";
import {Modal} from "@components/modal/Modal.tsx";
import Button from "@components/Button.tsx";
import FileInput from "@components/inputs/FileInput.tsx";

export const EditProfilePictureAction = action(
    async (api: Api, userId: number, userType: string, t, formData: FormData) => {

        const file = formData.get("file");

        if (!(file instanceof File)) {
            return {ok: false, resp: {file: ["File is required"]}};
        }
        const allowedTypes = ["image/jpeg", "image/png"];

        if (!allowedTypes.includes(file.type)) {
            return {
                ok: false,
                resp: {file: [t("global.error.imageUnsupportedMediaType")]}
            };
        }

        const MAX_SIZE = 10 * 1024 * 1024; // 10MB

        if (file.size > MAX_SIZE) {
            return {
                ok: false,
                resp: {file: [t("global.error.imageTooLarge")]}
            };
        }
        let response: Response;

        if (userType === "client") {
            response = await api.putClientProfilePicture(userId, file);
        } else {
            response = await api.putDriverProfilePicture(userId, file);
        }

        if (!response.ok) {
            if (response.status === 400) {
                const errorList: ApiError[] = await response.json();

                const fieldErrors: Record<string, string[]> = {};

                errorList.forEach(err => {
                    if (err.description) {
                        if (!fieldErrors["file"]) {
                            fieldErrors["file"] = [];
                        }
                        fieldErrors["file"].push(err.description);
                    }
                });

                return {ok: false, resp: fieldErrors};
            }
            if (response.status === 415) {
                return {ok: false, resp: {file: [t("global.error.imageUnsupportedMediaType")]}};

            }
            throw new Error();
        }
        return {ok: true};
    },
    "put-profile-picture-action"
);

export function EditProfilePicForm() {
    const t = useTranslator();
    const modalContext = useModalContext();
    const [_, setIsOpen] = modalContext.open;
    const [appState] = useAppState();
    const api = useApi();

    const submission = useSubmission(EditProfilePictureAction);

    createEffect((wasPending) => {
        const isPending = submission.pending;
        if (wasPending && !isPending && submission.result?.ok) {
            setIsOpen(false);
            //toast(succes)
        }
        return isPending;
    }, false);
    const errors = () => (submission.result?.ok === false ? submission.result.resp : {});

    return (
        <form
            action={EditProfilePictureAction.with(api, appState.user?.id!, appState.user?.type!, t)}
            method="post"
            enctype="multipart/form-data"
            class="flex flex-col gap-4"
        >
            <Modal.Dialog.Body>
                <FileInput
                    label={t("global.generic.word.chooseFile")} //  usar t("generic.phrase.choose.file")
                    name="file"
                    helperText={t("global.user.editUser.uploadPicture")}
                    error={errors()["file"]}
                />
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
