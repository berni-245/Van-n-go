import { useTranslator } from "@/app-context/AppContext";
import Button from "../components/Button";

export default function Home() {
  const t = useTranslator();

  return (
    <div class="relative overflow-hidden px-4 py-5 my-5 text-center">
      <img
        class="absolute top-0 left-0 w-full h-full object-cover object-center brightness-75 blur-sm"
        src="/images/welcome_picture.webp"
        alt="Background"
      />
      <div class="relative dark:text-on-surface-dark">
        <h1 class="mb-3">{t("global.title")}</h1>
        <div class="mx-auto w-full lg:w-1/2">
          <p class="text-xl mb-4">{t("global.page.home.greeting")}</p>
          <Button class="text-xl" type="primary" href="/search" text={t("global.page.home.callToAction")} />
        </div>
      </div>
    </div>
  );
}
