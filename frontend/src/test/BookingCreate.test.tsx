import { screen, waitFor } from "@solidjs/testing-library";
import userEvent from "@testing-library/user-event";
import { server } from "@/test/msw/server";
import { renderRoute } from "@/test/renderRoute";
import { seedLoggedInUser } from "@/test/msw/authFixtures";
import { zonesResponse, makeZone } from "@/test/msw/zoneFixtures";
import {
  makeBookableVehicle,
  makeAvailability,
  driverVehiclesResponse,
  availabilityResponse,
  bookedTimesResponse,
  createBookingSuccessResponse,
  createBookingFieldErrorsResponse,
  createBookingConflictResponse,
} from "@/test/msw/bookingCreateFixtures";
import BookingCreate from "@/pages/BookingCreate";

const user = userEvent.setup();

const ZONES = [
  makeZone({ id: 1, neighborhood: { name: "Palermo" } }),
  makeZone({ id: 2, neighborhood: { name: "Belgrano" } }),
];
// Built directly in BookingCreate.tsx's currentZoneOptions (not translated),
// so it's safe to assert/select on regardless of the app's locale.
const PALERMO_OPTION = "Palermo, Buenos Aires";
const BELGRANO_OPTION = "Belgrano, Buenos Aires";

function renderBookingCreate(extraRoutes: { path: string; component: () => unknown }[] = []) {
  return renderRoute("/bookings/:driverId/new", "/bookings/7/new", BookingCreate, extraRoutes as never);
}

/**
 * Drives the page up through picking today's date and an available shift —
 * the common setup for every test below. Availability is seeded for every
 * day of the week (see makeAvailability), so "today" is always bookable
 * regardless of when the test suite actually runs.
 */
async function selectTodayAndShift() {
  const today = String(new Date().getDate());
  // The day button renders disabled until availability data finishes
  // loading, then flips to enabled in place (BookingCreate.tsx uses <Index>
  // for the calendar grid, keyed by position, so this is the same DOM node
  // throughout — no need to re-query on each poll).
  const dayButton = await screen.findByRole("button", { name: today });
  await waitFor(() => expect(dayButton).not.toBeDisabled());
  await user.click(dayButton);

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
  await pickZone(1, BELGRANO_OPTION);
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
    createBookingSuccessResponse({ id: 42 })
  );

  renderBookingCreate([{ path: "/client/bookings", component: () => <p>bookings-list-stub</p> }]);

  await selectTodayAndShift();
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
    createBookingFieldErrorsResponse([
      {
        type: "ValidationError",
        entity: "destinationZoneId",
        description: "The destination zone must differ from the origin.",
      },
    ])
  );

  renderBookingCreate();

  await selectTodayAndShift();
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
    createBookingConflictResponse()
  );

  renderBookingCreate([{ path: "/client/bookings", component: () => <p>bookings-list-stub</p> }]);

  await selectTodayAndShift();
  await fillAndSubmit("Moving some boxes across town.");

  // stays on the booking form; conflict is surfaced via toast, not a field error
  await waitFor(() => expect(document.querySelector('button[type="submit"]')).not.toBeDisabled());
  expect(screen.queryByText("bookings-list-stub")).not.toBeInTheDocument();
});
