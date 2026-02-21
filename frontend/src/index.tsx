/* @refresh reload */
import "solid-devtools";
import "@/App.css";
import { render } from "solid-js/web";
import App from "@/App";
import { Router, Route, Navigate } from "@solidjs/router";
import { Link, Meta, MetaProvider, Title } from "@solidjs/meta";
import { AppContextProvider } from "@/app-context/AppContext";
import { worker } from "@/api-mock/browser";
import ParentErrorBoundary from "./components/ParentErrorBoundary";
import { AnonRoute, DriverRoute } from "./components/ProtectedRoute";
import { lazy } from "solid-js";

const root = document.getElementById("root");

// TODO: lazy load all components.
const Home = lazy(() => import("@/pages/Home"));
const Error404 = lazy(() => import("@/errors/404"));
const About = lazy(() => import("@/pages/About"));
const Login = lazy(() => import("@/pages/Login"));
const DriverAvailability = lazy(() => import("@/pages/DriverAvailability"));
const Settings = lazy(() => import("@/pages/Settings"));
const Vehicles = lazy(() => import("@/pages/Vehicles"));

if (!root || (import.meta.env.DEV && !(root instanceof HTMLElement))) {
  throw new Error(
    "Root element not found. Did you forget to add it to your index.html? Or maybe the id attribute got misspelled?"
  );
}

async function enableMocking() {
  if (process.env.NODE_ENV !== "development") return;

  // `worker.start()` returns a Promise that resolves
  // once the Service Worker is up and ready to intercept requests.
  return worker.start();
}

// await enableMocking();

// const filters = {
//   driverId: /^\d+$/,
//   vehicleId: /^\d+$/,
// };

render(
  () => (
    // TODO: this error boundary should probably be changed
    <ParentErrorBoundary>
      <MetaProvider>
        <Meta charset="utf-8" />
        <Meta name="viewport" content="width=device-width, initial-scale=1" />
        <Link rel="icon" type="image/svg+xml" href="/Logo.svg" />
        <Title>Van N' Go</Title>
        <Router
          root={(props) => (
            <AppContextProvider>
              <App>{props.children}</App>
            </AppContextProvider>
          )}
        >
          <Route path="/" component={AnonRoute}>
            <Route path="/" component={Home} />
            <Route path="/login" component={Login} />
            <Route path="/search" component={DriverAvailability} />
            <Route path="/about" component={About} />
          </Route>
          <Route path="/driver" component={DriverRoute}>
            <Route path="/" component={() => <Navigate href="vehicles" />} />
            <Route path="/vehicles" component={Vehicles} />
            <Route path="/bookings" component={Vehicles} />
            <Route path="/settings" component={Settings} />
          </Route>
          <Route path="**" component={Error404} />
        </Router>
      </MetaProvider>
    </ParentErrorBoundary>
  ),
  root
);
