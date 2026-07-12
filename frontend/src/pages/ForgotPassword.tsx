import { useApi, useTranslator } from "@/app-context/AppContext";
import Button from "@/components/Button";
import TextInput from "@/components/inputs/TextInput";
import { Title } from "@solidjs/meta";
import { A } from "@solidjs/router";
import { createSignal, Show } from "solid-js";
import { useToast } from "solid-notifications";

export default function ForgotPassword() {
  const t = useTranslator();
  const api = useApi();
  const toaster = useToast();

  const [email, setEmail] = createSignal("");
  const [loading, setLoading] = createSignal(false);
  const [success, setSuccess] = createSignal(false);
  const [error, setError] = createSignal("");

  const handleSubmit = async (e: SubmitEvent) => {
    e.preventDefault();
    if (!email()) return;

    try {
      setLoading(true);
      setError("");
      setSuccess(false);

      const res = await api.requestPasswordToken({ mail: email() });

      if (!res.ok) {
        if (res.status === 400 || res.status === 404) {
          setError("global.forgotPassword.tokenSentError");
        } else {
          setError("global.error.unknown");
        }
        return;
      }

      setSuccess(true);
      toaster.notify(t("global.forgotPassword.tokenSentSuccess" as any) as string, { type: "info" });
    } catch (e) {
      console.error(e);
      setError("global.error.unknown");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div class="flex min-h-svh items-center justify-center bg-surface p-4 dark:bg-surface-dark">
      <Title>{t("global.forgotPassword.title" as any) as string} - Van N' Go</Title>

      <div class="w-full max-w-md rounded-2xl border border-outline bg-surface-alt p-8 shadow-xl dark:border-outline-dark dark:bg-surface-dark-alt">
        <div class="mb-8 text-center">
          <h1 class="text-3xl font-bold tracking-tight text-on-surface dark:text-on-surface-dark">
            {t("global.forgotPassword.title" as any) as string}
          </h1>
          <p class="mt-2 text-sm text-on-surface/70 dark:text-on-surface-dark/70">
            {t("global.forgotPassword.subtitle" as any) as string}
          </p>
        </div>

        <Show when={success()}>
          <div class="mb-6 rounded-radius border border-outline bg-surface-alt/50 p-4 text-sm text-on-surface/90 dark:border-outline-dark dark:bg-surface-dark-alt/50 dark:text-on-surface-dark/90">
            <p class="font-medium">{t("global.forgotPassword.tokenSentSuccess" as any) as string}</p>
          </div>
        </Show>

        <Show when={error()}>
          <div class="mb-6 rounded-radius border border-danger/50 bg-danger/10 p-3 text-sm text-danger dark:border-red-400/50 dark:bg-red-400/10 dark:text-red-400">
            <p>• {t(error() as any) as string}</p>
          </div>
        </Show>

        <Show when={!success()}>
          <form onSubmit={handleSubmit} class="flex flex-col gap-6 items-center">
            <TextInput
              class="mx-auto"
              name="email"
              label={t("global.forgotPassword.email" as any) as string}
              value={email()}
              onInput={(e) => setEmail(e.currentTarget.value)}
              type="email"
              required
            />

            <Button
              type="submit"
              disabled={loading()}
              category="primary"
              text={loading() ? "..." : (t("global.forgotPassword.sendToken" as any) as string)}
            />
          </form>
        </Show>

        <div class="mt-6 text-center text-sm text-on-surface dark:text-on-surface-dark">
          <A href="/login" class="font-semibold text-primary hover:underline dark:text-primary-dark">
            {t("global.generic.word.back" as any) as string}
          </A>
        </div>
      </div>
    </div>
  );
}
