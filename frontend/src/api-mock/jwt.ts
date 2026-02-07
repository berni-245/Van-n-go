import { jwtDecode } from "jwt-decode";
import { MockUser } from "./users";
import { TokenPayload } from "@/api/jwt";

const pass = btoa("super-secret"); // not really used
const hashPart = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

function tokenToJwt(token: TokenPayload): string {
  return [hashPart, btoa(JSON.stringify(token)), pass].join(".");
}

export function generateTokens(user: MockUser): { authToken: string; refreshToken: string } {
  const now = Date.now();
  const authToken: TokenPayload = {
    exp: now + 10 * 1000, // 10s ttl
    userId: user.id,
    userType: user.type,
  };
  const refreshToken: TokenPayload = {
    exp: now + 60 * 2 * 1000, // 2min ttl
    userId: user.id,
    userType: user.type,
  };

  return {
    authToken: tokenToJwt(authToken),
    refreshToken: tokenToJwt(refreshToken),
  };
}

export function parseTokenPayload(token: string): TokenPayload | null {
  const decoded = jwtDecode<TokenPayload>(token);
  if (typeof decoded.userId === "number" && validUserType(decoded.userType) && typeof decoded.exp === "number") {
    return decoded;
  }
  return null;
}

function validUserType(type: string): boolean {
  return type === "client" || type === "driver";
}
