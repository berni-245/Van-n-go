import {screen, waitFor} from "@solidjs/testing-library";
import userEvent from "@testing-library/user-event";
import {server} from "@/test/msw/server";
import {renderRoute} from "@/test/renderRoute";
import {seedLoggedInUser} from "@/test/msw/authFixtures";
import {makeZone, zonesResponse} from "@/test/msw/zoneFixtures";
import {
    availabilityResponse,
    bookedTimesResponse,
    createBookingConflictResponse,
    createBookingFieldErrorsResponse,
    createBookingSuccessResponse,
    driverVehiclesResponse,
    makeAvailability,
    makeBookableVehicle,
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
 * Drives the page up through picking tomorrow's date and an available shift —
 * the common setup for every test below. Bookings are only allowed from
 * tomorrow onward (same-day is rejected by the backend), so today renders
 * disabled. Availability is seeded for every day of the week (see
 * makeAvailability), so tomorrow is always bookable regardless of when the
 * suite runs. When today is the last day of the month, tomorrow falls into
 * the next month and the calendar auto-advances; re-querying the button by
 * its day number inside waitFor handles that DOM swap transparently.
 */
async function selectTomorrowAndShift() {
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  const tomorrowDay = String(tomorrow.getDate());

  const dayButton = await waitFor(() => {
    const btn = screen.getByRole("button", { name: tomorrowDay });
    expect(btn).not.toBeDisabled();
    return btn;
  });
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
    createBookingConflictResponse()
  );

  renderBookingCreate([{ path: "/client/bookings", component: () => <p>bookings-list-stub</p> }]);

  await selectTomorrowAndShift();
  await fillAndSubmit("Moving some boxes across town.");

  // stays on the booking form; conflict is surfaced via toast, not a field error
  await waitFor(() => expect(document.querySelector('button[type="submit"]')).not.toBeDisabled());
  expect(screen.queryByText("bookings-list-stub")).not.toBeInTheDocument();
});
