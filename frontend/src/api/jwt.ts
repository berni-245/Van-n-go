import * as z from "zod";

export type UserType = z.infer<typeof UserTypeSchema>;
export type TokenType = z.infer<typeof TokenTypeSchema>;
export type TokenPayload = z.infer<typeof TokenPayloadSchema>;

const UserTypeSchema = z.enum(["client", "driver"]);
const TokenTypeSchema = z.enum(["auth", "refresh"]);

export const TokenPayloadSchema = z.object({
  type: UserTypeSchema,
  id: z.number(),
  tokenType: TokenTypeSchema,
  iat: z.number(),
  exp: z.number(),
});
