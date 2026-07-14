import { useApi, useTranslator } from "@/app-context/AppContext";
import Button from "@/components/Button";
import TextInput from "@/components/inputs/TextInput";
import PasswordInput from "@/components/inputs/PasswordInput";
import { Title } from "@solidjs/meta";
import { A, useNavigate, useSearchParams } from "@solidjs/router";
import { createSignal, Show } from "solid-js";
import { useToast } from "solid-notifications";

export default function RecoverPassword() {
  const t = useTranslator();
  const api = useApi();
  const toaster = useToast();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const [email, setEmail] = createSignal("");
  const [token, setToken] = createSignal("");
  const [password, setPassword] = createSignal("");
  const [confirmPassword, setConfirmPassword] = createSignal("");

  const [loading, setLoading] = createSignal(false);
  const [error, setError] = createSignal("");

  const handleSubmit = async (e: SubmitEvent) => {
    e.preventDefault();
    if (!email() || !token() || !password() || !confirmPassword()) return;

    if (password() !== confirmPassword()) {
      setError("global.error.passwordMismatch");
      return;
    }

    const id = Array.isArray(searchParams.id) ? searchParams.id[0] : searchParams.id;
    if (!id) {
      setError("global.error.unknown");
      return;
    }

    try {
      setLoading(true);
      setError("");

      const res = await api.updateUserPasswordWithToken(id, {
        mail: email(),
        token: token().trim(),
        password: password(),
      });

      if (!res.ok) {
        if (res.status === 400) {
          setError("global.recoverPassword.error");
        } else {
          setError("global.error.unknown");
        }
        return;
      }

      toaster.notify(t("global.recoverPassword.success" as any) as string, { type: "success" });
      navigate("/login");
    } catch (e) {
      setError("global.error.unknown");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div class="flex min-h-svh items-center justify-center bg-surface p-4 dark:bg-surface-dark">
      <Title>{t("global.recoverPassword.title" as any) as string} - Van N' Go</Title>

      <div class="w-full max-w-md rounded-2xl border border-outline bg-surface-alt p-8 shadow-xl dark:border-outline-dark dark:bg-surface-dark-alt">
        <div class="mb-8 text-center">
          <h1 class="text-3xl font-bold tracking-tight text-on-surface dark:text-on-surface-dark">
            {t("global.recoverPassword.title" as any) as string}
          </h1>
          <p class="mt-2 text-sm text-on-surface/70 dark:text-on-surface-dark/70">
            {t("global.recoverPassword.subtitle" as any) as string}
          </p>
        </div>

        <Show when={error()}>
          <div class="mb-6 rounded-radius border border-danger/50 bg-danger/10 p-3 text-sm text-danger dark:border-red-400/50 dark:bg-red-400/10 dark:text-red-400">
            <p>• {t(error() as any) as string}</p>
          </div>
        </Show>

        <form onSubmit={handleSubmit} class="flex flex-col gap-4 items-center">
          <TextInput
            class="mx-auto"
            name="email"
            label={t("global.recoverPassword.email" as any) as string}
            value={email()}
            onInput={(e) => setEmail(e.currentTarget.value)}
            type="email"
            required
            disabled={loading()}
          />

          <TextInput
            class="mx-auto"
            name="token"
            label={t("global.recoverPassword.token" as any) as string}
            value={token()}
            onInput={(e) => setToken(e.currentTarget.value)}
            type="text"
            maxLength={6}
            required
            disabled={loading()}
          />

          <PasswordInput
            class="mx-auto"
            name="password"
            label={t("global.recoverPassword.newPassword" as any) as string}
            value={password()}
            onInput={(e) => setPassword(e.currentTarget.value)}
            required
            disabled={loading()}
          />

          <PasswordInput
            class="mx-auto"
            name="confirmPassword"
            label={t("global.recoverPassword.confirmPassword" as any) as string}
            value={confirmPassword()}
            onInput={(e) => setConfirmPassword(e.currentTarget.value)}
            required
            disabled={loading()}
          />

          <Button
            type="submit"
            disabled={loading()}
            category="primary"
            text={loading() ? "..." : (t("global.recoverPassword.resetButton" as any) as string)}
            class="mt-4"
          />
        </form>

        <div class="mt-6 text-center text-sm text-on-surface dark:text-on-surface-dark">
          <A href="/login" class="font-semibold text-primary hover:underline dark:text-primary-dark">
            {t("global.generic.word.back" as any) as string}
          </A>
        </div>
      </div>
    </div>
  );
}
