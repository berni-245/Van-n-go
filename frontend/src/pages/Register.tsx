import { createSignal, For } from "solid-js";
import { useApi, useAppState, useTranslator } from "@/app-context/AppContext";
import Button from "@/components/Button";
import TextInput from "@/components/inputs/TextInput";
import PasswordInput from "@/components/inputs/PasswordInput";
import ZoneInput from "@components/inputs/ZoneInput.tsx";
import { useNavigate } from "@solidjs/router";
import { Title } from "@solidjs/meta";

const UserRoles = [
    { key: "CLIENT", value: "client", labelKey: "global.userType.client" },
    { key: "DRIVER", value: "driver", labelKey: "global.userType.driver" },
] as const;
type RoleEntry = typeof UserRoles[number];

type ApiValidationError = {
    description: string;
    entity: string;
    subtype: string;
    type: string;
};

type FieldErrors = Partial<Record<string, string[]>>;

function FieldError(props: { errors: FieldErrors; field: string }) {
    const msgs = () => props.errors[props.field];
    return (
        <For each={msgs()}>
            {(msg) => <p class="mt-1 mb-2 text-sm text-red-500">{msg}</p>}
        </For>
    );
}

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
        else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email().trim())) clientErrors.email = [t("global.error.validation.Email") as string];
        if (!password().trim()) clientErrors.password = [t("global.error.validation.NotBlank") as string];
        if (!confirmedPassword().trim()) clientErrors.confirmedPassword = [t("global.error.validation.NotBlank") as string];
        else if (password() !== confirmedPassword()) clientErrors.confirmedPassword = [t("global.error.passwordMismatch") as string];

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
                    lang,
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
                            msg = (translated && translated !== translationKey)
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
                const errMsg =
                    (json && (json.description || json.message)) ||
                    `${res.status} ${res.statusText}`;
                throw new Error(errMsg);
            }

            if (password() !== confirmedPassword()) {
                setFieldErrors({ confirmedPassword: [t("global.error.passwordMismatch") as string] });
                return;
            }

            await api.basicAuth(username(), password());

            if (!appState.session) throw new Error(t("global.loginError") as string);

            const redirect = `/${appState.user?.type ?? type()}`;
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
        <>
            <Title>Register - Van N' go</Title>
            <div class="min-h-screen flex items-center justify-center p-4">
                <div class="w-full max-w-lg bg-white dark:bg-gray-900 shadow-md rounded-lg p-6 sm:p-8 mx-2">
                    <form onSubmit={handleSubmit}>
                        <TextInput
                            label={t("global.username")}
                            value={username()}
                            onInput={(e) => setUsername(e.currentTarget.value)}
                            class="mb-1"
                        />
                        <FieldError errors={fieldErrors()} field="username" />

                        <TextInput
                            label={t("global.generic.word.email")}
                            value={email()}
                            onInput={(e) => setEmail(e.currentTarget.value)}
                            class="mb-1"
                        />
                        <FieldError errors={fieldErrors()} field="email" />

                        <PasswordInput
                            label={t("global.password")}
                            value={password()}
                            onInput={(e) => setPassword(e.currentTarget.value)}
                            class="mb-1"
                        />
                        <FieldError errors={fieldErrors()} field="password" />

                        <PasswordInput
                            label={t("global.confirmPassword")}
                            value={confirmedPassword()}
                            onInput={(e) => setConfirmedPassword(e.currentTarget.value)}
                            class="mb-1"
                        />
                        <FieldError errors={fieldErrors()} field="confirmedPassword" />

                        <label>{t("global.userType.label")}</label>
                        <select
                            value={type()}
                            onInput={(e) => setType(e.currentTarget.value as RoleEntry["value"])}
                            class="mb-4 block w-full"
                        >
                            <For each={UserRoles}>
                                {(r: RoleEntry) => (
                                    <option value={r.value}>{String(t(r.labelKey as any))}</option>
                                )}
                            </For>
                        </select>

                        {type() === "driver" && (
                            <>
                                <TextInput
                                    label={t("global.generic.word.description") as string}
                                    value={description()}
                                    onInput={(e) => setDescription(e.currentTarget.value)}
                                    class="mb-1"
                                />
                                <FieldError errors={fieldErrors()} field="description" />
                            </>
                        )}
                        {type() === "client" && (
                            <>
                                <ZoneInput
                                    label={t("global.zone.label" as any)}
                                    value={zoneQuery()}
                                    onInput={(v) => setZoneQuery(v)}
                                    onSelect={(id: number | null) => setZoneId(id)}
                                />
                                <FieldError errors={fieldErrors()} field="zoneId" />
                            </>
                        )}

                        <Button
                            type="submit"
                            disabled={loading()}
                            category="info"
                            text={loading() ? t("global.registering") as string : t("global.register") as string}
                        />
                    </form>

                    <For each={generalErrors()}>
                        {(error) => <p class="mt-3 text-red-600">{error}</p>}
                    </For>
                </div>
            </div>
        </>
    );
}