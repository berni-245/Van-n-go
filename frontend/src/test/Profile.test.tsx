import { screen, waitFor } from "@solidjs/testing-library";
import userEvent from "@testing-library/user-event";
import { http, HttpResponse } from "msw";
import { server } from "@/test/msw/server";
import { renderRoute } from "@/test/renderRoute";
import {
  seedLoggedInUser,
  clientResponse,
  driverUserResponse,
  makeClient,
  makeDriverUser,
  CLIENT_ENDPOINT,
} from "@/test/msw/authFixtures";
import { zonesResponse, makeZone } from "@/test/msw/zoneFixtures";
import { zonesByIdResponse } from "@/test/msw/bookingFixtures";
import {
  defaultProfileImageResponse,
  updateClientFieldErrorsResponse,
  updatePasswordResponse,
  updatePasswordFieldErrorsResponse,
} from "@/test/msw/profileFixtures";
import Profile from "@/pages/Profile";

const user = userEvent.setup();

/**
 * user.type() can lose characters to Modal.tsx's focus trap (solid-focus-trap):
 * its MutationObserver can re-grab focus mid-keystroke more than once (not
 * just right after the dialog opens), and how often that happens varies by
 * machine speed. Rather than chase the trap's exact timing, verify the
 * field actually ended up with the intended value and retry if it didn't.
 */
async function typeReliably(el: HTMLInputElement, text: string, attempts = 4) {
  for (let i = 0; i < attempts; i++) {
    await user.clear(el);
    await user.type(el, text);
    if (el.value === text) return;
  }
  throw new Error(`Could not reliably type "${text}" into ${el.name || el.tagName}; last value was "${el.value}"`);
}

const ZONE = makeZone({ id: 1, neighborhood: { name: "Palermo" } });
// EditClientProfile's Combobox options are "{neighborhood}, {province}, {country}"
// (3 parts); ClientProfileInfo's read-only display is "{neighborhood}, {province}" (2 parts).
const ZONE_READ_LABEL = "Palermo, Buenos Aires";

function header() {
  return document.querySelector(".border-b.border-outline.bg-surface h1") as HTMLElement;
}
function actionButtons() {
  return document.querySelectorAll(".mt-10.flex.flex-wrap button");
}
function editProfileButton() {
  return actionButtons()[0] as HTMLButtonElement;
}
function changePasswordButton() {
  return actionButtons()[1] as HTMLButtonElement;
}
function usernameField() {
  return document.querySelector('input[name="username"]') as HTMLInputElement;
}
function descriptionField() {
  return document.querySelector('input[name="description"]') as HTMLInputElement;
}
function oldPasswordField() {
  return document.querySelector('input[name="oldPassword"]') as HTMLInputElement;
}
function newPasswordField() {
  return document.querySelector('input[name="password"]') as HTMLInputElement;
}
function confirmPasswordField() {
  return document.querySelector('input[name="confirmPassword"]') as HTMLInputElement;
}
function formSubmitButton() {
  return document.querySelector('form button[type="submit"]') as HTMLButtonElement;
}

/**
 * Modal.tsx's focus trap (solid-focus-trap) grabs initial focus
 * asynchronously (afterPaint) right after the dialog opens, and its
 * MutationObserver can re-grab focus again shortly after that. Typing
 * immediately after the click races this settling process — focus gets
 * silently stolen mid-keystroke, truncating whatever was being typed. A
 * real user wouldn't type within milliseconds of a modal opening, so this
 * waits for the trap to settle (focus actually lands on oldPassword, the
 * first focusable element in the dialog) before typing.
 */
async function openChangePasswordModal() {
  await user.click(changePasswordButton());
  await waitFor(() => expect(document.activeElement).toHaveAttribute("aria-label", "close modal"));
}

test("shows the client's profile info, including their zone", async () => {
  seedLoggedInUser("client", { id: 9 });
  server.use(
    clientResponse(
      makeClient({
        id: 9,
        username: "maria_client",
        email: "maria@example.com",
        zone: "http://localhost:3000/api/zones/1",
      })
    ),
    zonesByIdResponse({ 1: ZONE }),
    defaultProfileImageResponse()
  );

  renderRoute("/profile", "/profile", Profile);

  await waitFor(() => expect(header()).toHaveTextContent("maria_client"));
  expect(await screen.findByText("maria@example.com")).toBeInTheDocument();
  expect(await screen.findByText(ZONE_READ_LABEL)).toBeInTheDocument();
});

test("shows the driver's profile info, including their rating and description", async () => {
  seedLoggedInUser("driver", { id: 7 });
  server.use(
    driverUserResponse(
      makeDriverUser({ id: 7, username: "juan_driver", description: "Reliable and on time.", rating: 4 })
    ),
    defaultProfileImageResponse()
  );

  renderRoute("/profile", "/profile", Profile);

  await waitFor(() => expect(header()).toHaveTextContent("juan_driver"));
  expect(await screen.findByText("Reliable and on time.")).toBeInTheDocument();
  expect(screen.getByText("4")).toBeInTheDocument();
});

test("updates the client's profile and reflects the new username", async () => {
  seedLoggedInUser("client", { id: 9 });

  // GET and PATCH share this state, the way a real backend would: after a
  // successful edit, Profile.tsx revalidates its profile query, which does
  // a fresh GET — that needs to reflect the update, not the original data.
  let currentClient = makeClient({ id: 9, username: "maria_client", zone: "http://localhost:3000/api/zones/1" });
  server.use(
    http.get(CLIENT_ENDPOINT, () => HttpResponse.json(currentClient)),
    http.patch(CLIENT_ENDPOINT, async ({ request }) => {
      currentClient = { ...currentClient, ...((await request.json()) as object) };
      return HttpResponse.json(currentClient);
    }),
    zonesResponse([ZONE]),
    zonesByIdResponse({ 1: ZONE }),
    defaultProfileImageResponse()
  );

  renderRoute("/profile", "/profile", Profile);
  await waitFor(() => expect(header()).toHaveTextContent("maria_client"));

  await user.click(editProfileButton());
  await waitFor(() => expect(document.querySelector('input[name="zoneId"]')).toBeInTheDocument()); // EditClientProfile's zone list finished loading

  await typeReliably(usernameField(), "maria_updated");
  await user.click(formSubmitButton());

  await waitFor(() => expect(header()).toHaveTextContent("maria_updated"));
  // Not asserting the dialog unmounts: Modal.tsx's exit transition
  // (solid-transition-group) waits for a real `transitionend` event, which
  // jsdom never fires, so the closed dialog's content stays mounted
  // indefinitely in tests even though isOpen() is correctly false.
});

test("shows a field-level error under the relevant field when updating the profile fails validation", async () => {
  seedLoggedInUser("client", { id: 9 });
  server.use(
    clientResponse(makeClient({ id: 9, zone: "http://localhost:3000/api/zones/1" })),
    zonesResponse([ZONE]),
    zonesByIdResponse({ 1: ZONE }),
    defaultProfileImageResponse(),
    updateClientFieldErrorsResponse([
      { type: "ValidationError", entity: "username", description: "That username is already taken." },
    ])
  );

  renderRoute("/profile", "/profile", Profile);
  await waitFor(() => expect(header()).toBeInTheDocument());

  await user.click(editProfileButton());
  await waitFor(() => expect(document.querySelector('input[name="zoneId"]')).toBeInTheDocument());
  await user.click(formSubmitButton());

  expect(await screen.findByText(/That username is already taken\./)).toBeInTheDocument();
  expect(usernameField()).toBeInTheDocument(); // dialog stays open
});

test("changes the password successfully", async () => {
  seedLoggedInUser("client", { id: 9 });
  server.use(
    clientResponse(makeClient({ id: 9, zone: "" })),
    defaultProfileImageResponse(),
    updatePasswordResponse(204)
  );

  renderRoute("/profile", "/profile", Profile);
  await waitFor(() => expect(header()).toBeInTheDocument());

  await openChangePasswordModal();
  await typeReliably(oldPasswordField(), "old-secret-1");
  await typeReliably(newPasswordField(), "new-secret-1");
  await typeReliably(confirmPasswordField(), "new-secret-1");
  await user.click(formSubmitButton());

  // Not asserting the dialog unmounts: see the note on the profile-update
  // test above. The submit button re-enabling is a reliable signal that the
  // submission actually settled (successfully — none of the field errors
  // that would otherwise appear are present).
  await waitFor(() => expect(formSubmitButton()).not.toBeDisabled());
  expect(screen.queryByText(/do not match/)).not.toBeInTheDocument();
});

test("shows a client-side mismatch error without calling the API when confirmation doesn't match", async () => {
  seedLoggedInUser("client", { id: 9 });
  server.use(clientResponse(makeClient({ id: 9, zone: "" })), defaultProfileImageResponse());
  // Deliberately no updatePasswordResponse mock: if the app called the API
  // here, msw's onUnhandledRequest: "error" would fail the test — proving
  // this check really does happen client-side, before any request goes out.

  renderRoute("/profile", "/profile", Profile);
  await waitFor(() => expect(header()).toBeInTheDocument());

  await openChangePasswordModal();
  await typeReliably(oldPasswordField(), "old-secret-1");
  await typeReliably(newPasswordField(), "new-secret-1");
  await typeReliably(confirmPasswordField(), "totally-different");
  await user.click(formSubmitButton());

  expect(await screen.findByText(/Passwords do not match/)).toBeInTheDocument();
});

test("shows an error toast (not a field error) when the password API rejects the change", async () => {
  seedLoggedInUser("client", { id: 9 });
  server.use(
    clientResponse(makeClient({ id: 9, zone: "" })),
    defaultProfileImageResponse(),
    updatePasswordFieldErrorsResponse([{ type: "ValidationError", description: "Old password is incorrect." }])
  );

  renderRoute("/profile", "/profile", Profile);
  await waitFor(() => expect(header()).toBeInTheDocument());

  await openChangePasswordModal();
  await typeReliably(oldPasswordField(), "wrong-password");
  await typeReliably(newPasswordField(), "new-secret-1");
  await typeReliably(confirmPasswordField(), "new-secret-1");
  await user.click(formSubmitButton());

  expect(await screen.findByText(/Old password is incorrect\./, {}, { timeout: 5000 })).toBeInTheDocument();
});
