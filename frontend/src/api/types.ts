export interface ApiError {
  type: string;
  description: string;
  entity?: string;
  subtype?: string;
}

type Language = "ENGLISH" | "SPANISH";

export type UserType = "driver" | "client";

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

export interface Booking {
  self: string;
  client: string;
  driver: string;
  vehicle: string;
  originZone: string;
  destinationZone: string;
  proofOfPayment: string;
  hasProofOfPayment: boolean;
  state: BookingState;
  date: string;
  jobDescription: string;
  shiftPeriod: ShiftPeriod;
  review: string;
}

export enum BookingState {
  Pending = "PENDING",
  Accepted = "ACCEPTED",
  Rejected = "REJECTED",
  Finished = "FINISHED",
  Canceled = "CANCELED",
}

export enum ShiftPeriod {
  Morning = "MORNING",
  Afternoon = "AFTERNOON"
}

export type WeekDay = "MONDAY" | "TUESDAY" | "WEDNESDAY" | "THURSDAY" | "FRIDAY" | "SATURDAY" | "SUNDAY";
export type Shift = "MORNING" | "AFTERNOON" | "NIGHT";

export interface TimeSlots {
  day: WeekDay;
  shift: Shift;
}

export interface Availability {
  zones: string[];
  timeSlots: TimeSlots[];
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

export type Message = {
  id: number;
  message: string;
  sentByDriver: boolean;
  sentAt: string; // ISO datetime
};

export interface UpdateVehicle {
  plateNumber?: string;
  volumeM3?: number;
  hourlyRate?: number;
  description?: string;
}