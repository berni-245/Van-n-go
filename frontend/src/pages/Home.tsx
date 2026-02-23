import { useTranslator } from "@/app-context/AppContext";
import Button from "../components/Button";
import { FaSolidPeopleGroup, FaSolidSearch, FaSolidTruck } from "solid-icons/fa";

export default function Home() {
  const t = useTranslator();

  return (
    <>
      <div class="relative overflow-hidden px-4 py-5 my-5 text-center">
        <img
          class="absolute top-0 left-0 w-full h-full object-cover object-center brightness-75 blur-sm"
          src="/images/welcome-picture.webp"
          alt="Background"
        />
        <div class="relative text-white">
          <h1 class="mb-3">{t("global.title")}</h1>
          <div class="mx-auto w-full lg:w-1/2">
            <p class="text-xl mb-4">{t("global.page.home.greeting")}</p>
            <Button class="text-xl" category="primary" href="/search" text={t("global.page.home.callToAction")} />
          </div>
        </div>
      </div>
      <section class="py-12 dark:text-on-surface-dark">
        <div class="mx-auto max-w-7xl px-4">
          <div class="grid gap-8 text-center lg:grid-cols-3">
            <div>
              <h2 class="mb-4 text-4xl font-semibold dark:text-on-surface-dark-strong">{t("global.page.home.aboutUs.title")}</h2>
              <p class="text-lg">{t("global.page.home.aboutUs.description")}</p>
            </div>
            <div>
              <h2 class="mb-4 text-4xl font-semibold dark:text-on-surface-dark-strong">{t("global.page.home.services.title")}</h2>
              <ul class="space-y-2 text-lg">
                <li class="flex items-center justify-center gap-4">
                  <i>
                    <FaSolidSearch />
                  </i>
                  {t("global.page.home.services.s1")}
                </li>
                <li class="flex items-center justify-center gap-4">
                  <i>
                    <FaSolidTruck />
                  </i>
                  {t("global.page.home.services.s2")}
                </li>
                <li class="flex items-center justify-center gap-4">
                  <i>
                    <FaSolidPeopleGroup />
                  </i>
                  {t("global.page.home.services.s3")}
                </li>
              </ul>
            </div>
            <div>
              <h2 class="mb-4 text-4xl font-semibold dark:text-on-surface-dark-strong">{t("global.page.home.clientOpinion.title")}</h2>
              <p class="text-lg mb-4">"{t("global.page.home.clientOpinion.description")}"</p>
                <footer class="text-sm text-on-surface dark:text-on-surface-dark opacity-50 before:content-['—_']">
                Belon Tusk, <cite title="Source Title">Space X</cite>
              </footer>
            </div>
          </div>
        </div>
      </section>
    </>
  );
}
