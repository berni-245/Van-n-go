export interface ApiError {
  type: string;
  description: string;
  entity?: string;
  subtype?: string;
}

type Language = "english" | "spanish";

interface BaseUser {
  id: number;
  username: string;
  email: string;
  preferredLanguage: Language;
  profilePic: string;
  hasProfilePic: boolean;
  self: string;
  pendingBookings: string;
  acceptedBookings: string;
  finishedBookings: string;
  rejectedBookings: string;
  canceledBookings: string;
}

export interface Client extends BaseUser {
  zone: string;
}

export interface Driver extends BaseUser {
  description?: string;
  rating?: number;
  cbu?: string;
  vehicles: string;
}

export interface Vehicle {
  id: number;
  plateNumber: string;
  volumeM3: number;
  hourlyRate: number;
  description: string;
  self: string;
  picture: string;
  hasPicture: boolean;
  driver: string;
  availability: string;
  bookedTimes: string;
}

export enum Size {
  Small = "small",
  Medium = "medium",
  Large = "large",
}

export enum WeekDay {
  Monday = "small",
  Tuesday = "medium",
  Wednesday = "large",
  Thursday = "thursday",
  Friday = "friday",
  Saturday = "saturday",
  Sunday = "sunday",
}

export interface Country {
  name: string;
  code: string;
}
export interface Province {
  name: string;
}
export interface Neighborhood {
  name: string;
}
export interface Zone {
  id: number;
  country: Country;
  province: Province;
  neighborhood: Neighborhood;
  self: string;
}

interface UpdateUser {
  username?: string;
  oldPassword?: string;
  password?: string;
  email?: string;
  preferredLanguage?: string;
}

export interface UpdateClient extends UpdateUser {
  zoneId?: number;
}

export interface UpdateDriver extends UpdateUser {
  description?: string;
}
