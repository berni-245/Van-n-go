import { createAsync, query } from "@solidjs/router";
import { For, Match, Show, Suspense, Switch } from "solid-js";
import { Title } from "@solidjs/meta";
import { useApi, useAppState, useTranslator } from "@/app-context/AppContext";
import Button from "@/components/Button";
import ImageWithFallback from "@/components/ImageWithFallback";
import { FaRegularStar, FaSolidStar } from "solid-icons/fa";
import { Client, Driver } from "@/api/types";


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
                      <span class="capitalize">{user().preferredLanguage}</span>
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

// ------------------------------------------------------------------
// SUB-COMPONENTES PARA SEPARAR LA LÓGICA (Buena práctica en SolidJS)
// ------------------------------------------------------------------

// Componente para lo exclusivo del Cliente
function ClientProfileInfo(props: { user: Client }) {
  const t = useTranslator();
  return (
    <Show when={props.user.zone}>
      <p>
        <strong class="font-semibold text-on-surface-strong dark:text-on-surface-dark-strong">
          {t("global.generic.word.zone")}:
        </strong>{" "}
        {props.user.zone}
      </p>
    </Show>
  );
}

// Componente para lo exclusivo del Conductor
function DriverProfileInfo(props: { user: Driver }) {
  const t = useTranslator();

  // Convertimos a entero por las dudas (como pediste del 0 al 5)
  const ratingInt = () => Math.round(props.user.rating || 0);

  return (
    <>
      <p>
        <strong class="font-semibold text-on-surface-strong dark:text-on-surface-dark-strong">
          {t("global.generic.word.description")}:
        </strong>{" "}
        {props.user.description || t("global.public.profile.noDescription")}
      </p>
      <p>
        <strong class="font-semibold text-on-surface-strong dark:text-on-surface-dark-strong">
          {t("global.generic.word.cbu")}:
        </strong>{" "}
        {props.user.cbu || t("global.public.profile.noCbu")}
      </p>

      {/* Sistema de Estrellas */}
      <div class="mt-2 flex items-center justify-center gap-3">
        <Show
          when={props.user.rating != null}
          fallback={<span class="text-sm italic">{t("global.client.availability.no_rating")}</span>}
        >
          <span class="text-xl font-bold text-warning">
            {props.user.rating}
          </span>
          <div class="flex items-center gap-1">
            <For each={Array.from({ length: 5 })}>
              {(_, index) => (
                <Show
                  when={index() < ratingInt()}
                  fallback={<FaRegularStar class="text-outline dark:text-outline-dark" size={24} />}
                >
                  <FaSolidStar class="text-warning" size={24} />
                </Show>
              )}
            </For>
          </div>
        </Show>
      </div>
    </>
  );
}