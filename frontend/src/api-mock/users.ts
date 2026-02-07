import { TokenPayload, UserType } from "@/api/jwt";
import { Client, Driver } from "@/api/types";

export interface MockUser {
  id: number;
  username: string;
  pass: string;
  type: UserType;
  typeArrId: number;
}

export const USERS: MockUser[] = [
  {
    id: 0,
    username: "driver1",
    pass: "driver1Pass",
    type: "driver",
    typeArrId: 0,
  },
  {
    id: 1,
    username: "driver2",
    pass: "driver2Pass",
    type: "driver",
    typeArrId: 1,
  },
  {
    id: 2,
    username: "client1",
    pass: "client1Pass",
    type: "client",
    typeArrId: 0,
  },
  {
    id: 3,
    username: "client2",
    pass: "client2Pass",
    type: "client",
    typeArrId: 1,
  },
];

export const DRIVERS: Driver[] = [
  {
    id: 0,
    username: "driver1",
    email: "driver1@mail.com",
    preferredLanguage: "english",
    description: "This is driver1 description",
    rating: 4.3,
    cbu: "1234567891234567891234",
    self: "/api/drivers/0",
    profilePic: "/api/drivers/0/profile-picture",
    pendingBookings: "/api/bookings?toDriver=0&status=pending",
    acceptedBookings: "/api/bookings?toDriver=0&status=accepted",
    finishedBookings: "/api/bookings?toDriver=0&status=finished",
    rejectedBookings: "/api/bookings?toDriver=0&status=rejected",
    canceledBookings: "/api/bookings?toDriver=0&status=canceled",
    vehicles: "/api/driver/0/vehicles",
  },
  {
    id: 1,
    username: "driver2",
    email: "driver2@mail.com",
    preferredLanguage: "spanish",
    description: "This is driver2 description",
    rating: 1.3,
    cbu: "1234567891234567891234",
    self: "/api/drivers/1",
    profilePic: "/api/drivers/1/profile-picture",
    pendingBookings: "/api/bookings?toDriver=1&status=pending",
    acceptedBookings: "/api/bookings?toDriver=1&status=accepted",
    finishedBookings: "/api/bookings?toDriver=1&status=finished",
    rejectedBookings: "/api/bookings?toDriver=1&status=rejected",
    canceledBookings: "/api/bookings?toDriver=1&status=canceled",
    vehicles: "/api/driver/1/vehicles",
  },
];

export const CLIENTS: Client[] = [
  {
    id: 2,
    username: "client1",
    email: "client1@mail.com",
    preferredLanguage: "english",
    self: "/api/clients/2",
    profilePic: "/api/clients/2/profile-picture",
    zone: "/api/zones/1",
    pendingBookings: "/api/bookings?fromClient=2&status=pending",
    acceptedBookings: "/api/bookings?fromClient=2&status=accepted",
    finishedBookings: "/api/bookings?fromClient=2&status=finished",
    rejectedBookings: "/api/bookings?fromClient=2&status=rejected",
    canceledBookings: "/api/bookings?fromClient=2&status=canceled",
  },
  {
    id: 3,
    username: "client2",
    email: "client2@mail.com",
    preferredLanguage: "spanish",
    self: "/api/clients/3",
    profilePic: "/api/clients/3/profile-picture",
    zone: "/api/zones/2",
    pendingBookings: "/api/bookings?fromClient=3&status=pending",
    acceptedBookings: "/api/bookings?fromClient=3&status=accepted",
    finishedBookings: "/api/bookings?fromClient=3&status=finished",
    rejectedBookings: "/api/bookings?fromClient=3&status=rejected",
    canceledBookings: "/api/bookings?fromClient=3&status=canceled",
  },
];

export function getMockUser(payload: TokenPayload, expected: { type: UserType; id: number }): Client | Driver | null {
  if (payload.userId === expected.id) {
    const user = USERS[expected.id];
    if (user.type === expected.type) {
      switch (user.type) {
        case "client":
          return CLIENTS[user.typeArrId];
        case "driver":
          return DRIVERS[user.typeArrId];
      }
    }
  }
  return null;
}
