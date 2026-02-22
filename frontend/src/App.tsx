import { useAppState, useTranslator } from "@/app-context/AppContext";
import { AppLayout } from "@components/AppLayout.tsx";
import { Sidebar } from "@components/sidebar/Sidebar";
import {
  FaSolidAdd,
  FaSolidBook,
  FaSolidGear,
  FaSolidListDots,
  FaSolidSearch,
  FaSolidTruck,
  FaSolidUserPlus,
  FaSolidUserAlt,
} from "solid-icons/fa";
import { FiLogIn } from "solid-icons/fi";
import { JSX, Match, Suspense, Switch, type Component } from "solid-js";
import { Logo } from "@/assets/Logo";

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
            <Sidebar.Item href="/client/profile" text={t("global.profile")} icon={<FaSolidUserAlt size={20} />} />
          </Match>
          <Match when={appState.user?.type === "driver"}>
            <Sidebar.CollapsibleItem
              text={t("global.page.vehicles.sidebar.title")}
              icon={<FaSolidTruck size={20} />}
              beginExpanded
            >
              <Sidebar.Item
                href={`/driver/vehicles`}
                text={t("global.page.vehicles.sidebar.allVehicles")}
                icon={<FaSolidListDots size={20} />}
              />
              <Sidebar.Item
                href={`/driver/vehicle/new`}
                text={t("global.page.vehicles.sidebar.newVehicle")}
                icon={<FaSolidAdd size={20} />}
              />
            </Sidebar.CollapsibleItem>
            <Sidebar.Item href="/driver/profile" text={t("global.profile")} icon={<FaSolidUserAlt size={20} />} />
            <Sidebar.Item href="/driver/bookings" text={t("global.bookings")} icon={<FaSolidBook size={20} />} />
            <Sidebar.Item href="/driver/settings" text={t("global.settings")} icon={<FaSolidGear size={20} />} />
          </Match>
          <Match when={!appState.user}>
            <Sidebar.Item href="/login" text={t("global.login")} icon={<FiLogIn size={20} />} />
            <Sidebar.Item href="/register" text={t("global.register")} icon={<FaSolidUserPlus size={20} />} />
          </Match>
        </Switch>
      </Sidebar>
      <AppLayout.Content>
        {/* TODO: prolly remove this suspense when the app is finished */}
        <Suspense fallback="This is the app suspense, you should probably be handling this in a child component">
          {props.children}
        </Suspense>
      </AppLayout.Content>
    </AppLayout>
  );
};

export default App;
