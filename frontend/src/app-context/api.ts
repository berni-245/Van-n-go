import { TokenPayload } from "@/api/jwt";
import { Client, Driver, UpdateClient, UpdateDriver, Vehicle, Zone } from "@/api/types";
import { AppState } from "@/app-context/appState";
import { jwtDecode } from "jwt-decode";
import { SetStoreFunction } from "solid-js/store";
import { clientToUserState, driverToUserState } from "./userState";
import { createMediaType as createMediaTypes } from "../api/media-types";
import { useLocation, useNavigate } from "@solidjs/router";

// export const API_ROOT = `${window.location.protocol}//${window.location.host}/api/`;
export const API_ROOT = `http://${window.location.hostname}:8080/api/`;
export type Api = ReturnType<typeof initializeApi>;

export interface LocationState {
  from: string;
}

const MediaTypesV1 = createMediaTypes("v1");

interface VisibilityOpts {
  visibility?: "public" | "all";
}

const sleep = (ms: number) => new Promise((r) => setTimeout(r, ms));

export function initializeApi(appState: AppState, setAppState: SetStoreFunction<AppState>) {
  const location = useLocation();
  const navigate = useNavigate();

  return {
    async authFetch(input: string | URL | Request): Promise<Response> {
      const request = input instanceof Request ? input : new Request(input);

      if (appState.session) {
        if (!jwtExpired(appState.session.payload.auth)) {
          request.headers.set("Authorization", `Bearer ${appState.session.token.auth}`);
        } else if (!jwtExpired(appState.session.payload.refresh)) {
          console.log("Expired auth token. Sending refresh...");
          request.headers.set("Authorization", `Bearer ${appState.session.token.refresh}`);
        } else {
          console.log("Expired tokens. Session reset");
          this.logout("/login", location.pathname);
          // TODO: maybe add a toast telling user the session has expired? Not sure if this is the
          // right place to add it tho...
        }
      }

      // await sleep(1000);
      const res = await fetch(request);

      if (res.status === 401) {
        // NOTE: we could get here for example if the user altered the jwt to change the expiration time.
        console.log("Invalid credentials. Session reset");
        this.logout("/login", location.pathname);
      } else {
        const authToken = res.headers.get("vango-auth-token");
        const refreshToken = res.headers.get("vango-refresh-token");

        if (authToken && refreshToken) {
          console.log("Got new tokens from server. Refreshing...");
          setAppState("session", {
            token: { auth: authToken, refresh: refreshToken },
            payload: { auth: jwtDecode(authToken), refresh: jwtDecode(refreshToken) },
          });
        }
      }

      return res;
    },

    async basicAuth(username: string, password: string) {
      const credentials = btoa(`${username}:${password}`);
      const req = apiRequest("", {
        headers: {
          Authorization: `Basic ${credentials}`,
        },
      });
      await this.authFetch(req);
    },

    async logout(redirect: string, from?: string) {
      setAppState("session", null);
      setAppState("user", null);
      navigate(redirect, { state: from ? { from } : undefined });
    },

    async getClient(id: number, opts: VisibilityOpts): Promise<Client | null> {
      const { visibility = "public" } = opts;

      const req = apiRequest(`clients/${id}`, {
        headers: {
          Accept: visibility === "public" ? MediaTypesV1.clientPublic : MediaTypesV1.clientAll,
        },
      });
      const userRes = await this.authFetch(req);
      if (userRes.ok) return (await userRes.json()) as Client;
      else return null;
    },

    async getDriver(id: number, opts: VisibilityOpts): Promise<Driver | null> {
      const { visibility = "public" } = opts;

      const req = apiRequest(`drivers/${id}`, {
        headers: {
          Accept: visibility === "public" ? MediaTypesV1.driverPublic : MediaTypesV1.driverAll,
        },
      });
      const userRes = await this.authFetch(req);
      if (userRes.ok) return (await userRes.json()) as Driver;
      else return null;
    },

    async getUser(): Promise<void> {
      if (appState.user) return;
      const payload = appState.session?.payload.auth;
      if (!payload) return;

      if (payload.type === "client") {
        const user = await this.getClient(payload.id, { visibility: "all" });
        if (!user) return;
        const userState = clientToUserState(user);
        setAppState("user", userState);
      } else if (payload.type === "driver") {
        const user = await this.getDriver(payload.id, { visibility: "all" });
        if (!user) return;
        const userState = driverToUserState(user);
        setAppState("user", userState);
      }
    },

    async getVehicles(driverId: number, page: number = 1): Promise<Vehicle[]> {
      const req = apiRequest(`drivers/${driverId}/vehicles`, { queryParams: { page: page.toString() } });
      const res = await this.authFetch(req);
      if (res.ok) return res.json();
      else {
        console.log("getVehicles error:", res);
        return [];
        // switch (res.status) {
        //     case 400: toast('Invalid page number');
        //     return [];
        // }
      }
    },

    async getZones(): Promise<Zone[]> {
      const res = await this.authFetch(`${API_ROOT}/zones`);
      return res.json();
    },

    async updateClientInfo(id: number, data: UpdateClient): Promise<Response> {
      const url = new URL(`clients/${id}`, API_ROOT);
      const req = new Request(url, {
        method: "PATCH",
        headers: {
          "Content-Type": MediaTypesV1.clientUpdate,
        },
        body: JSON.stringify(data),
      });
      return await this.authFetch(req);
    },

    async updateDriverInfo(id: number, data: UpdateDriver): Promise<Response> {
      const url = new URL(`drivers/${id}`, API_ROOT);

      const req = new Request(url, {
        method: "PATCH",
        headers: {
          "Content-Type": MediaTypesV1.driverUpdate,
        },
        body: JSON.stringify(data),
      });

      return await this.authFetch(req);
    },

    async putClientProfilePicture(clientId: number, file: File): Promise<Response> {
      const url = new URL(`clients/${clientId}/profile-picture`, API_ROOT);
      const req = new Request(url, {
        method: "PUT",
        headers: {
          "Content-Type": file.type, // image/png o image/jpeg
        },
        body: file,
      });
      return await this.authFetch(req);
    },
    async putDriverProfilePicture(driverId: number, file: File): Promise<Response> {
      const url = new URL(`drivers/${driverId}/profile-picture`, API_ROOT);
      const req = new Request(url, {
        method: "PUT",
        headers: {
          "Content-Type": file.type, // image/png o image/jpeg
        },
        body: file,
      });
      return await this.authFetch(req);
    },
  };


}

interface ApiRequestOpts {
  headers?: HeadersInit;
  queryParams?: Record<string, string>;
}

function apiRequest(path: string, opts?: ApiRequestOpts): Request {
  if (path.startsWith("/")) throw new Error("`path` should be relative");
  const url = new URL(path, API_ROOT);
  if (opts?.queryParams) {
    for (const param in opts.queryParams) {
      url.searchParams.set(param, opts.queryParams[param]);
    }
  }
  const req = new Request(url, { headers: opts?.headers });
  return req;
}

function jwtExpired(payload: TokenPayload): boolean {
  return payload.exp < Date.now() / 1000;
}

// function isJsonResponse(response: Response): boolean {
//   const contentType = response.headers.get("Content-Type");
//   if (!contentType) return false;
//   return contentType.includes("application/json") || contentType.includes("+json");
// }
