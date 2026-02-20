import { createAsync, query } from "@solidjs/router";
import {  Match, Show, Suspense, Switch } from "solid-js";
import { Title } from "@solidjs/meta";
import { useApi, useAppState, useTranslator } from "@/app-context/AppContext";
import Button from "@/components/Button";
import ImageWithFallback from "@/components/ImageWithFallback";
import { Client, Driver } from "@/api/types";
import ClientProfileInfo from "@/components/profile/ClientProfileInfo";
import DriverProfileInfo from "@/components/profile/DriverProfileInfo";

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
              <div class="overflow-hidden rounded-radius border border-outline bg-surface-alt dark:border-outline-dark dark:bg-surface-dark-alt shadow-lg">


                <div class="border-b border-outline bg-surface p-6 text-center dark:border-outline-dark dark:bg-surface-dark">
                  <h1 class="text-3xl font-bold text-on-surface-strong dark:text-on-surface-dark-strong">
                    {user().username}
                  </h1>
                </div>


                <div class="p-8">


                  <div class="mb-8 flex flex-col items-center">
                    <div class="relative h-40 w-40 overflow-hidden rounded-full border-4 border-outline dark:border-outline-dark">
                      <ImageWithFallback
                        src={user().profilePic}
                        fallbackSrc="/images/defaultUserPfp.png"
                        skipSrc={!user().hasProfilePic}
                        class="h-full w-full object-cover"
                        alt="Profile Picture"
                      />
                    </div>
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
                        {t("global.language.label")}:
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
                    <Button
                      category="primary"
                      href={`/${appState.user?.type}/profile/edit`}
                      text={t("global.generic.word.edit")}
                    />
                    <Button
                      category="secondary"
                      href={`/${appState.user?.type}/change/password`}
                      text={t("global.user.editUser.changePassword")}
                    />
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




