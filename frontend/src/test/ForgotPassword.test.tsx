import {screen, waitFor} from "@solidjs/testing-library";
import userEvent from "@testing-library/user-event";
import {server} from "@/test/msw/server";
import {renderRoute} from "@/test/renderRoute";
import {recordRequestToken, requestTokenFailure, requestTokenSuccess,} from "@/test/msw/forgotPasswordFixtures";
import ForgotPassword from "@/pages/ForgotPassword";

const user = userEvent.setup();

// The email field carries a stable `name`, so query by that rather than
// relying on translated label text or DOM order.
function emailInput() {
  return document.querySelector('input[name="email"]') as HTMLInputElement;
}
function submitButton() {
  return document.querySelector('button[type="submit"]') as HTMLButtonElement;
}
function errorBanner() {
  return document.querySelector(".text-danger");
}

async function fillEmail(email = "maria@example.com") {
  await waitFor(() => expect(emailInput()).toBeInTheDocument()); // wait for the form to be mounted
  await user.type(emailInput(), email);
}

test("requests a token and shows the success message, hiding the form", async () => {
  server.use(requestTokenSuccess());

  renderRoute("/forgot-password", "/forgot-password", ForgotPassword);

  await fillEmail();
  await user.click(submitButton());

  // On success the form (its email field) is unmounted — the unambiguous
  // signal that the request settled successfully.
  await waitFor(() => expect(emailInput()).not.toBeInTheDocument());
  // The confirmation copy is shown. It renders in *two* places — the in-page
  // banner and a toast (both fed the same string) — so match all of them
  // rather than expecting a single element.
  expect(
    screen.getAllByText(/a token will be sent to the indicated inbox/i).length
  ).toBeGreaterThan(0);
});

test("sends the entered email as `mail` in the request body", async () => {
  const { handler, calls } = recordRequestToken();
  server.use(handler);

  renderRoute("/forgot-password", "/forgot-password", ForgotPassword);

  await fillEmail("someone@example.com");
  await user.click(submitButton());

  // Wait until the request has settled: the form is replaced by the success
  // state once the API answers.
  await waitFor(() => expect(emailInput()).not.toBeInTheDocument());

  expect(calls).toHaveLength(1);
  expect(calls[0].body).toEqual({ mail: "someone@example.com" });
});

test("does not call the API when the email is empty", async () => {
  const { handler, calls } = recordRequestToken();
  server.use(handler);

  renderRoute("/forgot-password", "/forgot-password", ForgotPassword);

  await waitFor(() => expect(submitButton()).toBeInTheDocument());
  await user.click(submitButton());

  expect(calls).toHaveLength(0);
  // Still on the form, no success message.
  expect(emailInput()).toBeInTheDocument();
  expect(screen.queryByText(/a token will be sent to the indicated inbox/i)).not.toBeInTheDocument();
});

test("shows the email-specific error and stays on the form on a 400 response", async () => {
  server.use(requestTokenFailure(400));

  renderRoute("/forgot-password", "/forgot-password", ForgotPassword);

  await fillEmail();
  await user.click(submitButton());

  expect(await screen.findByText(/Error requesting token/i)).toBeInTheDocument();
  // the submit handler's `finally` re-enables the button once it has settled
  await waitFor(() => expect(submitButton()).not.toBeDisabled());
  // still on the form, no success message
  expect(emailInput()).toBeInTheDocument();
});

test("shows the same email-specific error on a 404 response", async () => {
  server.use(requestTokenFailure(404));

  renderRoute("/forgot-password", "/forgot-password", ForgotPassword);

  await fillEmail();
  await user.click(submitButton());

  expect(await screen.findByText(/Error requesting token/i)).toBeInTheDocument();
  await waitFor(() => expect(submitButton()).not.toBeDisabled());
  expect(emailInput()).toBeInTheDocument();
});

test("shows an error banner and stays on the form on an unexpected (500) response", async () => {
  server.use(requestTokenFailure(500));

  renderRoute("/forgot-password", "/forgot-password", ForgotPassword);

  await fillEmail();
  await user.click(submitButton());

  // An error banner appears (a different, generic branch than the 400/404 one)...
  await waitFor(() => expect(errorBanner()).toBeInTheDocument());
  // ...and it is NOT the email-specific message.
  expect(screen.queryByText(/Error requesting token/i)).not.toBeInTheDocument();
  await waitFor(() => expect(submitButton()).not.toBeDisabled());
  expect(emailInput()).toBeInTheDocument();
});
