import { HttpResponse, JsonBodyType } from "msw";
import { generateTokens, parseTokenPayload } from "./jwt";
import { USERS } from "./users";
import { TokenPayload } from "../api/jwt";

type Auth = { type: "basic" | "bearer"; credentials: string };
type AuthHeader = { type: "none" } | Auth;

interface PublicResponse {
  body: JsonBodyType;
  status: number;
}
type AuthResponse = (payload: TokenPayload) => { body: JsonBodyType; status: number };

interface CraftPublicResponse {
  request: Request;
  response: PublicResponse;
}

interface CraftAuthorizedResponse {
  request: Request;
  response: AuthResponse;
}

function tokenResponse(request: Request): { headers: HeadersInit | null; payload: TokenPayload | null } {
  const authTokenResponse = request.headers.get("vango-auth-token-response");
  const refreshTokenResponse = request.headers.get("vango-refresh-token-response");

  if (authTokenResponse && refreshTokenResponse) {
    return {
      headers: {
        "vango-auth-token": authTokenResponse,
        "vango-refresh-token": refreshTokenResponse,
      },
      payload: parseTokenPayload(authTokenResponse)!,
    };
  }

  return { headers: null, payload: null };
}

export function publicResponse({ request, response }: CraftPublicResponse): HttpResponse<JsonBodyType> {
  let { headers } = tokenResponse(request);

  return HttpResponse.json(response.body, {
    status: response.status,
    headers: headers ?? undefined,
  });
}

export function authorizedResponse({ request, response }: CraftAuthorizedResponse): HttpResponse<JsonBodyType> {
  const authorized = request.headers.get("authorized");
  if (authorized !== "true") {
    return HttpResponse.json(
      {
        type: "unauthorized",
        description: "Invalid authorization",
      },
      { status: 401 }
    );
  }

  let { headers, payload } = tokenResponse(request);
  if (!payload) {
    const auth = parseAuthorizationHeader(request);
    if (auth.type !== "bearer") throw new Error("Request was authorized but has no bearer token");
    payload = parseTokenPayload(auth.credentials)!;
  }

  const { body, status } = response(payload);

  return HttpResponse.json(body, {
    status,
    headers: headers ?? undefined,
  });
}

function parseAuthorizationHeader(request: Request): AuthHeader {
  const authHeader = request.headers.get("Authorization");
  if (!authHeader) return { type: "none" };

  const [method, credentials] = authHeader.split(" ");

  switch (method.toLowerCase()) {
    case "basic":
      return { type: "basic", credentials };
    case "bearer":
      return { type: "bearer", credentials };
    default:
      return { type: "none" };
  }
}

export function handleAuthorization(request: Request) {
  const auth = parseAuthorizationHeader(request);

  switch (auth.type) {
    case "basic":
      handleBasicAuth(request, auth);
      break;
    case "bearer":
      handleTokenAuth(request, auth);
      break;
  }
}

function handleBasicAuth(request: Request, auth: Auth) {
  try {
    const [username, password] = atob(auth.credentials).split(":");
    console.log(username, password);
    const user = USERS.find((user) => user.username === username);
    if (!user) console.log("Invalid username");
    else if (user.pass !== password) console.log("Invalid password");
    else {
      const { authToken, refreshToken } = generateTokens(user);
      request.headers.set("vango-auth-token-response", authToken);
      request.headers.set("vango-refresh-token-response", refreshToken);
      request.headers.set("authorized", "true");
      console.log("User logged in");
    }
  } catch (e) {
    console.error(e);
  }
}

function handleTokenAuth(request: Request, auth: Auth) {
  const authToken = parseTokenPayload(auth.credentials);
  if (!authToken) return;
  if (authToken.exp < Date.now()) {
    const refreshJwt = request.headers.get("vango-refresh-token");
    if (refreshJwt) {
      const refreshPayload = parseTokenPayload(refreshJwt);
      if (!refreshPayload || refreshPayload.exp < Date.now()) return;
      const { authToken, refreshToken } = generateTokens(USERS[refreshPayload.userId]);
      request.headers.set("vango-auth-token-response", authToken);
      request.headers.set("vango-refresh-token-response", refreshToken);
      console.log("Tokens refreshed");
    }
  }
  console.log("User authorized through token");
  request.headers.set("authorized", "true");
}
