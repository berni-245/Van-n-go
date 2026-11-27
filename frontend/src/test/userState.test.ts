import { expect, test } from "vitest";
import { clientToUserState, driverToUserState } from "@/app-context/userState";
import type { Client, Driver } from "@/api/types";

// Only id/username/email/preferredLanguage/profilePic actually make it into
// UserState -- the rest of Client/Driver (self, the *Bookings links, zone,
// description, rating, cbu, vehicles) is filled in with placeholder values
// here since the mappers are expected to ignore it entirely.
function makeClient(overrides: Partial<Client> = {}): Client {
  return {
    id: 1,
    username: "juan_perez",
    email: "juan@example.com",
    preferredLanguage: "SPANISH",
    profilePic: "https://example.com/pic.jpg",
    self: "/api/clients/1",
    pendingBookings: "/api/clients/1/bookings/pending",
    acceptedBookings: "/api/clients/1/bookings/accepted",
    finishedBookings: "/api/clients/1/bookings/finished",
    rejectedBookings: "/api/clients/1/bookings/rejected",
    canceledBookings: "/api/clients/1/bookings/canceled",
    zone: "/api/zones/5",
    ...overrides,
  };
}

function makeDriver(overrides: Partial<Driver> = {}): Driver {
  return {
    id: 2,
    username: "maria_gomez",
    email: "maria@example.com",
    preferredLanguage: "ENGLISH",
    profilePic: "https://example.com/pic2.jpg",
    self: "/api/drivers/2",
    pendingBookings: "/api/drivers/2/bookings/pending",
    acceptedBookings: "/api/drivers/2/bookings/accepted",
    finishedBookings: "/api/drivers/2/bookings/finished",
    rejectedBookings: "/api/drivers/2/bookings/rejected",
    canceledBookings: "/api/drivers/2/bookings/canceled",
    vehicles: "/api/drivers/2/vehicles",
    description: "Experienced driver",
    rating: 4.5,
    cbu: "0000000000000000000000",
    ...overrides,
  };
}

test("clientToUserState maps the relevant fields and renames preferredLanguage to language", () => {
  expect(clientToUserState(makeClient())).toEqual({
    id: 1,
    type: "client",
    username: "juan_perez",
    email: "juan@example.com",
    language: "SPANISH",
    profilePic: "https://example.com/pic.jpg",
  });
});

test("clientToUserState drops the zone and all the HATEOAS links", () => {
  const state = clientToUserState(makeClient());
  expect(state).not.toHaveProperty("zone");
  expect(state).not.toHaveProperty("self");
});

test("driverToUserState maps the relevant fields and renames preferredLanguage to language", () => {
  expect(driverToUserState(makeDriver())).toEqual({
    id: 2,
    type: "driver",
    username: "maria_gomez",
    email: "maria@example.com",
    language: "ENGLISH",
    profilePic: "https://example.com/pic2.jpg",
  });
});

test("driverToUserState drops description/rating/cbu/vehicles (commented out of UserState on purpose)", () => {
  const state = driverToUserState(makeDriver());
  expect(state).not.toHaveProperty("description");
  expect(state).not.toHaveProperty("rating");
  expect(state).not.toHaveProperty("cbu");
  expect(state).not.toHaveProperty("vehicles");
});

test("each mapper stamps its own literal 'type', regardless of input", () => {
  expect(clientToUserState(makeClient()).type).toBe("client");
  expect(driverToUserState(makeDriver()).type).toBe("driver");
});
