import {screen, waitFor} from "@solidjs/testing-library";
import userEvent from "@testing-library/user-event";
import {server} from "@/test/msw/server";
import {renderRoute} from "@/test/renderRoute";
import {seedLoggedInUser} from "@/test/msw/authFixtures";
import {
    createVehicleFieldErrors,
    createVehicleLimitReached,
    createVehicleSuccess,
    recordCreateVehicle,
} from "@/test/msw/createVehicleFixtures";
import CreateVehicle from "@/pages/CreateVehicle";

const user = userEvent.setup();

// Every field on the form carries a stable `name`, so query by that rather
// than relying on translated label text or DOM order. Field errors render with
// a leading "• " bullet (TextInput), so error assertions use regexes/substrings
// rather than exact strings.
function plateInput() {
  return document.querySelector('input[name="plateNumber"]') as HTMLInputElement;
}
function rateInput() {
  return document.querySelector('input[name="hourlyRate"]') as HTMLInputElement;
}
function volumeInput() {
  return document.querySelector('input[name="volumeM3"]') as HTMLInputElement;
}
function descriptionInput() {
  return document.querySelector('textarea[name="description"]') as HTMLTextAreaElement;
}
function submitButton() {
  return document.querySelector('button[type="submit"]') as HTMLButtonElement;
}

/**
 * Fills the form with values that pass the component's client-side validation
 * (a well-formed plate, an in-range rate/volume, a short description), so a
 * submit actually reaches the API. Override any field to exercise a specific
 * validation branch.
 */
async function fillValidForm({ plate = "AB123CD", rate = "1500", volume = "12", description = "Cargo van" } = {}) {
  await waitFor(() => expect(plateInput()).toBeInTheDocument()); // wait for the form to mount
  await user.type(plateInput(), plate);
  await user.type(rateInput(), rate);
  await user.type(volumeInput(), volume);
  await user.type(descriptionInput(), description);
}

const NEW_ROUTE = "/driver/vehicles/new";
const listStub = [{ path: "/driver/vehicles", component: () => <p>vehicles-list-stub</p> }];

test("creates a vehicle and navigates to the vehicles list on success", async () => {
  seedLoggedInUser("driver", { id: 7 });
  server.use(createVehicleSuccess({ id: 42 }));

  renderRoute(NEW_ROUTE, NEW_ROUTE, CreateVehicle, listStub);

  await fillValidForm();
  await user.click(submitButton());

  expect(await screen.findByText("vehicles-list-stub")).toBeInTheDocument();
});

test("submits the entered data (numbers parsed) to the logged-in driver's endpoint", async () => {
  seedLoggedInUser("driver", { id: 7 });
  const { handler, calls } = recordCreateVehicle({ id: 42 });
  server.use(handler);

  renderRoute(NEW_ROUTE, NEW_ROUTE, CreateVehicle, listStub);

  await fillValidForm({ plate: "AB123CD", rate: "1500", volume: "12", description: "Cargo van" });
  await user.click(submitButton());

  // Navigation confirms the request settled successfully.
  await screen.findByText("vehicles-list-stub");

  expect(calls).toHaveLength(1);
  expect(calls[0].driverId).toBe("7");
  expect(calls[0].body).toEqual({
    plateNumber: "AB123CD",
    hourlyRate: 1500,
    volumeM3: 12,
    description: "Cargo van",
  });
});

test("shows a required-field error for every empty field and never calls the API", async () => {
  seedLoggedInUser("driver", { id: 7 });
  const { handler, calls } = recordCreateVehicle();
  server.use(handler);

  renderRoute(NEW_ROUTE, NEW_ROUTE, CreateVehicle);

  await waitFor(() => expect(submitButton()).toBeInTheDocument());
  await user.click(submitButton());

  // One per required field: plateNumber, hourlyRate, volumeM3, description.
  await waitFor(() => expect(screen.getAllByText(/This field is required/)).toHaveLength(4));
  expect(calls).toHaveLength(0);
});

test("rejects an invalid license plate format client-side, without calling the API", async () => {
  seedLoggedInUser("driver", { id: 7 });
  const { handler, calls } = recordCreateVehicle();
  server.use(handler);

  renderRoute(NEW_ROUTE, NEW_ROUTE, CreateVehicle);

  // Everything else is valid, so only the plate's format check fails.
  await fillValidForm({ plate: "NOTAPLATE" });
  await user.click(submitButton());

  expect(await screen.findByText(/Invalid license plate format/i)).toBeInTheDocument();
  expect(calls).toHaveLength(0);
});

test("surfaces an API field error next to the relevant field and stays on the form", async () => {
  seedLoggedInUser("driver", { id: 7 });
  server.use(
    createVehicleFieldErrors([
      { type: "ValidationError", entity: "plateNumber", description: "That plate is already registered." },
    ]),
  );

  renderRoute(NEW_ROUTE, NEW_ROUTE, CreateVehicle, listStub);

  await fillValidForm();
  await user.click(submitButton());

  expect(await screen.findByText(/That plate is already registered\./)).toBeInTheDocument();
  expect(screen.queryByText("vehicles-list-stub")).not.toBeInTheDocument();
});

test("shows a toast and stays on the form when the driver hit their vehicle limit (409)", async () => {
  seedLoggedInUser("driver", { id: 7 });
  server.use(createVehicleLimitReached());

  renderRoute(NEW_ROUTE, NEW_ROUTE, CreateVehicle, listStub);

  await fillValidForm();
  await user.click(submitButton());

  // The 409 is surfaced via a toast, not a field error.
  expect(await screen.findByText("You have reached the maximum vehicle limit")).toBeInTheDocument();
  // the submission settles and the button re-enables
  await waitFor(() => expect(submitButton()).not.toBeDisabled());
  expect(screen.queryByText("vehicles-list-stub")).not.toBeInTheDocument();
});
