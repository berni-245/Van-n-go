import { screen } from "@solidjs/testing-library";
import userEvent from "@testing-library/user-event";
import { http, HttpResponse } from "msw";
import { server } from "@/test/msw/server";
import { renderRoute } from "@/test/renderRoute";
import {
  seedLoggedInUser,
  driverUserResponse,
  clientResponse,
  makeDriverUser,
  makeClient,
} from "@/test/msw/authFixtures";
import { makeZone } from "@/test/msw/zoneFixtures";
import {
  apiUri,
  makeBooking,
  makeVehicle,
  bookingsResponse,
  vehicleResponse,
  zonesByIdResponse,
  BOOKINGS_ENDPOINT,
} from "@/test/msw/bookingFixtures";
import { BookingState } from "@/api/types";
import Bookings from "@/pages/Bookings";

const user = userEvent.setup();

const ZONES_BY_ID = {
  1: makeZone({ id: 1, neighborhood: { name: "Palermo" } }),
  2: makeZone({ id: 2, neighborhood: { name: "Belgrano" } }),
};

test("renders each booking for the logged-in client, showing the driver's username", async () => {
  seedLoggedInUser("client", { id: 9 });

  const booking = makeBooking({
    id: 1,
    driver: apiUri("drivers/7"),
    vehicle: apiUri("drivers/7/vehicles/3"),
  });

  server.use(
    bookingsResponse([booking]),
    driverUserResponse(makeDriverUser({ id: 7, username: "juan_driver" })),
    vehicleResponse(makeVehicle({ id: 3, self: apiUri("drivers/7/vehicles/3"), driver: apiUri("drivers/7") })),
    zonesByIdResponse(ZONES_BY_ID)
  );

  renderRoute("/bookings", "/bookings?state=PENDING&page=1", Bookings);

  expect(await screen.findByText("juan_driver")).toBeInTheDocument();
});

test("renders each booking for a logged-in driver, showing the client's username", async () => {
  seedLoggedInUser("driver", { id: 7 });

  const booking = makeBooking({
    id: 5,
    client: apiUri("clients/9"),
    driver: apiUri("drivers/7"),
    vehicle: apiUri("drivers/7/vehicles/3"),
  });

  server.use(
    bookingsResponse([booking]),
    clientResponse(makeClient({ id: 9, username: "maria_client" })),
    vehicleResponse(makeVehicle({ id: 3, self: apiUri("drivers/7/vehicles/3"), driver: apiUri("drivers/7") })),
    zonesByIdResponse(ZONES_BY_ID)
  );

  renderRoute("/bookings", "/bookings?state=PENDING&page=1", Bookings);

  expect(await screen.findByText("maria_client")).toBeInTheDocument();
});

test("shows the empty state for a tab with no bookings", async () => {
  seedLoggedInUser("client", { id: 9 });
  server.use(bookingsResponse([]));

  renderRoute("/bookings", "/bookings?state=PENDING&page=1", Bookings);

  // EMPTY_STATE_ICON is a plain emoji in the source (not translated copy),
  // so it's a stable, locale-independent thing to assert on.
  expect(await screen.findByText("📭")).toBeInTheDocument();
});

test("switching tabs requests bookings for the newly selected state", async () => {
  seedLoggedInUser("client", { id: 9 });

  let lastRequestedState: string | null = null;
  server.use(
    http.get(BOOKINGS_ENDPOINT, ({ request }) => {
      const url = new URL(request.url);
      lastRequestedState = url.searchParams.get("state");
      const bookings =
        lastRequestedState === "ACCEPTED"
          ? [
              makeBooking({
                id: 2,
                state: BookingState.Accepted,
                driver: apiUri("drivers/7"),
                vehicle: apiUri("drivers/7/vehicles/3"),
              }),
            ]
          : [];
      return HttpResponse.json(bookings, {
        headers: { Link: `<${apiUri("bookings?page=1")}>; rel="first", <${apiUri("bookings?page=1")}>; rel="last"` },
      });
    }),
    driverUserResponse(makeDriverUser({ id: 7, username: "juan_driver" })),
    vehicleResponse(makeVehicle({ id: 3, self: apiUri("drivers/7/vehicles/3"), driver: apiUri("drivers/7") })),
    zonesByIdResponse(ZONES_BY_ID)
  );

  renderRoute("/bookings", "/bookings?state=PENDING&page=1", Bookings);

  // initial load is the PENDING tab, which our handler above returns as empty
  await screen.findByText("📭");
  expect(lastRequestedState).toBe("PENDING");

  // Tab buttons don't carry an accessible role="tab" or stable translated
  // text we can rely on here, so select the 2nd one (Accepted) by position;
  // BOOKING_TABS' order is fixed in Bookings.tsx.
  const tabButtons = document.querySelectorAll('[role="tablist"] button');
  await user.click(tabButtons[1]);

  expect(await screen.findByText("juan_driver")).toBeInTheDocument();
  expect(lastRequestedState).toBe("ACCEPTED");
});
