export type UserType = "client" | "driver";

export interface TokenPayload {
  userId: number;
  userType: UserType;
  exp: number;
}
