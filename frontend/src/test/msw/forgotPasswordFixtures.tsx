import { http, HttpResponse } from "msw";
import type { RequestPasswordToken } from "@/api/types";

// ForgotPassword.tsx calls api.requestPasswordToken({ mail }), which issues
// `POST {API_ROOT}users`. API_ROOT resolves to `${origin}${BASE_URL}api/`, so
// the path always ends in `/api/users` regardless of the configured base path.
export const REQUEST_TOKEN_ENDPOINT = "*/api/users";

/** The request succeeds: a recovery token is (conceptually) emailed to the user. */
export function requestTokenSuccess() {
  return http.post(REQUEST_TOKEN_ENDPOINT, () => new HttpResponse(null, { status: 200 }));
}

/**
 * The request is rejected. The backend answers 400 (bad request) or 404 (no
 * user with that email) for an address the component can't send a token to —
 * both of which the component maps to the same "check your email" message. Any
 * other status is treated by the component as an unexpected failure.
 */
export function requestTokenFailure(status = 400) {
  return http.post(REQUEST_TOKEN_ENDPOINT, () => new HttpResponse(null, { status }));
}

/**
 * Like requestTokenSuccess(), but records every request so a test can assert on
 * what the component actually sent — the body ({ mail }). Handy for verifying
 * the entered email is forwarded to the API unchanged.
 */
export function recordRequestToken(status = 200) {
  const calls: { body: RequestPasswordToken }[] = [];
  const handler = http.post(REQUEST_TOKEN_ENDPOINT, async ({ request }) => {
    calls.push({ body: (await request.json()) as RequestPasswordToken });
    return new HttpResponse(null, { status });
  });
  return { handler, calls };
}
