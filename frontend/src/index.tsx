/* @refresh reload */
import "solid-devtools";
import "@/App.css";
import { render } from "solid-js/web";
import App from "@/App";
import { Router, Route } from "@solidjs/router";
import { MetaProvider } from "@solidjs/meta";
import { AppContextProvider } from "@/app-context/AppContext";
import Home from "@/pages/Home";
import About from "@/pages/About";
import Error404 from "@/errors/404";
import { worker } from "@/api-mock/browser";
import DriverAvailability from "@/pages/DriverAvailability";
import Settings from "@/pages/Settings";
import Login from "./pages/Login";

const root = document.getElementById("root");

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

await enableMocking();

render(
  () => (
    <MetaProvider>
      <AppContextProvider>
        <Router root={(props) => <App>{props.children}</App>}>
          <Route path="/" component={Home} />
          <Route path="/login" component={Login} />
          <Route path="/search" component={DriverAvailability} />
          <Route path="/about" component={About} />
          <Route path="/settings" component={Settings} />
          <Route path="**" component={Error404} />
        </Router>
      </AppContextProvider>
    </MetaProvider>
  ),
  root
);
