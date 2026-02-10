import { Client, Driver } from "@/api/types";
import * as z from "zod";

// type BaseUser = z.infer<typeof BaseUserSchema>;
type UserState = z.infer<typeof UserStateSchema>;

const BaseUserSchema = z.object({
  id: z.number(),
  username: z.string(),
  email: z.email(),
  language: z.string(),
  profilePic: z.string(),
});

const ClientUserSchema = BaseUserSchema.extend({
  type: z.literal("client"),
});

const DriverUserSchema = BaseUserSchema.extend({
  type: z.literal("driver"),
  // NOTE: Don't really need these for the app state, when accessing the profile it's probably
  // better to just request the user info again so it's not stale.
  // cbu: z.string().optional(),
  // description: z.string().optional(),
  // rating: z.number().optional(),
});

export const UserStateSchema = z.discriminatedUnion("type", [ClientUserSchema, DriverUserSchema]);

export function clientToUserState(user: Client): UserState {
  return {
    id: user.id,
    type: "client",
    username: user.username,
    email: user.email,
    language: user.preferredLanguage,
    profilePic: user.profilePic,
  };
}

export function driverToUserState(user: Driver): UserState {
  return {
    id: user.id,
    type: "driver",
    username: user.username,
    email: user.email,
    language: user.preferredLanguage,
    profilePic: user.profilePic,
    // description: user.description,
    // cbu: user.cbu,
    // rating: user.rating,
  };
}
