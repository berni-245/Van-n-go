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
