import { screen, waitFor } from "@solidjs/testing-library";
import userEvent from "@testing-library/user-event";
import { http, HttpResponse } from "msw";
import { Navigate } from "@solidjs/router";
import { server } from "@/test/msw/server";
import { renderRoute } from "@/test/renderRoute";
import {
  authSuccessResponse,
  authFailureResponse,
  clientResponse,
  driverUserResponse,
  makeClient,
  makeDriverUser,
  CLIENT_ENDPOINT,
} from "@/test/msw/authFixtures";
import Login from "@/pages/Login";

const user = userEvent.setup();

// Login.tsx doesn't set `name` on the username field itself, but the app's
// translated labels aren't available in this project snapshot, so we query
// by the stable attributes instead of translated label text.
function usernameInput() {
  return document.querySelector('input[name="username"]') as HTMLInputElement;
}
function passwordInput() {
  return document.querySelector('input[name="password"]') as HTMLInputElement;
}
function submitButton() {
  return document.querySelector('button[type="submit"]') as HTMLButtonElement;
}

async function submitLoginForm() {
  await screen.findByRole("textbox"); // wait for the Login form itself to be mounted
  await user.type(usernameInput(), "someuser");
  await user.type(passwordInput(), "hunter2");
  await user.click(submitButton());
}

test("logs in and redirects to the user's own dashboard on success", async () => {
  server.use(authSuccessResponse("driver", 7), driverUserResponse(makeDriverUser({ id: 7 })));

  renderRoute("/login", "/login", Login, [{ path: "/driver", component: () => <p>driver-dashboard-stub</p> }]);
  await submitLoginForm();

  expect(await screen.findByText("driver-dashboard-stub")).toBeInTheDocument();
});

test("redirects back to the page the user came from, if one was provided", async () => {
  server.use(authSuccessResponse("client", 3), clientResponse(makeClient({ id: 3 })));

  // Mirrors how ProtectedRoute actually redirects an unauthenticated user:
  // `<Navigate href="/login" state={{ from: "..." }} />`. Starting the test
  // there (rather than pre-seeding history) is what makes location.state
  // reach Login for real.
  renderRoute("/intercepted", "/intercepted", () => <Navigate href="/login" state={{ from: "/vehicles" }} />, [
    { path: "/login", component: Login },
    { path: "/vehicles", component: () => <p>vehicles-stub</p> },
  ]);
  await submitLoginForm();

  expect(await screen.findByText("vehicles-stub")).toBeInTheDocument();
});

test("shows an error and stays on the page when credentials are rejected", async () => {
  server.use(authFailureResponse(401));

  renderRoute("/login", "/login", Login, [{ path: "/client", component: () => <p>client-dashboard-stub</p> }]);
  await submitLoginForm();

  // wait for the submit handler's `finally` to re-enable the button, i.e. for it to have settled
  await waitFor(() => expect(submitButton()).not.toBeDisabled());

  expect(screen.queryByText("client-dashboard-stub")).not.toBeInTheDocument();
  expect(document.querySelector(".text-danger")).toBeInTheDocument(); // the error banner
  expect(usernameInput()).toBeInTheDocument(); // still on the login form
});

test("does not navigate if fetching the profile after a successful login fails", async () => {
  server.use(
    authSuccessResponse("client", 5),
    http.get(CLIENT_ENDPOINT, () => new HttpResponse(null, { status: 500 }))
  );

  renderRoute("/login", "/login", Login, [{ path: "/client", component: () => <p>client-dashboard-stub</p> }]);
  await submitLoginForm();

  await waitFor(() => expect(submitButton()).not.toBeDisabled());
  expect(screen.queryByText("client-dashboard-stub")).not.toBeInTheDocument();
});
