import { screen } from "@solidjs/testing-library";
import { server } from "@/test/msw/server";
import { renderRoute } from "@/test/renderRoute";
import { seedLoggedInUser } from "@/test/msw/authFixtures";
import { makeVehicle, recordVehiclesRequest, vehiclesResponse } from "@/test/msw/vehicleFixtures";
import Vehicles from "@/pages/Vehicles";

// Vehicles.tsx reads the driver id straight off the logged-in session
// (appState.user.id), so every test seeds a driver before rendering. Each
// VehicleCard renders the plate number verbatim, which is a stable,
// locale-independent thing to assert on.

test("renders a card for each of the logged-in driver's vehicles", async () => {
  seedLoggedInUser("driver", { id: 9 });

  server.use(
    vehiclesResponse([makeVehicle({ id: 1, plateNumber: "AB123CD" }), makeVehicle({ id: 2, plateNumber: "XY987ZW" })])
  );

  renderRoute("/vehicles", "/vehicles", Vehicles);

  expect(await screen.findByText("AB123CD")).toBeInTheDocument();
  expect(await screen.findByText("XY987ZW")).toBeInTheDocument();
});

test("shows the vehicle's details (volume, rate, description)", async () => {
  seedLoggedInUser("driver", { id: 9 });

  server.use(
    vehiclesResponse([
      makeVehicle({ id: 1, plateNumber: "AB123CD", volumeM3: 12, hourlyRate: 1500, description: "Cargo van" }),
    ])
  );

  renderRoute("/vehicles", "/vehicles", Vehicles);

  expect(await screen.findByText("AB123CD")).toBeInTheDocument();
  expect(screen.getByText("12 m³")).toBeInTheDocument();
  expect(screen.getByText("$1500/h")).toBeInTheDocument();
  expect(screen.getByText("Cargo van")).toBeInTheDocument();
});

test("shows the empty state when the driver has no vehicles", async () => {
  seedLoggedInUser("driver", { id: 9 });
  server.use(vehiclesResponse([]));

  renderRoute("/vehicles", "/vehicles", Vehicles);

  // The empty-state icon is a plain, untranslated emoji in the source, so it's
  // a stable, locale-independent marker for the "no vehicles" branch.
  expect(await screen.findByText("🚐")).toBeInTheDocument();
});

test("requests vehicles for the logged-in driver's own id, starting at page 1", async () => {
  seedLoggedInUser("driver", { id: 42 });

  const { handler, calls } = recordVehiclesRequest([makeVehicle({ id: 1, plateNumber: "AB123CD" })]);
  server.use(handler);

  renderRoute("/vehicles", "/vehicles", Vehicles);

  // Wait until the request has actually gone out (the card confirms it landed).
  await screen.findByText("AB123CD");

  expect(calls.length).toBeGreaterThan(0);
  expect(calls[0].driverId).toBe("42");
  expect(calls[0].page).toBe("1");
});
