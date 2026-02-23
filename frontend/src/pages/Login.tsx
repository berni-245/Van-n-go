import {LocationState} from "@/app-context/api";
import {useApi, useAppState, useTranslator} from "@/app-context/AppContext";
import Button from "@/components/Button";
import PasswordInput from "@/components/inputs/PasswordInput";
import TextInput from "@/components/inputs/TextInput";
import {Title} from "@solidjs/meta";
import {createAsync, useLocation, useNavigate} from "@solidjs/router";
import {createSignal,  Show} from "solid-js";

export default function Login() {
    const t = useTranslator();
    const [appState, _] = useAppState();
    const api = useApi();
    const navigate = useNavigate();
    const location = useLocation<LocationState>();

    const [loading, setLoading] = createSignal(false);
    const [errors, setErrors] = createSignal<string[]>([]);

    const [username, setUsername] = createSignal("");
    const [password, setPassword] = createSignal("");

    createAsync(async () => {
        await useApi().getUser();

        if (appState.user) {
            const redirect = location.state?.from || `/${appState.user.type}`;
            navigate(redirect, {replace: true});
        }
    });

    const handleSubmit = async (e: SubmitEvent) => {
        e.preventDefault();
        try {
            setLoading(true);
            setErrors([]);

            await api.basicAuth(username(), password());

            if (!appState.session) throw new Error();

        } catch (e) {
            console.error(e);
            setErrors((prev) => [...prev, "global.loginError"]);

        } finally {
            setLoading(false);
        }
    };

    return (
        <div class="flex min-h-svh items-center justify-center bg-surface p-4 dark:bg-surface-dark">
            <Title>Login - Van N' Go</Title>


            <div
                class="w-full max-w-md rounded-2xl border border-outline bg-surface-alt p-8 shadow-xl dark:border-outline-dark dark:bg-surface-dark-alt">


                <div class="mb-8 text-center">
                    <h1 class="text-3xl font-bold tracking-tight text-on-surface dark:text-on-surface-dark">
                        {t("global.login")}
                    </h1>
                    <p class="mt-2 text-sm text-on-surface/70 dark:text-on-surface-dark/70">
                        Ingresa tus credenciales para continuar
                    </p>
                </div>

                <form onSubmit={handleSubmit} class="flex flex-col gap-4 items-center">


                    <Show when={errors().length > 0}>
                        <div
                            class="rounded-radius border border-danger/50 bg-danger/10 p-3 text-sm text-danger dark:border-red-400/50 dark:bg-red-400/10 dark:text-red-400">

                               <p>• {t("global.loginError")}</p>

                        </div>
                    </Show>


                    <TextInput
                        class="mx-auto"
                        label={t("global.username")}
                        value={username()}
                        onInput={(e) => setUsername(e.currentTarget.value)}
                    />

                    <PasswordInput
                        class="mx-auto"
                        label={t("global.password")}
                        value={password()}
                        onInput={(e) => setPassword(e.currentTarget.value)}
                    />


                    <div class="mt-4">
                        <Button
                            type="submit"
                            disabled={loading()}
                            category="primary"
                            text={loading() ? t("global.loadingSession") : t("global.login")}
                            class="w-full"
                        />
                    </div>
                </form>


                <div class="mt-6 text-center text-sm text-on-surface dark:text-on-surface-dark">
                    ¿No tenés una cuenta?{" "}
                    <a
                        href="/register"
                        class="font-semibold text-primary hover:underline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary dark:text-primary-dark dark:focus-visible:outline-primary-dark"
                    >
                        {t("global.register")}
                    </a>
                </div>

            </div>
        </div>
    );
}
