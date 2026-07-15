/* @refresh reload */
import "solid-devtools";
import "@/App.css";
import { render } from "solid-js/web";
import App from "@/App";
import { Navigate, Route, Router } from "@solidjs/router";
import { Link, Meta, MetaProvider, Title } from "@solidjs/meta";
import { AppContextProvider } from "@/app-context/AppContext";
import ParentErrorBoundary from "./components/ParentErrorBoundary";
import RootErrorBoundary from "./components/RootErrorBoundary";
import { AnonRoute, ClientRoute, DriverRoute } from "./components/ProtectedRoute";
import { ROUTER_BASE } from "@/utils/routing";
import { lazy } from "solid-js";

const root = document.getElementById("root");

const Home = lazy(() => import("@/pages/Home"));
const Error404 = lazy(() => import("@/errors/404"));
const Login = lazy(() => import("@/pages/Login"));
const Vehicles = lazy(() => import("@/pages/Vehicles"));
const VehicleEdit = lazy(() => import("@/pages/VehicleEdit"));
const VehicleCreate = lazy(() => import("@/pages/CreateVehicle.tsx"));
const Bookings = lazy(() => import("@/pages/Bookings"));
const BookingCreate = lazy(() => import("@/pages/BookingCreate.tsx"));
const Register = lazy(() => import("@/pages/Register"));
const Search = lazy(() => import("@/pages/Search"));
const Chat = lazy(() => import("@/pages/Chat"));
const ForgotPassword = lazy(() => import("@/pages/ForgotPassword"));
const RecoverPassword = lazy(() => import("@/pages/RecoverPassword"));
const Profile = lazy(() => import("@/pages/Profile"));

if (!root || (import.meta.env.DEV && !(root instanceof HTMLElement))) {
  throw new Error(
    "Root element not found. Did you forget to add it to your index.html? Or maybe the id attribute got misspelled?"
  );
}

const filters = {
  vehicleId: /^\d+$/,
};

render(
  () => (
    <RootErrorBoundary>
      <MetaProvider>
        <Meta charset="utf-8" />
        <Meta name="viewport" content="width=device-width, initial-scale=1" />
        <Link rel="icon" type="image/svg+xml" href="/Logo.svg" />
        <Title>Van N' Go</Title>
        <Router
          base={ROUTER_BASE}
          root={(props) => (
            <AppContextProvider>
              <ParentErrorBoundary>
                <App>{props.children}</App>
              </ParentErrorBoundary>
            </AppContextProvider>
          )}
        >
          <Route path="/" component={AnonRoute}>
            <Route path="/" component={Home} />
            <Route path="/login" component={Login} />
            <Route path="/register" component={Register} />
            <Route path="/search" component={Search} />
            <Route path="/forgot-password" component={ForgotPassword} />
            <Route path="/recover-password" component={RecoverPassword} />
          </Route>
          <Route path="/driver" component={DriverRoute}>
            <Route path="/" component={() => <Navigate href="vehicles" />} />
            <Route path="/vehicles" component={Vehicles} />
            <Route path="/chat" component={Chat} />
            <Route path="/vehicles/:vehicleId" component={VehicleEdit} matchFilters={filters} />
            <Route path="/vehicles/new" component={VehicleCreate} />
            <Route path="/bookings" component={Bookings} />
            <Route path="/bookings" component={Bookings} />
            <Route path="/profile" component={Profile} />
          </Route>
          <Route path="/client" component={ClientRoute}>
            <Route path="/" component={() => <Navigate href="search" />} />
            <Route path="/search" component={Search} />
            <Route path="/bookings" component={Bookings} />
            <Route path="/chat" component={Chat} />
            <Route path="/profile" component={Profile} />
            <Route path={"/bookings/:driverId/new"} component={BookingCreate} />
          </Route>
          <Route path="**" component={Error404} />
        </Router>
      </MetaProvider>
    </RootErrorBoundary>
  ),
  root
);
