import {screen, waitFor} from "@solidjs/testing-library";
import userEvent from "@testing-library/user-event";
import {http, HttpResponse} from "msw";
import {server} from "@/test/msw/server";
import {renderRoute} from "@/test/renderRoute";
import {seedLoggedInUser} from "@/test/msw/authFixtures";
import {makeZone, zonesResponse} from "@/test/msw/zoneFixtures";
import {
  AVAILABILITY_ENDPOINT,
  availabilityResponse,
  bookedTimesResponse,
  createBookingConflictResponse,
  createBookingFieldErrorsResponse,
  createBookingSuccessResponse,
  driverVehiclesResponse,
  makeAvailability,
  makeBookableVehicle,
  makeUserBooking,
  userBookingsResponse,
} from "@/test/msw/bookingCreateFixtures";
import {apiUri} from "@/test/msw/bookingFixtures";
import BookingCreate from "@/pages/BookingCreate";

const user = userEvent.setup();

// The seeded vehicle serves zones 1 and 2 for pickup (see makeAvailability),
// but NOT zone 3. Recoleta therefore exercises the rule that a destination can
// be any zone even when the vehicle doesn't offer it for pickup.
const ZONES = [
  makeZone({ id: 1, neighborhood: { name: "Palermo" } }),
  makeZone({ id: 2, neighborhood: { name: "Belgrano" } }),
  makeZone({ id: 3, neighborhood: { name: "Recoleta" } }),
];
// Built directly in BookingCreate.tsx's zone options (not translated),
// so it's safe to assert/select on regardless of the app's locale.
const PALERMO_OPTION = "Palermo, Buenos Aires";
const RECOLETA_OPTION = "Recoleta, Buenos Aires";

function renderBookingCreate(extraRoutes: { path: string; component: () => unknown }[] = []) {
  return renderRoute("/bookings/:driverId/new", "/bookings/7/new", BookingCreate, extraRoutes as never);
}

/**
 * Drives the page up through picking tomorrow's date and an available shift —
 * the common setup for every test below. Bookings are only allowed from
 * tomorrow onward (same-day is rejected by the backend), so today renders
 * disabled. Availability is seeded for every day of the week (see
 * makeAvailability), so tomorrow is always bookable regardless of when the
 * suite runs. When today is the last day of the month, tomorrow falls into
 * the next month and the calendar auto-advances; re-querying the button by
 * its day number inside waitFor handles that DOM swap transparently.
 */
async function selectTomorrow() {
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  const tomorrowDay = String(tomorrow.getDate());

  const dayButton = await waitFor(() => {
    const btn = screen.getByRole("button", { name: tomorrowDay });
    expect(btn).not.toBeDisabled();
    return btn;
  });
  await user.click(dayButton);
}

async function selectTomorrowAndShift() {
  await selectTomorrow();

  const shiftButton = await waitFor(() => {
    const btn = document.querySelector(".flex.flex-wrap.gap-2 button") as HTMLButtonElement | null;
    if (!btn) throw new Error("no shift button rendered yet");
    return btn;
  });
  await user.click(shiftButton);
}

async function pickZone(comboboxIndex: number, optionLabel: string) {
  const combobox = screen.getAllByRole("combobox")[comboboxIndex];
  await user.click(combobox);
  await user.click(await screen.findByText(optionLabel));
}

async function fillAndSubmit(jobDescription: string) {
  await pickZone(0, PALERMO_OPTION);
  // Recoleta (zone 3) is not a pickup zone for the vehicle, so it only appears
  // as a destination option — picking it here exercises the any-destination rule.
  await pickZone(1, RECOLETA_OPTION);
  await user.type(document.querySelector('textarea[name="jobDescription"]') as HTMLTextAreaElement, jobDescription);
  await user.click(document.querySelector('button[type="submit"]') as HTMLButtonElement);
}

test("lets a client pick an available day/shift and submits a booking successfully", async () => {
  seedLoggedInUser("client", { id: 9 });

  const vehicle = makeBookableVehicle({ id: 5, driverId: 7 });
  server.use(
    driverVehiclesResponse([vehicle]),
    availabilityResponse(makeAvailability()),
    bookedTimesResponse([]),
    zonesResponse(ZONES),
    userBookingsResponse(),
    createBookingSuccessResponse({ id: 42 })
  );

  renderBookingCreate([{ path: "/client/bookings", component: () => <p>bookings-list-stub</p> }]);

  await selectTomorrowAndShift();
  await fillAndSubmit("Moving some boxes across town.");

  expect(await screen.findByText("bookings-list-stub")).toBeInTheDocument();
});

test("shows a field-level error returned by the API next to the relevant field", async () => {
  seedLoggedInUser("client", { id: 9 });

  const vehicle = makeBookableVehicle({ id: 5, driverId: 7 });
  server.use(
    driverVehiclesResponse([vehicle]),
    availabilityResponse(makeAvailability()),
    bookedTimesResponse([]),
    zonesResponse(ZONES),
    userBookingsResponse(),
    createBookingFieldErrorsResponse([
      {
        type: "ValidationError",
        entity: "destinationZoneId",
        description: "The destination zone must differ from the origin.",
      },
    ])
  );

  renderBookingCreate();

  await selectTomorrowAndShift();
  await fillAndSubmit("Moving some boxes across town.");

  expect(await screen.findByText(/The destination zone must differ from the origin\./)).toBeInTheDocument();
});

test("does not navigate away when the API reports a scheduling conflict (409)", async () => {
  seedLoggedInUser("client", { id: 9 });

  const vehicle = makeBookableVehicle({ id: 5, driverId: 7 });
  server.use(
    driverVehiclesResponse([vehicle]),
    availabilityResponse(makeAvailability()),
    bookedTimesResponse([]),
    zonesResponse(ZONES),
    userBookingsResponse(),
    createBookingConflictResponse()
  );

  renderBookingCreate([{ path: "/client/bookings", component: () => <p>bookings-list-stub</p> }]);

  await selectTomorrowAndShift();
  await fillAndSubmit("Moving some boxes across town.");

  // stays on the booking form; conflict is surfaced via toast, not a field error
  await waitFor(() => expect(document.querySelector('button[type="submit"]')).not.toBeDisabled());
  expect(screen.queryByText("bookings-list-stub")).not.toBeInTheDocument();
});

test("only offers vehicles that work the zone the client searched in", async () => {
  seedLoggedInUser("client", { id: 9 });

  // Two vehicles for the same driver: one works zone 1 (the searched zone),
  // the other only works zone 2. The availability handler answers per-vehicle
  // by path param so their zones actually differ.
  const inZoneVehicle = makeBookableVehicle({ id: 5, driverId: 7, plateNumber: "INZONE01" });
  const outZoneVehicle = makeBookableVehicle({ id: 6, driverId: 7, plateNumber: "OUTZONE9" });

  server.use(
    driverVehiclesResponse([inZoneVehicle, outZoneVehicle]),
    http.get(AVAILABILITY_ENDPOINT, ({ params }) => {
      const zones = params.vehicleId === "5" ? [apiUri("zones/1")] : [apiUri("zones/2")];
      return HttpResponse.json(makeAvailability({ zones }));
    }),
    bookedTimesResponse([]),
    zonesResponse(ZONES),
    userBookingsResponse()
  );

  // originZoneId=1 mirrors arriving from a search scoped to zone 1.
  renderRoute("/bookings/:driverId/new", "/bookings/7/new?originZoneId=1", BookingCreate);

  await selectTomorrow();

  // The zone-1 vehicle is bookable; the zone-2 one is filtered out entirely.
  expect(await screen.findByText("INZONE01")).toBeInTheDocument();
  expect(screen.queryByText("OUTZONE9")).not.toBeInTheDocument();
});

test("hides only the exact vehicle+shift the client already booked, leaving other vehicles open", async () => {
  seedLoggedInUser("client", { id: 9 });

  const bookedVehicle = makeBookableVehicle({ id: 5, driverId: 7, plateNumber: "BOOKED05" });
  const freeVehicle = makeBookableVehicle({ id: 6, driverId: 7, plateNumber: "FREE0006" });

  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  const tomorrowDateStr = new Date(tomorrow.getFullYear(), tomorrow.getMonth(), tomorrow.getDate())
    .toISOString()
    .split("T")[0];

  server.use(
    driverVehiclesResponse([bookedVehicle, freeVehicle]),
    availabilityResponse(makeAvailability()),
    bookedTimesResponse([]),
    zonesResponse(ZONES),
    userBookingsResponse([
      makeUserBooking({ vehicleId: 5, driverId: 7, date: tomorrowDateStr, shiftPeriod: "MORNING" }),
    ])
  );

  renderBookingCreate();

  await selectTomorrow();

  // Vehicle 6 is offered; vehicle 5 (already booked that morning) is not.
  expect(await screen.findByText("FREE0006")).toBeInTheDocument();
  expect(screen.queryByText("BOOKED05")).not.toBeInTheDocument();
});

test("offers any zone as destination but restricts origin to the vehicle's pickup zones", async () => {
  seedLoggedInUser("client", { id: 9 });

  const vehicle = makeBookableVehicle({ id: 5, driverId: 7 });
  server.use(
    driverVehiclesResponse([vehicle]),
    availabilityResponse(makeAvailability()),
    bookedTimesResponse([]),
    zonesResponse(ZONES),
    userBookingsResponse()
  );

  renderBookingCreate();

  await selectTomorrowAndShift();

  const [originCombobox, destinationCombobox] = screen.getAllByRole("combobox");

  // Origin lists only the vehicle's pickup zones (1 and 2) — Recoleta (zone 3) is excluded.
  await user.click(originCombobox);
  expect(await screen.findByText(PALERMO_OPTION)).toBeInTheDocument();
  expect(screen.queryByText(RECOLETA_OPTION)).not.toBeInTheDocument();
  await user.click(originCombobox); // toggle it closed before opening the other

  // Destination lists every zone, including the non-pickup Recoleta.
  await user.click(destinationCombobox);
  expect(await screen.findByText(RECOLETA_OPTION)).toBeInTheDocument();
});
