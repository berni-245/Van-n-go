import { screen, waitFor } from "@solidjs/testing-library";
import userEvent from "@testing-library/user-event";
import { server } from "@/test/msw/server";
import { renderRoute } from "@/test/renderRoute";
import { passwordResetFailure, passwordResetSuccess, recordPasswordReset } from "@/test/msw/recoverPasswordFixtures";
import RecoverPassword from "@/pages/RecoverPassword";

const user = userEvent.setup();

// Each field on the page carries a stable `name`, so query by that rather than
// relying on translated label text or DOM order.
function emailInput() {
  return document.querySelector('input[name="email"]') as HTMLInputElement;
}
function tokenInput() {
  return document.querySelector('input[name="token"]') as HTMLInputElement;
}
function newPasswordInput() {
  return document.querySelector('input[name="password"]') as HTMLInputElement;
}
function confirmPasswordInput() {
  return document.querySelector('input[name="confirmPassword"]') as HTMLInputElement;
}
function submitButton() {
  return document.querySelector('button[type="submit"]') as HTMLButtonElement;
}
function errorBanner() {
  return document.querySelector(".text-danger");
}

/**
 * Fills every field on the form. `token` defaults to a value with surrounding
 * whitespace so tests can also assert the component trims it before sending.
 * Passwords match by default; pass a different `confirm` to exercise the
 * client-side mismatch guard.
 */
async function fillForm({
  email = "maria@example.com",
  token = " 1234 ",
  password = "hunter22",
  confirm = "hunter22",
}: { email?: string; token?: string; password?: string; confirm?: string } = {}) {
  await waitFor(() => expect(emailInput()).toBeInTheDocument()); // wait for the form to be mounted
  await user.type(emailInput(), email);
  await user.type(tokenInput(), token);
  await user.type(newPasswordInput(), password);
  await user.type(confirmPasswordInput(), confirm);
}

test("resets the password and redirects to /login on success", async () => {
  server.use(passwordResetSuccess());

  renderRoute("/recover-password", "/recover-password?id=42", RecoverPassword, [
    { path: "/login", component: () => <p>login-stub</p> },
  ]);

  await fillForm();
  await user.click(submitButton());

  expect(await screen.findByText("login-stub")).toBeInTheDocument();
});

test("sends the id from the URL and a trimmed token in the request", async () => {
  const { handler, calls } = recordPasswordReset();
  server.use(handler);

  renderRoute("/recover-password", "/recover-password?id=42", RecoverPassword, [
    { path: "/login", component: () => <p>login-stub</p> },
  ]);

  // Surrounding whitespace within the field's 6-char maxLength, so we can
  // assert the component trims it before sending.
  await fillForm({ token: " 1234 " });
  await user.click(submitButton());

  // wait until the request has actually gone out (navigation confirms it)
  await screen.findByText("login-stub");

  expect(calls).toHaveLength(1);
  expect(calls[0].id).toBe("42");
  expect(calls[0].body).toMatchObject({
    mail: "maria@example.com",
    token: "1234", // whitespace stripped by the component
    password: "hunter22",
  });
});

test("blocks the submit and shows an error when the passwords don't match", async () => {
  const { handler, calls } = recordPasswordReset();
  server.use(handler);

  renderRoute("/recover-password", "/recover-password?id=42", RecoverPassword, [
    { path: "/login", component: () => <p>login-stub</p> },
  ]);

  await fillForm({ password: "hunter22", confirm: "different99" });
  await user.click(submitButton());

  expect(await screen.findByText(/Passwords do not match/i)).toBeInTheDocument();
  expect(calls).toHaveLength(0); // never hit the API
  expect(screen.queryByText("login-stub")).not.toBeInTheDocument(); // still on the form
});

test("shows an error and does not call the API when the URL has no id", async () => {
  const { handler, calls } = recordPasswordReset();
  server.use(handler);

  renderRoute("/recover-password", "/recover-password", RecoverPassword, [
    { path: "/login", component: () => <p>login-stub</p> },
  ]);

  await fillForm();
  await user.click(submitButton());

  await waitFor(() => expect(errorBanner()).toBeInTheDocument());
  expect(calls).toHaveLength(0);
  expect(screen.queryByText("login-stub")).not.toBeInTheDocument();
});

test("shows the token-specific error and stays on the page on a 400 response", async () => {
  server.use(passwordResetFailure(400));

  renderRoute("/recover-password", "/recover-password?id=42", RecoverPassword, [
    { path: "/login", component: () => <p>login-stub</p> },
  ]);

  await fillForm();
  await user.click(submitButton());

  expect(await screen.findByText(/Error resetting password/i)).toBeInTheDocument();
  // the submit handler's `finally` re-enables the button once it has settled
  await waitFor(() => expect(submitButton()).not.toBeDisabled());
  expect(screen.queryByText("login-stub")).not.toBeInTheDocument();
});

test("shows a generic error and stays on the page on an unexpected (500) response", async () => {
  server.use(passwordResetFailure(500));

  renderRoute("/recover-password", "/recover-password?id=42", RecoverPassword, [
    { path: "/login", component: () => <p>login-stub</p> },
  ]);

  await fillForm();
  await user.click(submitButton());

  expect(await screen.findByText(/An unknown error occurred/i)).toBeInTheDocument();
  await waitFor(() => expect(submitButton()).not.toBeDisabled());
  expect(screen.queryByText("login-stub")).not.toBeInTheDocument();
});
