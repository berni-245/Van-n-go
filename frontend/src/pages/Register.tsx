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

export default function Register() {
    const t = useTranslator();
    const [appState] = useAppState();
    const api = useApi();
    const navigate = useNavigate();

    const [loading, setLoading] = createSignal(false);
    const [errors, setErrors] = createSignal<string[]>([]);

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
        setErrors([]);

        if (password() !== confirmedPassword()) {
            setErrors([t("global.error.passwordMismatch") as string]);
            return;
        }

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
                const json = await res.json().catch(() => ({}));
                const errMsg =
                    (json && (json.description || json.message || JSON.stringify(json))) ||
                    `${res.status} ${res.statusText}`;
                throw new Error(errMsg);
            }

            navigate("/login", { replace: true });
        } catch (err) {
            console.error(err);
            if (err instanceof Error) setErrors([err.message]);
            else setErrors([t("global.error.unknown") as string]);
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
                            class="mb-4"
                        />
                        <TextInput
                            label={t("global.generic.word.email")}
                            value={email()}
                            onInput={(e) => setEmail(e.currentTarget.value)}
                            class="mb-4"
                        />
                        <PasswordInput
                            label={t("global.password")}
                            value={password()}
                            onInput={(e) => setPassword(e.currentTarget.value)}
                            class="mb-4"
                        />
                        <PasswordInput
                            label={t("global.confirmPassword")}
                            value={confirmedPassword()}
                            onInput={(e) => setConfirmedPassword(e.currentTarget.value)}
                            class="mb-4"
                        />

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
                            <TextInput
                                label={t("global.generic.word.description") as string}
                                value={description()}
                                onInput={(e) => setDescription(e.currentTarget.value)}
                                class="mb-4"
                            />
                        )}
                        {type() === "client" && (
                            <ZoneInput
                                label={t("global.zone.label" as any)}
                                value={zoneQuery()}
                                onInput={(v) => setZoneQuery(v)}
                                onSelect={(id: number | null) => setZoneId(id)}
                            />
                        )}

                        <Button
                            type="submit"
                            disabled={loading()}
                            category="info"
                            text={loading() ? t("global.registering") as string : t("global.register") as string}
                        />
                    </form>

                    <For each={errors()}>
                        {(error) => <p class="mt-3 text-red-600">{error}</p>}
                    </For>
                </div>
            </div>
        </>
    );
}