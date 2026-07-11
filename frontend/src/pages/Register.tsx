import { createSignal, For, Show } from "solid-js";
import { useApi, useAppState, useTranslator } from "@/app-context/AppContext";
import Button from "@/components/Button";
import TextInput from "@/components/inputs/TextInput";
import PasswordInput from "@/components/inputs/PasswordInput";
import ZoneInput from "@components/inputs/ZoneInput.tsx";
import Select from "@/components/Select";
import { useNavigate, A } from "@solidjs/router";
import { Title } from "@solidjs/meta";
import type { Client, Driver } from "@/api/types";

const UserRoles = [
  { key: "CLIENT", value: "client", labelKey: "global.userType.client" },
  { key: "DRIVER", value: "driver", labelKey: "global.userType.driver" },
] as const;
type RoleEntry = (typeof UserRoles)[number];

type ApiValidationError = {
  description: string;
  entity: string;
  subtype: string;
  type: string;
};

type FieldErrors = Partial<Record<string, string[]>>;

export default function Register() {
  const t = useTranslator();
  const [appState] = useAppState();
  const api = useApi();
  const navigate = useNavigate();

  const [loading, setLoading] = createSignal(false);
  const [fieldErrors, setFieldErrors] = createSignal<FieldErrors>({});
  const [generalErrors, setGeneralErrors] = createSignal<string[]>([]);

  const [username, setUsername] = createSignal("");
  const [email, setEmail] = createSignal("");
  const [password, setPassword] = createSignal("");
  const [confirmedPassword, setConfirmedPassword] = createSignal("");

  const [description, setDescription] = createSignal("");
  const [zoneQuery, setZoneQuery] = createSignal<string>("");
  const [zoneId, setZoneId] = createSignal<number | null>(null);

  const [type, setType] = createSignal<RoleEntry["value"]>("client");

  const handleSubmit = async (e: SubmitEvent) => {
    e.preventDefault();
    setGeneralErrors([]);

    const clientErrors: FieldErrors = {};

    if (!username().trim()) clientErrors.username = [t("global.error.validation.NotBlank") as string];
    if (!email().trim()) clientErrors.email = [t("global.error.validation.NotBlank") as string];
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email().trim()))
      clientErrors.email = [t("global.error.validation.Email") as string];
    if (!password().trim()) clientErrors.password = [t("global.error.validation.NotBlank") as string];
    if (!confirmedPassword().trim()) clientErrors.confirmedPassword = [t("global.error.validation.NotBlank") as string];
    else if (password() !== confirmedPassword())
      clientErrors.confirmedPassword = [t("global.error.passwordMismatch") as string];

    if (type() === "driver" && !description().trim()) {
      clientErrors.description = [t("global.error.validation.NotBlank") as string];
    }
    if (type() === "client") {
      if (!zoneQuery().trim()) clientErrors.zoneId = [t("global.error.validation.NotBlank") as string];
      else if (zoneId() === null) clientErrors.zoneId = [t("global.error.validation.InvalidZone") as string];
    }

    setFieldErrors(clientErrors);
    if (Object.keys(clientErrors).length > 0) return;

    setLoading(true);
    try {
      const lang = appState.preferences?.locale || "es-AR";
      let res: Response;

      if (type() === "client") {
        const body: { username: string; email: string; password: string; zoneId?: number } = {
          username: username(),
          email: email(),
          password: password(),
        };
        const parsedZone = Number(zoneId());
        if (zoneId() !== null && !Number.isNaN(parsedZone)) {
          body.zoneId = parsedZone;
        }
        res = await api.registerClient(body, lang);
      } else {
        res = await api.registerDriver(
          {
            username: username(),
            email: email(),
            password: password(),
            description: description(),
          },
          lang
        );
      }

      if (!res.ok) {
        const json = await res.json().catch(() => null);

        // Handle array of validation errors from the API
        if (Array.isArray(json)) {
          const mapped: FieldErrors = {};
          const unmatched: string[] = [];

          for (const err of json as ApiValidationError[]) {
            const field = err.entity ?? null;

            // Special case: zoneId NotNull means the user typed something but didn't pick from the list
            let msg: string;
            if (err.entity === "zoneId" && err.subtype === "NotNull" && zoneQuery().trim() !== "") {
              msg = t("global.error.validation.InvalidZone") as string;
            } else {
              const translationKey = `global.error.validation.${err.subtype}` as any;
              const translated = t(translationKey);
              msg =
                translated && translated !== translationKey
                  ? (translated as string)
                  : (err.description ?? (t("global.error.unknown") as string));
            }

            // Map API entity names to our local field names
            const fieldMap: Record<string, string> = {
              zoneId: "zoneId",
              username: "username",
              email: "email",
              password: "password",
              description: "description",
            };

            const localField = field && fieldMap[field] ? fieldMap[field] : null;
            if (localField) {
              mapped[localField] = [...(mapped[localField] ?? []), msg];
            } else {
              unmatched.push(msg);
            }
          }

          // Merge in client-side password mismatch if applicable
          if (password() !== confirmedPassword()) {
            mapped.confirmedPassword = [
              ...(mapped.confirmedPassword ?? []),
              t("global.error.passwordMismatch") as string,
            ];
          }

          setFieldErrors(mapped);
          if (unmatched.length > 0) setGeneralErrors(unmatched);
          return;
        }

        // Fallback for non-array error responses
        const errMsg = (json && (json.description || json.message)) || `${res.status} ${res.statusText}`;
        throw new Error(errMsg);
      }

      if (password() !== confirmedPassword()) {
        setFieldErrors({ confirmedPassword: [t("global.error.passwordMismatch") as string] });
        return;
      }

      const createdUser = (await res.json()) as Client | Driver;

      await api.basicAuth(username(), password());

      if (!appState.session) throw new Error(t("global.loginError") as string);

      // `basicAuth` only stores the session tokens in the `appState`, after successful
      // login we update the state manually with the post request's response data.
      api.setUser(createdUser, type());

      const redirect = `/${appState.user?.type}`;
      navigate(redirect, { replace: true });
    } catch (err) {
      console.error(err);
      if (err instanceof Error) setGeneralErrors([err.message]);
      else setGeneralErrors([t("global.error.unknown") as string]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div class="flex min-h-svh items-center justify-center bg-surface p-4 dark:bg-surface-dark">
      <Title>Register - Van N' Go</Title>

      <div class="w-full max-w-md rounded-2xl border border-outline bg-surface-alt p-8 shadow-xl dark:border-outline-dark dark:bg-surface-dark-alt">
        <div class="mb-8 text-center">
          <h1 class="text-3xl font-bold tracking-tight text-on-surface dark:text-on-surface-dark">
            {t("global.register")}
          </h1>
          <p class="mt-2 text-sm text-on-surface/70 dark:text-on-surface-dark/70">Crea una cuenta para continuar</p>
        </div>

        <form onSubmit={handleSubmit} class="flex flex-col gap-4 items-center w-full">
          <Show when={generalErrors().length > 0}>
            <div class="w-full max-w-xs rounded-radius border border-danger/50 bg-danger/10 p-3 text-sm text-danger dark:border-red-400/50 dark:bg-red-400/10 dark:text-red-400">
              <For each={generalErrors()}>{(error) => <p>• {error}</p>}</For>
            </div>
          </Show>

          <TextInput
            class="mx-auto"
            name="username"
            label={t("global.username")}
            value={username()}
            onInput={(e) => setUsername(e.currentTarget.value)}
            error={fieldErrors().username}
          />

          <TextInput
            class="mx-auto"
            name="email"
            label={t("global.generic.word.email")}
            value={email()}
            onInput={(e) => setEmail(e.currentTarget.value)}
            error={fieldErrors().email}
          />

          <PasswordInput
            class="mx-auto"
            label={t("global.password")}
            value={password()}
            onInput={(e) => setPassword(e.currentTarget.value)}
            error={fieldErrors().password}
          />

          <PasswordInput
            class="mx-auto"
            name="confirmedPassword"
            label={t("global.confirmPassword")}
            value={confirmedPassword()}
            onInput={(e) => setConfirmedPassword(e.currentTarget.value)}
            error={fieldErrors().confirmedPassword}
          />

          <Select
            label={t("global.userType.label") as string}
            value={type()}
            onInput={(e) => setType(e.currentTarget.value as RoleEntry["value"])}
          >
            <For each={UserRoles}>
              {(r: RoleEntry) => (
                <option
                  class="bg-surface text-on-surface dark:bg-surface-dark dark:text-on-surface-dark"
                  value={r.value}
                >
                  {String(t(r.labelKey as any))}
                </option>
              )}
            </For>
          </Select>

          {type() === "driver" && (
            <TextInput
              class="mx-auto"
              name="description"
              label={t("global.generic.word.description") as string}
              value={description()}
              onInput={(e) => setDescription(e.currentTarget.value)}
              error={fieldErrors().description}
            />
          )}

          {type() === "client" && (
            <div class="w-full flex justify-center">
              <ZoneInput
                label={t("global.zone.label" as any) as string}
                value={zoneQuery()}
                onInput={(v) => setZoneQuery(v)}
                onSelect={(id: number | null) => setZoneId(id)}
                error={fieldErrors().zoneId?.[0] || null}
              />
            </div>
          )}

          <Button
            type="submit"
            disabled={loading()}
            category="primary"
            text={loading() ? (t("global.registering") as string) : (t("global.register") as string)}
            class="mt-4"
          />
        </form>

        <div class="mt-6 text-center text-sm text-on-surface dark:text-on-surface-dark">
          ¿Ya tenés una cuenta?{" "}
          <A
            href="/login"
            class="font-semibold text-primary hover:underline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary dark:text-primary-dark dark:focus-visible:outline-primary-dark"
          >
            {t("global.login")}
          </A>
        </div>
      </div>
    </div>
  );
}
