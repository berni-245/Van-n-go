import { createSignal, For } from "solid-js";
import {useAppState, useTranslator} from "@/app-context/AppContext";
import Button from "@/components/Button";
import TextInput from "@/components/inputs/TextInput";
import PasswordInput from "@/components/inputs/PasswordInput";
import ZoneInput from "@components/inputs/ZoneInput.tsx";

const UserRoles = [
    { key: "CLIENT", value: "client", labelKey: "global.userType.client" },
    { key: "DRIVER", value: "driver", labelKey: "global.userType.driver" },
] as const;
type RoleEntry = typeof UserRoles[number];

export default function Register() {
    const t = useTranslator();
    const [appState] = useAppState();

    const [loading, setLoading] = createSignal(false);
    const [errors, setErrors] = createSignal<string[]>([]);

    const [username, setUsername] = createSignal("");
    const [email, setEmail] = createSignal("");
    const [password, setPassword] = createSignal("");
    const [confirmedPassword, setConfirmedPassword] = createSignal("");

    const [description, setDescription] = createSignal("");
    const [zoneId, setZoneId] = createSignal<string | null>(null);

    const [type, setType] = createSignal<RoleEntry["value"]>("client");

    const handleSubmit = async (e: SubmitEvent) => {
        e.preventDefault();
        setErrors([]);
        if (password() !== confirmedPassword()) {
            setErrors((prev) => [...prev, "Passwords do not match"]);
            return;
        }

        setLoading(true);
        try {
            const langHeader = (appState.preferences && appState.preferences.locale) || "es-AR";
            const headers: Record<string, string> = {
                "Content-Type": "application/json",
                "Accept-Language": langHeader,
            };

            let url = "";
            let bodyObj: Record<string, any> = {
                username: username(),
                email: email(),
                password: password(),
            };

            if (type() === "client") {
                url = "/clients";
                if (zoneId()) {
                    const z = Number(zoneId());
                    if (!Number.isNaN(z)) bodyObj.zoneId = z;
                }
            } else {
                url = "/drivers";
                bodyObj.description = description();
            }

            const res = await fetch(url, {
                method: "POST",
                headers,
                body: JSON.stringify(bodyObj),
            });

            const json = await res.json().catch(() => ({}));

            if (!res.ok) {
                const errMsg =
                    (json && (json.description || json.message || JSON.stringify(json))) ||
                    `${res.status} ${res.statusText}`;
                throw new Error(errMsg);
            }

            setUsername("");
            setEmail("");
            setPassword("");
            setConfirmedPassword("");
            setDescription("");
            setZoneId(null);
            setErrors((prev) => [...prev, "Registration successful — please log in."]);
        } catch (err) {
            console.error(err);
            if (err instanceof Error) setErrors((prev) => [...prev, err.message]);
            else setErrors((prev) => [...prev, "Unknown error"]);
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
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
                            label={t("global.email")}
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
                            value={password()}
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
                            <>
                                <TextInput label="Description" value={description()}
                                           onInput={(e) => setDescription(e.currentTarget.value)} class="mb-4"/>
                            </>
                        )}
                        {type() === "client" && (
                            <ZoneInput
                                label={t("global.zone.label" as any)}
                                value={zoneQuery()}
                                onInput={(val) => setZoneQuery(val)}
                                onSelect={(id) => setZoneId(id)}
                                error={!zoneId() && zoneQuery().trim() !== "" ? t("global.zone.invalid") : null}
                            />
                        )}

                        <Button type="submit" disabled={loading()} category="info"
                                text={loading() ? "Registering…" : "Register"}/>
                    </form>
                </div>
            </div>
                    <For each={errors()}>{(error) => <p class="text-red-600">{error}</p>}</For>
                </>
);
}
