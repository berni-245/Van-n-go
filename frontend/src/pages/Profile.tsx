import { createAsync, query, action, useSubmission } from "@solidjs/router";
import { createEffect, Match, Show, Suspense, Switch } from "solid-js";
import { Title } from "@solidjs/meta";
import { useApi, useAppState, useTranslator } from "@/app-context/AppContext";
import Button from "@/components/Button";
import ImageWithFallback from "@/components/ImageWithFallback";
import { ApiError, Client, Driver, UpdateClient, UpdateDriver } from "@/api/types";
import ClientProfileInfo from "@/components/profile/ClientProfileInfo";
import DriverProfileInfo from "@/components/profile/DriverProfileInfo";
import { Modal } from "@/components/modal/Modal";
import { useModalContext } from "@/components/modal/Context";
import TextInput from "@/components/inputs/TextInput";
import PasswordInput from "@/components/inputs/PasswordInput";
import EditClientProfile from "@/components/profile/EditClientProfile";
import EditDriverProfile from "@/components/profile/EditDriverProfile";
import { Api } from "@/app-context/api.ts";
import Select from "@components/Select.tsx";
import { EditProfilePicForm } from "@components/profile/EditProfilePicForm";

const userProfileQuery = query(async () => {
  const api = useApi();
  const [appState] = useAppState();

  if (!appState.user) return null;


  if (appState.user.type === "client") {
    return await api.getClient(appState.user.id, { visibility: "all" });
  } else {
    return await api.getDriver(appState.user.id, { visibility: "all" });
  }
}, "user-profile");

export default function Profile() {
  const [appState] = useAppState();
  const t = useTranslator();

  // Ejecutamos la query
  const fullUser = createAsync(() => userProfileQuery());

  return (
    <>
      <Title>{t("global.components.header.profile")} - Van N' Go</Title>

      <section class="mx-auto w-full max-w-3xl px-4 py-8">
        <Suspense fallback={<div class="text-center">Loading profile...</div>}>
          <Show when={fullUser()}>
            {(user) => (
              <div
                class="overflow-hidden rounded-radius border border-outline bg-surface-alt dark:border-outline-dark dark:bg-surface-dark-alt shadow-lg">


                <div
                  class="border-b border-outline bg-surface p-6 text-center dark:border-outline-dark dark:bg-surface-dark">
                  <h1 class="text-3xl font-bold text-on-surface-strong dark:text-on-surface-dark-strong">
                    {user().username}
                  </h1>
                </div>


                <div class="p-8">



                  <div class="mb-8 flex flex-col items-center">
                    <Modal>
                      <Modal.OpenElement>
                        <div class="group relative h-40 w-40 cursor-pointer overflow-hidden rounded-full border-4 border-outline dark:border-outline-dark">
                          <ImageWithFallback
                            src={user().profilePic}
                            fallbackSrc="/images/defaultUserPfp.png"
                            skipSrc={!user().hasProfilePic}
                            class="h-full w-full object-cover transition-all duration-200 group-hover:brightness-75"
                            alt="Profile Picture"
                          />

                          <div class="absolute inset-0 flex items-center justify-center bg-black/40 opacity-0 transition-opacity duration-200 group-hover:opacity-100">
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="white" class="size-8">
                              <path stroke-linecap="round" stroke-linejoin="round" d="M6.827 6.175A2.31 2.31 0 0 1 5.186 7.23c-.38.054-.757.112-1.134.175C2.999 7.58 2.25 8.507 2.25 9.574V18a2.25 2.25 0 0 0 2.25 2.25h15A2.25 2.25 0 0 0 21.75 18V9.574c0-1.067-.75-1.994-1.802-2.169a47.865 47.865 0 0 0-1.134-.175 2.31 2.31 0 0 1-1.64-1.055l-.822-1.316a2.192 2.192 0 0 0-1.736-1.039 48.774 48.774 0 0 0-5.232 0 2.192 2.192 0 0 0-1.736 1.039l-.821 1.316Z" />
                              <path stroke-linecap="round" stroke-linejoin="round" d="M16.5 12.75a4.5 4.5 0 1 1-9 0 4.5 4.5 0 0 1 9 0ZM18.75 10.5h.008v.008h-.008V10.5Z" />
                            </svg>
                          </div>
                        </div>
                      </Modal.OpenElement>

                      <Modal.Dialog>
                        <Modal.Dialog.Header>
                          <h3 class="text-xl font-bold">{t("global.generic.word.profilePicture")}</h3>
                        </Modal.Dialog.Header>

                        <EditProfilePicForm/>
                      </Modal.Dialog>
                    </Modal>
                  </div>


                  <div class="flex flex-col gap-4 text-center text-lg text-on-surface dark:text-on-surface-dark">
                    <p>
                      <strong class="font-semibold text-on-surface-strong dark:text-on-surface-dark-strong">
                        {t("global.generic.word.email")}:
                      </strong>{" "}
                      {user().email}
                    </p>
                    <p>
                      <strong class="font-semibold text-on-surface-strong dark:text-on-surface-dark-strong">
                        {t("global.language.preferredLanguage")}:
                      </strong>{" "}
                      <span class="capitalize">{t(`global.language.${user().preferredLanguage}`)}</span>
                    </p>

                    <Switch>
                      <Match when={appState.user?.type === "client"}>
                        <ClientProfileInfo user={user() as Client} />
                      </Match>
                      <Match when={appState.user?.type === "driver"}>
                        <DriverProfileInfo user={user() as Driver} />
                      </Match>
                    </Switch>
                  </div>

                  <div class="mt-10 flex flex-wrap justify-center gap-4">

                    <Modal>

                      <Modal.OpenElement>
                        <Button category="primary" text={t("global.generic.word.edit")} />
                      </Modal.OpenElement>

                      <Modal.Dialog>
                        <Modal.Dialog.Header>
                          <h3 class="text-xl font-bold">{t("global.generic.word.edit")}</h3>
                        </Modal.Dialog.Header>

                        <EditProfileForm user={user()} />
                      </Modal.Dialog>
                    </Modal>


                    <Modal>
                      <Modal.OpenElement>
                        <Button category="secondary" text={t("global.user.editUser.changePassword")} />
                      </Modal.OpenElement>

                      <Modal.Dialog>
                        <Modal.Dialog.Header>
                          <h3>{t("global.user.editUser.changePassword")}</h3>
                        </Modal.Dialog.Header>

                        <ChangePasswordForm />
                      </Modal.Dialog>
                    </Modal>

                  </div>

                </div>
              </div>
            )}
          </Show>
        </Suspense>
      </section>
    </>
  );
}


export const updateProfileAction = action(async (api: Api, userId: number, userType: string, formData: FormData) => {
  let response: Response;
 // const [_, setAppState] = useAppState();

  if (userType === "client") {
    const clientData: UpdateClient = {
      username: formData.get("username") as string,
      email: formData.get("email") as string,
      zoneId: Number(formData.get("zoneId")),
      preferredLanguage: formData.get("preferredLanguage") as string,
    };
    response = await api.updateClientInfo(userId, clientData);
  } else {
    const driverData: UpdateDriver = {
      username: formData.get("username") as string,
      email: formData.get("email") as string,
      description: formData.get("description") as string,
      preferredLanguage: formData.get("preferredLanguage") as string,
    };
    response = await api.updateDriverInfo(userId, driverData);
  }

  if (!response.ok) {
    if (response.status === 400) {

      const errorList: ApiError[] = await response.json();


      const fieldErrors: Record<string, string[]> = {};

      errorList.forEach(err => {
        if (err.entity && err.description) {
          if (!fieldErrors[err.entity]) {
            fieldErrors[err.entity] = [];
          }
          fieldErrors[err.entity].push(err.description);
        }
      });
      return { ok: false, resp: fieldErrors };
    }
    throw new Error();
  }

  api.getUser()
  return { ok: true, resp: await response.json() };
}, "update-profile-action");

function EditProfileForm(props: { user: Client | Driver }) {
  const t = useTranslator();
  const modalContext = useModalContext();
  const [_, setIsOpen] = modalContext.open;
  const [appState] = useAppState();
  const api = useApi();

  const submission = useSubmission(updateProfileAction);

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
    <form action={updateProfileAction.with(api, appState.user?.id!, appState.user?.type!)}
          method="post" class="flex flex-col gap-4">
      <Modal.Dialog.Body>
        <div class="space-y-4">
          <TextInput disabled={submission.pending}
                     name="username"
                     label={t("global.username")}
                     value={props.user.username}
                     error={errors()["username"]}
          />
          <TextInput disabled={submission.pending}
                     name="email"
                     label={t("global.generic.word.email")}
                     value={props.user.email}
                     type="email"
                     error={errors()["email"]}
          />
          <Select
            label={t("global.language.preferredLanguage")}
            name="preferredLanguage"
            value={props.user.preferredLanguage}
          >
            <option value="ENGLISH" selected={appState.preferences.locale === "en"}>
              {t("global.language.ENGLISH")}
            </option>
            <option value="SPANISH" selected={appState.preferences.locale === "es"}>
              {t("global.language.SPANISH")}
            </option>
          </Select>
          <Switch>
            <Match when={appState.user?.type === "client"}>
              <EditClientProfile user={props.user as Client} errors={errors()} />
            </Match>
            <Match when={appState.user?.type === "driver"}>
              <EditDriverProfile user={props.user as Driver} errors={errors()} />
            </Match>
          </Switch>
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


export const changePasswordAction = action(async (api: Api, userId: number, userType: string, formData: FormData) => {
  let response: Response;
  let password = formData.get("password") as string;
  let oldPassword = formData.get("oldPassword") as string;


  if (password !== formData.get("confirmPassword")) {
    return { ok: false, resp: { confirmPassword: ["Passwords do not match"] } };
  }

  if (userType === "client") {
    const clientData: UpdateClient = {
      password: password,
      oldPassword: oldPassword,
    };
    response = await api.updateClientInfo(userId, clientData);
  } else {
    const driverData: UpdateDriver = {
      password: password,
      oldPassword: oldPassword,
    };
    response = await api.updateDriverInfo(userId, driverData);
  }


  if (!response.ok) {
    if (response.status === 400) {

      const errorList: ApiError[] = await response.json();


      const fieldErrors: Record<string, string[]> = {};

      errorList.forEach(err => {
        if (err.entity && err.description) {
          if (!fieldErrors[err.entity]) {
            fieldErrors[err.entity] = [];
          }
          fieldErrors[err.entity].push(err.description);
        }
      });
      return { ok: false, resp: fieldErrors };
    }
    throw new Error();
  }


  return { ok: true, resp: await response.json() };
}, "change-password-action");


function ChangePasswordForm() {
  const t = useTranslator();
  const modalContext = useModalContext();
  const [_, setIsOpen] = modalContext.open;
  const [appState] = useAppState();
  const api = useApi();


  const submission = useSubmission(changePasswordAction);

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
    <form action={changePasswordAction.with(api, appState.user?.id!, appState.user?.type!)} method="post"
          class="flex flex-col gap-4">
      <Modal.Dialog.Body>
        <div class="space-y-4">
          <PasswordInput
            name="oldPassword"
            disabled={submission.pending}
            label={t("global.user.editUser.oldPassword")}
            error={errors()["oldPassword"]}
          />
          <PasswordInput
            name="password"
            disabled={submission.pending}
            label={t("global.user.editUser.newPassword")}
            error={errors()["password"]}
          />
          <PasswordInput
            name="confirmPassword"
            value={undefined}
            disabled={submission.pending}
            label={t("global.user.editUser.confirmNewPassword")}
            error={errors()["confirmPassword"]}
          />
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
        /> </Modal.Dialog.Footer>
    </form>
  );
}

