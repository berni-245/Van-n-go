import { screen, waitFor } from "@solidjs/testing-library";
import userEvent from "@testing-library/user-event";
import { http, HttpResponse } from "msw";
import { server } from "@/test/msw/server";
import { renderRoute } from "@/test/renderRoute";
import { zonesResponse, makeZone } from "@/test/msw/zoneFixtures";
import { authSuccessResponse, clientResponse, makeClient, driverUserResponse, makeDriverUser } from "@/test/msw/authFixtures";
import Register from "@/pages/Register";

const user = userEvent.setup();

const PALERMO = makeZone({ id: 1, neighborhood: { name: "Palermo" } });
const PALERMO_LABEL = "Palermo, Buenos Aires, Argentina";

function usernameInput() {
  return document.querySelector('input[name="username"]') as HTMLInputElement;
}
function emailInput() {
  return document.querySelector('input[name="email"]') as HTMLInputElement;
}
function passwordInput() {
  return document.querySelector('input[name="password"]') as HTMLInputElement;
}
function confirmedPasswordInput() {
  return document.querySelector('input[name="confirmedPassword"]') as HTMLInputElement;
}
function descriptionInput() {
  return document.querySelector('input[name="description"]') as HTMLInputElement;
}
// The zone query field is the only textbox on the page that doesn't have a
// `name` attribute (ZoneInput doesn't expose one), so it can be picked out
// from the rest by elimination instead of relying on DOM order.
function zoneQueryInput() {
  return screen.getAllByRole("textbox").find((el) => !el.getAttribute("name")) as HTMLInputElement;
}
function userTypeSelect() {
  return document.querySelector("select") as HTMLSelectElement;
}
function submitButton() {
  return document.querySelector('button[type="submit"]') as HTMLButtonElement;
}

async function fillCommonFields() {
  await user.type(usernameInput(), "maria_client");
  await user.type(emailInput(), "maria@example.com");
  await user.type(passwordInput(), "hunter22");
  await user.type(confirmedPasswordInput(), "hunter22");
}

async function pickZone() {
  const zoneInput = zoneQueryInput();
  await user.click(zoneInput);
  await user.type(zoneInput, "Palermo");
  await user.click(await screen.findByText(PALERMO_LABEL));
}

test("registers a new client and redirects to their dashboard", async () => {
  server.use(
    zonesResponse([PALERMO]),
    http.post("*/api/clients", () => new HttpResponse(null, { status: 201 })),
    authSuccessResponse("client", 9),
    clientResponse(makeClient({ id: 9 })),
  );

  renderRoute("/register", "/register", Register, [{ path: "/client", component: () => <p>client-dashboard-stub</p> }]);

  await fillCommonFields();
  await pickZone();
  await user.click(submitButton());

  expect(await screen.findByText("client-dashboard-stub")).toBeInTheDocument();
});

test("registers a new driver (no zone required) and redirects to their dashboard", async () => {
  server.use(
    http.post("*/api/drivers", () => new HttpResponse(null, { status: 201 })),
    authSuccessResponse("driver", 4),
    driverUserResponse(makeDriverUser({ id: 4 })),
  );

  renderRoute("/register", "/register", Register, [{ path: "/driver", component: () => <p>driver-dashboard-stub</p> }]);

  await user.selectOptions(userTypeSelect(), "driver");
  await fillCommonFields();
  await user.type(descriptionInput(), "Own truck, available weekends.");
  await user.click(submitButton());

  expect(await screen.findByText("driver-dashboard-stub")).toBeInTheDocument();
});

test("shows a field-level error returned by the API next to the relevant field", async () => {
  server.use(
    zonesResponse([PALERMO]),
    http.post("*/api/clients", () =>
      HttpResponse.json(
        [
          {
            type: "ValidationError",
            entity: "email",
            // A subtype that (almost certainly) has no translation in the app's
            // dictionary, so the component falls back to `description` verbatim
            // — letting this test assert on exact text regardless of locale.
            subtype: "AlreadyRegisteredTestSubtype",
            description: "That email is already registered.",
          },
        ],
        { status: 400 },
      ),
    ),
  );

  renderRoute("/register", "/register", Register);

  await fillCommonFields();
  await pickZone();
  await user.click(submitButton());

  expect(await screen.findByText(/That email is already registered\./)).toBeInTheDocument();
  // still on the registration form
  expect(usernameInput()).toBeInTheDocument();
});

test("shows a general error banner for a non-validation API error", async () => {
  server.use(
    zonesResponse([PALERMO]),
    http.post(
      "*/api/clients",
      () => HttpResponse.json({ message: "Something broke upstream." }, { status: 500 }),
    ),
  );

  renderRoute("/register", "/register", Register);

  await fillCommonFields();
  await pickZone();
  await user.click(submitButton());

  expect(await screen.findByText(/Something broke upstream\./)).toBeInTheDocument();
});
