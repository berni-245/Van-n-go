import { Suspense, type Component, JSX } from "solid-js";
import { A, useLocation } from "@solidjs/router";
import { useAppState, useTranslator } from "@/app-context/AppContext";
import { AppLayout, Content } from "@components/AppLayout.tsx";
import { Sidebar } from "./components/sidebar/Sidebar";
import { Logo } from "./assets/Logo";
import { FaSolidGear } from "solid-icons/fa";

const App: Component<{ children: JSX.Element }> = (props) => {
  const location = useLocation();
  const [appState, setAppState] = useAppState();
  const t = useTranslator();

  return (
    <AppLayout>
      <Sidebar logo={<Logo />}>
        <Sidebar.Item href="/drivers" text="Drivers" onClick={() => alert("lalalal")} />
        <Sidebar.Item href="/settings" text={t("global.settings")} icon={<FaSolidGear size={20} />} />
      </Sidebar>
      <Content>
        <nav class="bg-gray-200 text-gray-900 px-4">
          <ul class="flex items-center">
            <li class="py-2 px-4">
              <A href="/" class="no-underline hover:underline">
                {t("global.home")}
              </A>
            </li>
            <li class="py-2 px-4">
              <A href="/about" class="no-underline hover:underline">
                About
              </A>
            </li>
            <li class="py-2 px-4">
              <A href="/error" class="no-underline hover:underline">
                Error
              </A>
            </li>

            <li class="text-sm flex items-center space-x-1 ml-auto">
              <span>URL:</span>
              <input class="w-75px p-1 bg-white text-sm rounded-lg" type="text" readOnly value={location.pathname} />
            </li>
          </ul>
        </nav>

        <Suspense>{props.children}</Suspense>
      </Content>
    </AppLayout>
  );
};

export default App;
