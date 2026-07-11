import {screen, waitFor} from "@solidjs/testing-library";
import userEvent from "@testing-library/user-event";
import type {RequestHandler} from "msw";
import {server} from "@/test/msw/server";
import {renderRoute} from "@/test/renderRoute";
import {seedLoggedInUser} from "@/test/msw/authFixtures";
import {makeZone, zonesResponse} from "@/test/msw/zoneFixtures";
import {
    getVehicleResponse,
    makeEditableVehicle,
    makeVehicleAvailability,
    recordUpdateVehicle,
    updateVehicleAccessError,
    updateVehicleFieldErrors,
    updateVehicleSuccess,
    vehicleAvailabilityResponse,
} from "@/test/msw/vehicleEditFixtures";
import VehicleEdit from "@/pages/VehicleEdit";

const user = userEvent.setup();

// The edit page renders three separate forms (general data, availability,
// picture). Every field carries a stable `name`; the general-data form is the
// first in the DOM, so its submit is the first button[type="submit"]. Field
// errors render with a leading "• " (TextInput), so error assertions use
// regexes rather than exact strings.
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
function saveChangesButton() {
  return document.querySelector('button[type="submit"]') as HTMLButtonElement;
}

/**
 * Registers the queries the page loads on mount (vehicle + zones +
 * availability; the picture is fetched from an /images/ URL served by the
 * global stub) plus the given PATCH handler, seeds a logged-in driver, and
 * renders the edit route for vehicle 5. Returns once handlers are in place;
 * callers await the form fields to confirm the load finished.
 */
function renderEdit(updateHandler: RequestHandler = updateVehicleSuccess()) {
  seedLoggedInUser("driver", { id: 7 });
  server.use(
    getVehicleResponse(makeEditableVehicle({ id: 5 })),
    zonesResponse([makeZone({ id: 1 })]),
    vehicleAvailabilityResponse(makeVehicleAvailability()),
    updateHandler,
  );
  return renderRoute("/driver/vehicles/:vehicleId/edit", "/driver/vehicles/5/edit", VehicleEdit);
}

async function waitForForm() {
  await waitFor(() => expect(plateInput()).toBeInTheDocument());
}

async function retypePlate(value: string) {
  await user.clear(plateInput());
  await user.type(plateInput(), value);
}

test("pre-fills the form with the vehicle's current values", async () => {
  renderEdit();
  await waitForForm();

  expect(plateInput().value).toBe("AB123CD");
  expect(rateInput().value).toBe("1500");
  expect(volumeInput().value).toBe("12");
  expect(descriptionInput().value).toBe("Cargo van");
});

test("saves an edit and shows a success toast (staying on the page)", async () => {
  renderEdit(updateVehicleSuccess());
  await waitForForm();

  await retypePlate("XY987ZW");
  await user.click(saveChangesButton());

  expect(await screen.findByText("Vehicle successfully updated!")).toBeInTheDocument();
});

test("sends the edited fields to the vehicle's update endpoint (numbers parsed)", async () => {
  const { handler, calls } = recordUpdateVehicle();
  renderEdit(handler);
  await waitForForm();

  await retypePlate("XY987ZW");
  await user.click(saveChangesButton());

  await screen.findByText("Vehicle successfully updated!");

  expect(calls).toHaveLength(1);
  expect(calls[0].driverId).toBe("7");
  expect(calls[0].vehicleId).toBe("5");
  expect(calls[0].body).toEqual({
    plateNumber: "XY987ZW",
    volumeM3: 12,
    hourlyRate: 1500,
    description: "Cargo van",
  });
});

test("rejects an invalid license plate format client-side, without calling the API", async () => {
  const { handler, calls } = recordUpdateVehicle();
  renderEdit(handler);
  await waitForForm();

  await retypePlate("NOTAPLATE");
  await user.click(saveChangesButton());

  expect(await screen.findByText(/Invalid license plate format/i)).toBeInTheDocument();
  expect(calls).toHaveLength(0);
});

test("surfaces an API field error next to the relevant field", async () => {
  renderEdit(
    updateVehicleFieldErrors([
      { type: "ValidationError", entity: "plateNumber", description: "That plate is already registered." },
    ]),
  );
  await waitForForm();

  await retypePlate("XY987ZW");
  await user.click(saveChangesButton());

  expect(await screen.findByText(/That plate is already registered\./)).toBeInTheDocument();
});

test("shows an access-error toast when the update is forbidden (403)", async () => {
  renderEdit(updateVehicleAccessError(403));
  await waitForForm();

  await retypePlate("XY987ZW");
  await user.click(saveChangesButton());

  expect(await screen.findByText("Could not access the vehicle or it is unavailable")).toBeInTheDocument();
  // the submission settles and the button re-enables
  await waitFor(() => expect(saveChangesButton()).not.toBeDisabled());
});
