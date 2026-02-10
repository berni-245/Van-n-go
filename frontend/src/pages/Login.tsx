import { API_ROOT, LocationState } from "@/app-context/api";
import { useApi, useAppState, useTranslator } from "@/app-context/AppContext";
import Button from "@/components/Button";
import PasswordInput from "@/components/inputs/PasswordInput";
import TextInput from "@/components/inputs/TextInput";
import { Title } from "@solidjs/meta";
import { createAsync, useLocation, useNavigate } from "@solidjs/router";
import { createSignal, For } from "solid-js";

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
      navigate(redirect, { replace: true });
    }
    // TODO: toast and/or mark inputs with red instead of error
    // else throw new Error("Couldn't log in");
    else {
      console.warn("Couldn't log in");
    }
  });

  const handleSubmit = async (e: SubmitEvent) => {
    e.preventDefault();
    try {
      setLoading(true);
      await api.basicAuth(username(), password());

      // I don't care if request status != 20X as long as I got the tokens.
      // if (!res.ok) {
      //   setLoading(false);
      //   if (api.isJsonResponse(res)) {
      //     const json = await res.json();
      //     const error = json as ApiError;
      //     throw new Error(`${error.type}: ${error.description}`);
      //   } else throw new Error("Idk some error");
      // }

      // TODO: toast and/or mark inputs with red instead of error
      if (!appState.session) throw new Error("Couldn't log in");
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
      <Title>Login - Van N' go</Title>
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
