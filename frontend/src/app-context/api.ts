import { UserType } from "@/api/jwt";
import {
  Availability,
  Booking,
  BookingState,
  Client,
  Driver,
  Message,
  RecoverPassword,
  RequestPasswordToken,
  Review,
  UpdateClient,
  UpdateDriver,
  UpdateUserPassword,
  UpdateVehicle,
  Vehicle,
  Zone,
} from "@/api/types";
import { AppState } from "@/app-context/appState";
import { jwtDecode } from "jwt-decode";
import { SetStoreFunction } from "solid-js/store";
import { clientToUserState, driverToUserState } from "./userState";
import { createMediaType as createMediaTypes } from "../api/media-types";
import { useLocation, useNavigate } from "@solidjs/router";
import { PaginatedResource, parsePageInfo } from "./pagination";
import { stripBase } from "@/utils/routing";
import { showToast } from "solid-notifications";
import * as i18n from "@solid-primitives/i18n";
import type { Dictionary } from "./locale";

export const API_ROOT = `${window.location.origin}${import.meta.env.BASE_URL}api/`;
export type Api = ReturnType<typeof initializeApi>;

export interface LocationState {
  from: string;
}

const MediaTypesV1 = createMediaTypes("v1");

//const sleep = (ms: number) => new Promise((r) => setTimeout(r, ms));

interface VisibilityOpts {
  visibility?: "public" | "all";
}
export function initializeApi(
  appState: AppState,
  setAppState: SetStoreFunction<AppState>,
  t: i18n.Translator<Dictionary>
) {
  const location = useLocation();
  const navigate = useNavigate();

  return {
    async authFetch(input: string | URL | Request): Promise<Response> {
      const base = input instanceof Request ? input : new Request(input);
      base.headers.set("Accept-Language", appState.preferences.locale || "es");

      // A successful response may carry refreshed tokens; store them whenever present.
      const captureTokens = (res: Response): Response => {
        const authToken = res.headers.get("vango-auth-token");
        const refreshToken = res.headers.get("vango-refresh-token");
        if (authToken && refreshToken) {
          setAppState("session", {
            token: { auth: authToken, refresh: refreshToken },
            payload: { auth: jwtDecode(authToken), refresh: jwtDecode(refreshToken) },
          });
        }
        return res;
      };

      const attempt = (token?: string): Promise<Response> => {
        const req = base.clone();
        if (token) req.headers.set("Authorization", `Bearer ${token}`);
        return fetch(req);
      };

      if (!appState.session) {
        return captureTokens(await attempt());
      }

      let res = await attempt(appState.session.token.auth);
      if (res.status === 401) {
        res = await attempt(appState.session.token.refresh);
        if (res.status === 401) {
          this.logout("/login", location.pathname);
          showToast(t("global.error.sessionExpired") as string, { type: "error" });
          return res;
        }
      }
      return captureTokens(res);
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
      // `from` is stored and later used as a navigation target, which must be base-relative
      // (the router re-adds the base). Strip it here so callers can pass a raw
      // location.pathname without producing a duplicated base (e.g. /base/base/login).
      navigate(redirect, { state: from ? { from: stripBase(from) } : undefined });
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

    async getClientByUri(clientURI: string, opts: VisibilityOpts = { visibility: "public" }): Promise<Client | null> {
      const id = extractNumericIdFromEndOfPath(clientURI);
      if (id === null) return null;
      return this.getClient(id, opts);
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

    async getDriverByUri(driverURI: string, opts: VisibilityOpts = { visibility: "public" }): Promise<Driver | null> {
      const id = extractNumericIdFromEndOfPath(driverURI);
      if (id === null) return null;
      return this.getDriver(id, opts);
    },

    async registerClient(
      data: { username: string; email: string; password: string; zoneId?: number },
      lang: string
    ): Promise<Response> {
      const url = new URL("clients", API_ROOT);
      return await fetch(
        new Request(url, {
          method: "POST",
          headers: {
            "Content-Type": MediaTypesV1.clientCreate,
            "Accept-Language": lang,
          },
          body: JSON.stringify(data),
        })
      );
    },

    async registerDriver(
      data: { username: string; email: string; password: string; description: string },
      lang: string
    ): Promise<Response> {
      const url = new URL("drivers", API_ROOT);
      return await fetch(
        new Request(url, {
          method: "POST",
          headers: {
            "Content-Type": MediaTypesV1.driverCreate,
            "Accept-Language": lang,
          },
          body: JSON.stringify(data),
        })
      );
    },

    async searchDrivers(params: DriverSearchParams): Promise<{ drivers: Driver[]; maxPages: number }> {
      const queryParams: Record<string, string> = {};
      if (params.originZoneId != null) queryParams.originZoneId = String(params.originZoneId);
      if (params.size) queryParams.size = params.size;
      if (params.maxPrice != null) queryParams.maxPrice = String(params.maxPrice);
      if (params.weekDay) queryParams.weekDay = params.weekDay;
      if (params.minRating != null) queryParams.minRating = String(params.minRating);
      if (params.sortOrder) queryParams.sortOrder = params.sortOrder;
      queryParams.page = String(params.page ?? 1);

      const req = apiRequest("drivers", {
        headers: { Accept: MediaTypesV1.driverList },
        queryParams,
      });

      const res = await this.authFetch(req);
      if (!res.ok) return { drivers: [], maxPages: 1 };

      const drivers = (await res.json()) as Driver[];
      const maxPages = parseTotalPages(res.headers.get("Link"), params.page ?? 1);
      return { drivers, maxPages };
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

    setUser(user: Client | Driver, type: UserType): void {
      setAppState("user", type === "client" ? clientToUserState(user as Client) : driverToUserState(user as Driver));
    },

    async getVehicles(driverId: number, page: number = 1): Promise<PaginatedResource<Vehicle>> {
      const req = apiRequest(`drivers/${driverId}/vehicles`, { queryParams: { page: page.toString() } });
      const res = await this.authFetch(req);
      const pages = parsePageInfo(res)!;
      if (res.ok) {
        const vehicles: Vehicle[] = await res.json();
        return { data: vehicles, pages };
      } else {
        console.error("getVehicles error:", res);
        // switch (res.status) {
        //   case 400:
        //     toaster.notify("Invalid page number", { type: "error" });
        //     break;
        // }
        return { data: [], pages };
      }
    },

    async getZones(): Promise<Zone[]> {
      const res = await this.authFetch(`${API_ROOT}zones`);
      return res.json();
    },

    async getZonesRecord(): Promise<Record<number, Zone>> {
      const req = apiRequest("zones", { headers: { Accept: MediaTypesV1.zones } });
      const res = await this.authFetch(req);
      const zones = (await res.json()) as Zone[];
      const zoneEntries = zones.map((z) => [z.id, z] as [number, Zone]);
      return Object.fromEntries(zoneEntries) as Record<number, Zone>;
    },

    async getZoneByUri(uri: string): Promise<Zone> {
      // Como el HATEOAS ya te da la ruta correcta, se la pasamos directo al authFetch
      const res = await this.authFetch(uri);

      if (!res.ok) {
        throw new Error("No se pudo obtener la zona");
      }
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

    async getVehicle(driverId: number, vehicleId: number): Promise<Vehicle | null> {
      const req = apiRequest(`drivers/${driverId}/vehicles/${vehicleId}`, {
        headers: { Accept: MediaTypesV1.vehicle },
      });
      const res = await this.authFetch(req);
      if (res.ok) return res.json();
      else {
        console.error(`getVehicle ${driverId}@${vehicleId} error:`, res);
        return null;
      }
    },

    async getVehicleAvailability(url: string): Promise<Availability | null> {
      const req = apiRequest(url, { headers: { Accept: MediaTypesV1.availability } });
      const res = await this.authFetch(req);

      const text = await res.text();

      if (!res.ok) return null;

      return JSON.parse(text);
    },

    async getVehicleByUri(vehicleURI: string): Promise<Vehicle | null> {
      const segments = new URL(vehicleURI).pathname.split("/");

      const vehicleId = Number(segments[segments.length - 1]);
      const driverId = Number(segments[segments.length - 3]);

      if (Number.isNaN(driverId) || Number.isNaN(vehicleId)) {
        throw new Error("URI does not contain valid driverId and vehicleId");
      }

      return this.getVehicle(driverId, vehicleId);
    },

    async createVehicle(
      driverId: number,
      data: { plateNumber: string; volumeM3: number; hourlyRate: number; description: string }
    ): Promise<Response> {
      const url = new URL(`drivers/${driverId}/vehicles`, API_ROOT);
      const req = new Request(url, {
        method: "POST",
        headers: {
          "Content-Type": MediaTypesV1.vehicleCreate,
        },
        body: JSON.stringify(data),
      });
      return await this.authFetch(req);
    },

    async getDriverVehicles(driverId: number): Promise<any[]> {
      const res = await this.authFetch(`${API_ROOT}drivers/${driverId}/vehicles`);
      if (!res.ok) return [];
      return await res.json();
    },

    async getBookedTimes(
      driverId: number,
      vehicleId: number,
      month: string,
      year: number
    ): Promise<{ date: string; shift: string }[]> {
      const req = apiRequest(`drivers/${driverId}/vehicles/${vehicleId}/booked-times`, {
        queryParams: { month, year: String(year) },
      });
      const res = await this.authFetch(req);
      if (!res.ok) return [];
      return await res.json();
    },

    async createBooking(payload: any): Promise<Response> {
      const url = new URL(`bookings`, API_ROOT);
      const req = new Request(url, {
        method: "POST",
        headers: {
          "Content-Type": MediaTypesV1.bookingCreate,
        },
        body: JSON.stringify(payload),
      });
      return await this.authFetch(req);
    },

    async updateVehicle(driverId: number, vehicleId: number, data: UpdateVehicle): Promise<Response> {
      const url = new URL(`drivers/${driverId}/vehicles/${vehicleId}`, API_ROOT);
      const req = new Request(url, {
        method: "PATCH",
        headers: {
          "Content-Type": MediaTypesV1.vehicleUpdate,
        },
        body: JSON.stringify(data),
      });

      return await this.authFetch(req);
    },

    async putVehicleImage(url: string, file: File): Promise<Response> {
      const req = new Request(url, {
        method: "PUT",
        headers: {
          "Content-Type": file.type,
        },
        body: file,
      });
      return await this.authFetch(req);
    },

    async updateVehicleAvailability(
      driverId: number,
      vehicleId: number,
      payload: { zones: number[]; timeSlots: { day: string; shift: string }[] }
    ): Promise<Response> {
      const url = new URL(`drivers/${driverId}/vehicles/${vehicleId}/availability`, API_ROOT);
      const req = new Request(url, {
        method: "PATCH",
        headers: {
          "Content-Type": MediaTypesV1.availabilityUpdate,
        },
        body: JSON.stringify(payload),
      });

      return await this.authFetch(req);
    },

    async getMessages(
      driverId: number,
      clientId: number,
      page: number = 1
    ): Promise<{ messages: Message[]; maxPages: number } | null> {
      const req = apiRequest("messages", {
        headers: { Accept: MediaTypesV1.messageList },
        queryParams: { driverId: String(driverId), clientId: String(clientId), page: String(page) },
      });
      const res = await this.authFetch(req);
      if (!res.ok) return null;
      const messages = (await res.json()) as Message[];
      const maxPages = parseTotalPages(res.headers.get("Link"), page);
      return { messages, maxPages };
    },

    async sendMessage(driverId: number, clientId: number, message: string): Promise<Message | null> {
      const url = new URL("messages", API_ROOT);
      const req = new Request(url, {
        method: "POST",
        headers: {
          "Content-Type": MediaTypesV1.messageCreate,
          Accept: MediaTypesV1.message,
        },
        body: JSON.stringify({ driverId, clientId, message }),
      });
      const res = await this.authFetch(req);
      if (!res.ok) return null;
      return res.json() as Promise<Message>;
    },

    // async getBookings(userId: number, state: BookingState, page: number = 1): Promise<Booking[]> {
    //   const req = apiRequest('bookings',
    //       {queryParams: {userId: userId.toString(), state: state.toUpperCase(), page: page.toString()}});
    //   const res = await this.authFetch(req)
    //   if (res.ok) return res.json();
    //   else return [];
    // },
    async getBookings(userId: number, state: BookingState, page: number = 1): Promise<PaginatedResource<Booking>> {
      const req = apiRequest("bookings", {
        queryParams: { userId: userId.toString(), state: state.toUpperCase(), page: page.toString() },
      });
      const res = await this.authFetch(req);
      if (res.ok) {
        const pages = parsePageInfo(res)!;
        const data = (await res.json()) as Booking[];
        return { data, pages };
      } else return { data: [], pages: null };
    },

    async getBookingsInYearMonth(userId: number, state: BookingState, year: number, month: string): Promise<Booking[]> {
      const req = apiRequest("bookings", {
        queryParams: {
          userId: userId.toString(),
          state: state.toUpperCase(),
          year: year.toString(),
          month: month.toUpperCase(),
        },
      });
      const res = await this.authFetch(req);
      if (res.ok) return (await res.json()) as Booking[];
      else return [];
    },

    async updateBookingState(bookingId: number, state: BookingState): Promise<Response> {
      const payload = {
        state: state.toUpperCase(),
      };
      const url = new URL(`bookings/${bookingId}`, API_ROOT);
      const req = new Request(url, {
        method: "PATCH",
        headers: {
          "Content-Type": MediaTypesV1.bookingUpdateState,
        },
        body: JSON.stringify(payload),
      });

      return await this.authFetch(req);
    },

    async getReview(bookingId: number): Promise<Review | null> {
      const req = apiRequest(`bookings/${bookingId}/review`);
      const res = await this.authFetch(req);
      if (res.ok) return res.json();
      else return null;
    },

    async hasReview(bookingId: number): Promise<boolean> {
      return (await this.getReview(bookingId)) !== null;
    },

    async hasProofOfPayment(bookingId: number): Promise<boolean> {
      const req = apiRequest(`bookings/${bookingId}/proof-of-payment`);
      const res = await this.authFetch(req);
      return res.ok;
    },

    async postReview(bookingId: number, rating: number, comments: string): Promise<Response> {
      const payload = {
        rating: rating,
        comments: comments,
        booking: `api/bookings/${bookingId}`,
      };
      const url = new URL(`bookings/${bookingId}/review`, API_ROOT);
      const req = new Request(url, {
        method: "POST",
        headers: {
          "Content-Type": MediaTypesV1.bookingReview,
        },
        body: JSON.stringify(payload),
      });

      return await this.authFetch(req);
    },

    async putProofOfPayment(bookingId: number, file: File): Promise<Response> {
      const url = new URL(`bookings/${bookingId}/proof-of-payment`, API_ROOT);
      const req = new Request(url, {
        method: "PUT",
        headers: {
          "Content-Type": file.type,
        },
        body: file,
      });
      return await this.authFetch(req);
    },

    async requestPasswordToken(data: RequestPasswordToken): Promise<Response> {
      const url = new URL("users", API_ROOT);
      const req = new Request(url, {
        method: "POST",
        headers: {
          "Content-Type": MediaTypesV1.recovery,
        },
        body: JSON.stringify(data),
      });
      return await this.authFetch(req);
    },

    async updateUserPasswordWithToken(id: string | number, data: RecoverPassword): Promise<Response> {
      const url = new URL(`users/${id}`, API_ROOT);
      const req = new Request(url, {
        method: "PATCH",
        headers: {
          "Content-Type": MediaTypesV1.passwordToken,
        },
        body: JSON.stringify(data),
      });
      return await this.authFetch(req);
    },

    async updateUserPassword(id: number, data: UpdateUserPassword): Promise<Response> {
      const url = new URL(`users/${id}`, API_ROOT);
      const req = new Request(url, {
        method: "PATCH",
        headers: {
          "Content-Type": MediaTypesV1.passwordChange,
        },
        body: JSON.stringify(data),
      });
      return await this.authFetch(req);
    },
  };
}

interface ApiRequestOpts extends RequestInit {
  queryParams?: Record<string, string>;
}

export function apiRequest(path: string, opts?: ApiRequestOpts): Request {
  // `path` may be a relative API path (e.g. "drivers/1") or a fully-qualified HATEOAS
  // URI returned by the API (e.g. "http://host/paw-2024b-01/api/..."). `new URL(path,
  // API_ROOT)` handles both: an absolute URI ignores the base, a relative one resolves
  // against API_ROOT. A leading "/" is rejected because it would resolve against the
  // origin root and silently drop the "/paw-2024b-01/api/" base path.
  if (path.startsWith("/")) throw new Error("`path` must be a relative API path or an absolute URI");

  const url = new URL(path, API_ROOT);

  if (opts?.queryParams) {
    for (const [key, value] of Object.entries(opts.queryParams)) {
      url.searchParams.set(key, value);
    }
  }

  return new Request(url, opts);
}

export function extractNumericIdFromEndOfPath(uri: string): number | null {
  const idStr = uri.replace(/\/+$/, "").split("/").pop();
  if (!idStr) return null;

  const id = Number(idStr);
  return Number.isNaN(id) ? null : id;
}

export interface DriverSearchParams {
  originZoneId?: number | null;
  size?: string;
  maxPrice?: number | null;
  weekDay?: string;
  minRating?: number | null;
  sortOrder?: string;
  page?: number;
}

export function parseTotalPages(linkHeader: string | null, currentPage: number): number {
  if (!linkHeader) return currentPage;
  const match = linkHeader.match(/[?&]page=(\d+)[^>]*>;\s*rel="last"/);
  return match ? parseInt(match[1], 10) : currentPage;
}

// function isJsonResponse(response: Response): boolean {
//   const contentType = response.headers.get("Content-Type");
//   if (!contentType) return false;
//   return contentType.includes("application/json") || contentType.includes("+json");
// }
