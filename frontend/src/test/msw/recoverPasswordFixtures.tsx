import {http, HttpResponse} from "msw";
import type {RecoverPassword} from "@/api/types";

// RecoverPassword.tsx calls api.updateUserPasswordWithToken(id, ...), which
// issues `PATCH {API_ROOT}users/{id}/password`. API_ROOT resolves to
// `${origin}${BASE_URL}api/`, so the path always ends in
// `/api/users/:id/password` regardless of the configured base path.
export const PASSWORD_RESET_ENDPOINT = "*/api/users/:id/password";

/** The reset succeeds: the token was valid and the password was updated. */
export function passwordResetSuccess() {
  return http.patch(PASSWORD_RESET_ENDPOINT, () => new HttpResponse(null, { status: 200 }));
}

/**
 * The reset is rejected. The backend answers 400 for a bad/expired token
 * (which the component maps to a specific "check your token" message); any
 * other status is treated by the component as an unexpected failure.
 */
export function passwordResetFailure(status = 400) {
  return http.patch(PASSWORD_RESET_ENDPOINT, () => new HttpResponse(null, { status }));
}

/**
 * Like passwordResetSuccess(), but records every request so a test can assert
 * on what the component actually sent — the `id` taken from the URL's `?id=`
 * param, and the body (mail/token/password). Handy for verifying, e.g., that
 * the token is trimmed before being sent.
 */
export function recordPasswordReset(status = 200) {
  const calls: { id: string; body: RecoverPassword }[] = [];
  const handler = http.patch(PASSWORD_RESET_ENDPOINT, async ({ request, params }) => {
    calls.push({ id: String(params.id), body: (await request.json()) as RecoverPassword });
    return new HttpResponse(null, { status });
  });
  return { handler, calls };
}
