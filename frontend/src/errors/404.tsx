import { Title } from "@solidjs/meta";
import Button from "@/components/Button";
import { useTranslator } from "../app-context/AppContext";

export default function NotFound() {
  const t = useTranslator();

  return (
    <>
      <Title>404 • Van N' Go</Title>

      <main class="flex min-h-[calc(100vh-4rem)] items-center justify-center px-6">
        <section class="mx-auto max-w-lg text-center">
          <p class="text-sm font-semibold uppercase tracking-[0.35em] text-primary">{t("errors.404.name")}</p>

          <h1 class="mt-2 text-8xl font-extrabold leading-none text-base-content">404</h1>

          <h2 class="mt-6 text-3xl font-bold text-base-content">{t("errors.404.title")}</h2>

          <p class="mt-4 text-base-content/70">{t("errors.404.description")}</p>

          <div class="mt-10 flex flex-wrap justify-center gap-4">
            <Button text="Go home" href="/" class="btn btn-primary" category="primary" />

            <Button text="Go back" onClick={() => history.back()} category="secondary" />
          </div>

          <p class="mt-4 text-sm italic text-base-content/50">{t("errors.404.cheekyMsg")}</p>
        </section>
      </main>
    </>
  );
}
