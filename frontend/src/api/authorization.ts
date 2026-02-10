// interface BasicAuthArgs {
//   request: Request;
//   username: string;
//   password: string;
// }
//
// export function addBasicAuth({ request, username, password }: BasicAuthArgs): void {
//   const credentials = btoa(`${username}:${password}`);
//   request.headers.set("Authorization", `Basic ${credentials}`);
// }

// export async function fetchWithToken(
//   input: string | URL | Request,
//   opts: {
//     session: AuthSession | null;
//     onSessionUpdate: (session: AuthSession | null) => void;
//   }
// ): Promise<Response> {
//   const request = input instanceof Request ? input : new Request(input);
//
//   if (opts.session) {
//     request.headers.set("Authorization", `Bearer ${opts.session.authToken}`);
//     request.headers.set("vango-refresh-token", `Bearer ${opts.session.refreshToken}`);
//   }
//
//   request.headers.set("Content-Type", "Application/json");
//   request.headers.set("Accept", "Application/json");
//
//   const res = await fetch(request);
//
//   if (res.status === 401) {
//     console.log("Expired tokens. User logged out");
//     // setAppState("session", null);
//     opts.onSessionUpdate(null);
//   } else {
//     const authToken = res.headers.get("vango-auth-token");
//     const refreshToken = res.headers.get("vango-refresh-token");
//
//     // if (authToken && refreshToken) setAppState("session", { authToken, refreshToken });
//     if (authToken && refreshToken) {
//       opts.onSessionUpdate({ authToken, refreshToken });
//     }
//   }
//
//   return res;
// }
