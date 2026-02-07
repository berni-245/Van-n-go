import { Client, Driver } from "@/api/types";

type BaseUser = {
  id: number;
  username: string;
  email: string;
  language: string;
  profilePic: string;
};

type ClientUser = BaseUser & {
  type: "client";
};

type DriverUser = BaseUser & {
  type: "driver";
  description: string;
  rating: number;
};

export type UserState = ClientUser | DriverUser;

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
    description: user.description,
    language: user.preferredLanguage,
    profilePic: user.profilePic,
    rating: user.rating,
  };
}
