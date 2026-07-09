import { http, HttpResponse } from "msw";
import type { Client, Driver } from "@/api/types";

// api.basicAuth() sends `GET {API_ROOT}` (a plain fetch with a Basic auth
// header, no trailing path) and expects the server to answer with
// `vango-auth-token` / `vango-refresh-token` headers on success.
export const AUTH_ENDPOINT = "*/api/";
export const CLIENT_ENDPOINT = "*/api/clients/:id";
export const DRIVER_ENDPOINT = "*/api/drivers/:id";

function base64UrlEncode(value: unknown): string {
  const base64 = Buffer.from(JSON.stringify(value), "utf-8").toString("base64");
  return base64.replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/, "");
}

/**
 * Builds an unsigned-but-well-formed JWT. jwt-decode (used by api.ts) only
 * base64-decodes the payload, it never verifies the signature, so this is
 * enough to exercise the app's post-login code path.
 */
export function makeJwt(payload: Record<string, unknown>): string {
  const header = base64UrlEncode({ alg: "none", typ: "JWT" });
  const body = base64UrlEncode(payload);
  return `${header}.${body}.test-signature`;
}

function tokenPayload(type: "client" | "driver", id: number, tokenType: "auth" | "refresh") {
  const now = Math.floor(Date.now() / 1000);
  return { type, id, tokenType, iat: now, exp: now + 3600 };
}

/** Responds with fresh auth+refresh tokens for the given user type/id, as if credentials were valid. */
export function authSuccessResponse(userType: "client" | "driver", id = 1) {
  return http.get(AUTH_ENDPOINT, () => {
    const auth = makeJwt(tokenPayload(userType, id, "auth"));
    const refresh = makeJwt(tokenPayload(userType, id, "refresh"));
    return new HttpResponse(null, {
      status: 200,
      headers: { "vango-auth-token": auth, "vango-refresh-token": refresh },
    });
  });
}

/** Simulates invalid credentials: no tokens are issued. */
export function authFailureResponse(status = 401) {
  return http.get(AUTH_ENDPOINT, () => new HttpResponse(null, { status }));
}

export function makeClient(overrides: Partial<Client> = {}): Client {
  return {
    id: 1,
    username: "maria_client",
    email: "maria@example.com",
    preferredLanguage: "SPANISH",
    profilePic: "",
    self: "/api/clients/1",
    pendingBookings: "",
    acceptedBookings: "",
    finishedBookings: "",
    rejectedBookings: "",
    canceledBookings: "",
    zone: "/api/zones/1",
    ...overrides,
  };
}

export function makeDriverUser(overrides: Partial<Driver> = {}): Driver {
  return {
    id: 1,
    username: "juan_driver",
    email: "juan@example.com",
    preferredLanguage: "SPANISH",
    profilePic: "",
    self: "/api/drivers/1",
    pendingBookings: "",
    acceptedBookings: "",
    finishedBookings: "",
    rejectedBookings: "",
    canceledBookings: "",
    description: "Reliable driver",
    vehicles: "/api/drivers/1/vehicles",
    ...overrides,
  };
}

/** The `getClient`/`getUser` call that follows a successful login. */
export function clientResponse(client: Client) {
  return http.get(CLIENT_ENDPOINT, () => HttpResponse.json(client));
}

/** The `getDriver`/`getUser` call that follows a successful login. */
export function driverUserResponse(driver: Driver) {
  return http.get(DRIVER_ENDPOINT, () => HttpResponse.json(driver));
}
