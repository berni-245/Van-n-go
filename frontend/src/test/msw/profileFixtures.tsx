import { http, HttpResponse } from "msw";
import type { ApiError, Client, Driver } from "@/api/types";
import { CLIENT_ENDPOINT, DRIVER_ENDPOINT } from "@/test/msw/authFixtures";

export const PASSWORD_ENDPOINT = "*/api/users/:id/password";

/** PATCH clients/:id — Profile.tsx's updateProfileAction reads the returned Client straight off this response. */
export function updateClientResponse(client: Client) {
  return http.patch(CLIENT_ENDPOINT, () => HttpResponse.json(client));
}

export function updateClientFieldErrorsResponse(errors: ApiError[], status = 400) {
  return http.patch(CLIENT_ENDPOINT, () => HttpResponse.json(errors, { status }));
}

/** PATCH drivers/:id */
export function updateDriverResponse(driver: Driver) {
  return http.patch(DRIVER_ENDPOINT, () => HttpResponse.json(driver));
}

/**
 * ImageWithFallback.tsx unconditionally fetches a fallback image whenever
 * `src` is falsy (e.g. an empty profilePic). Every test that renders
 * Profile's picture needs this mocked or it hits msw's
 * onUnhandledRequest: "error".
 */
export function defaultProfileImageResponse() {
  return http.get("*/images/default-user.png", () => new HttpResponse("fake-image-bytes", { status: 200 }));
}

/** PATCH users/password?id=... */
export function updatePasswordResponse(status = 204) {
  return http.patch(PASSWORD_ENDPOINT, () => new HttpResponse(null, { status }));
}

export function updatePasswordFieldErrorsResponse(errors: ApiError[], status = 400) {
  return http.patch(PASSWORD_ENDPOINT, () => HttpResponse.json(errors, { status }));
}
