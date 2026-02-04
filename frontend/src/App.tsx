import { Suspense, type Component, JSX, Switch, Match } from "solid-js";
import { useAppState, useTranslator } from "@/app-context/AppContext";
import { AppLayout, Content } from "@components/AppLayout.tsx";
import { Sidebar } from "./components/sidebar/Sidebar";
import { Logo } from "./assets/Logo";
import { FaSolidBook, FaSolidGear, FaSolidSearch, FaSolidTruck, FaSolidUserPlus } from "solid-icons/fa";
import { FiLogIn } from "solid-icons/fi";

const App: Component<{ children: JSX.Element }> = (props) => {
  const [appState, _] = useAppState();
  const t = useTranslator();

  return (
    <AppLayout>
      <Sidebar logo={<Logo />}>
        <Switch>
          <Match when={appState.user?.type === "client"}>
            <Sidebar.Item href="/search" text={t("global.find-freight")} icon={<FaSolidSearch size={20} />} />
            <Sidebar.Item href="/bookings" text={t("global.bookings")} icon={<FaSolidBook size={20} />} />
            <Sidebar.Item href="/settings" text={t("global.settings")} icon={<FaSolidGear size={20} />} />
          </Match>
          <Match when={appState.user?.type === "driver"}>
            <Sidebar.Item href="/vehicles" text={t("global.vehicles")} icon={<FaSolidTruck size={20} />} />
            <Sidebar.Item href="/bookings" text={t("global.bookings")} icon={<FaSolidBook size={20} />} />
            <Sidebar.Item href="/settings" text={t("global.settings")} icon={<FaSolidGear size={20} />} />
          </Match>
          <Match when={!appState.user}>
            <Sidebar.Item href="/login" text={t("global.login")} icon={<FiLogIn size={20} />} />
            <Sidebar.Item href="/register" text={t("global.register")} icon={<FaSolidUserPlus size={20} />} />
          </Match>
        </Switch>
      </Sidebar>
      <Content>
        <Suspense>{props.children}</Suspense>
      </Content>
    </AppLayout>
  );
};

export default App;
