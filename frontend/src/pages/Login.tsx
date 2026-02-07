import { addBasicAuth } from "@/api/authorization";
import { ApiError } from "@/api/types";
import { useAppState, useApi, useTranslator } from "@/app-context/AppContext";
import Button from "@/components/Button";
import { createSignal, For } from "solid-js";
import TextInput from "@/components/inputs/TextInput";
import PasswordInput from "@/components/inputs/PasswordInput";
import { createAsync, query, redirect } from "@solidjs/router";
import { TokenPayload } from "@/api/jwt";
import { BASE_URL as API_ROOT } from "@/app-context/api";

const getCurrentUserQuery = query(async (payload?: TokenPayload) => {
  if (!payload) return;

  const api = useApi();

  const user = await api.getUser(payload.userType, payload.userId);

  if (user) return redirect(`/${payload.userType}s/${payload.userId}`);
}, "currentUser");

export default function Login() {
  const t = useTranslator();
  const [appState, _] = useAppState();
  const api = useApi();

  const [loading, setLoading] = createSignal(false);
  const [errors, setErrors] = createSignal<string[]>([]);

  const [username, setUsername] = createSignal("");
  const [password, setPassword] = createSignal("");

  createAsync(() => getCurrentUserQuery(appState.session?.payload));

  const handleSubmit = async (e: SubmitEvent) => {
    e.preventDefault();
    try {
      setLoading(true);
      const request = new Request(API_ROOT);
      addBasicAuth({ request, username: username(), password: password() });
      const res = await api.authFetch(request);
      // TODO: maybe should handle json parsing errors?
      const json = await res.json();

      // TODO: toast and/or mark inputs with red instead of error
      if (!res.ok) {
        setLoading(false);
        const error = json as ApiError;
        throw new Error(`${error.type}: ${error.description}`);
      }
    } catch (e) {
      // TODO: toast message
      console.error(e);
      if (e instanceof Error) {
        setErrors((prev) => [...prev, e.message]);
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <form onSubmit={handleSubmit}>
        <TextInput
          label={t("global.username")}
          // placeholder={t("global.username")}
          value={username()}
          onInput={(e) => setUsername(e.currentTarget.value)}
          class="mb-4"
        />
        <PasswordInput
          label={t("global.password")}
          // placeholder={t("global.password")}
          value={password()}
          onInput={(e) => setPassword(e.currentTarget.value)}
          class="mb-4"
        />
        <Button type="submit" disabled={loading()} category="info" text={loading() ? "Logging in…" : "Login"} />
      </form>
      <For each={errors()}>{(error) => <p>{error}</p>}</For>
    </>
  );
}
