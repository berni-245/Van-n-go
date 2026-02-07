import { SetStoreFunction } from "solid-js/store";
import { AppState } from "./appState";
import { Client, Driver } from "@/api/types";
import { UserType } from "../api/jwt";
import { clientToUserState, driverToUserState, UserState } from "./userState";
import { jwtDecode } from "jwt-decode";

export const BASE_URL = "/api";

export function initializeApi(appState: AppState, setAppState: SetStoreFunction<AppState>) {
  return {
    async authFetch(input: string | URL | Request): Promise<Response> {
      const request = input instanceof Request ? input : new Request(input);

      if (appState.session) {
        request.headers.set("Authorization", `Bearer ${appState.session.authToken}`);
        request.headers.set("vango-refresh-token", `Bearer ${appState.session.refreshToken}`);
      }

      request.headers.set("Content-Type", "Application/json");
      request.headers.set("Accept", "Application/json");

      const res = await fetch(request);

      if (res.status === 401) {
        console.log("Expired tokens (could be an unsuccessful login attempt too). User logged out");
        setAppState("session", null);
        setAppState("user", null);
      } else {
        const authToken = res.headers.get("vango-auth-token");
        const refreshToken = res.headers.get("vango-refresh-token");

        if (authToken && refreshToken)
          setAppState("session", { authToken, refreshToken, payload: jwtDecode(authToken)! });
      }

      return res;
    },

    async getClient(id: number): Promise<Client | null> {
      const userRes = await this.authFetch(`${BASE_URL}/clients/${id}`);
      if (userRes.ok) return (await userRes.json()) as Client;
      else return null;
    },

    async getDriver(id: number): Promise<Driver | null> {
      const userRes = await this.authFetch(`${BASE_URL}/drivers/${id}`);
      if (userRes.ok) return (await userRes.json()) as Driver;
      else return null;
    },

    async getUser(type: UserType, id: number): Promise<Client | Driver | null> {
      if (type === "client") {
        const user = await this.getClient(id);
        if (!user) return null;
        const userState: UserState = clientToUserState(user);
        setAppState("user", userState);
        return user;
      } else if (type === "driver") {
        const user = await this.getDriver(id);
        if (!user) return null;
        const userState: UserState = driverToUserState(user);
        setAppState("user", userState);
        return user;
      }
      return null;
    },
  };
}

export type Api = ReturnType<typeof initializeApi>;
