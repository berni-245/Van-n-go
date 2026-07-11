import { screen } from "@solidjs/testing-library";
import userEvent from "@testing-library/user-event";
import { http, HttpResponse } from "msw";
import { server } from "@/test/msw/server";
import { renderRoute } from "@/test/renderRoute";
import { driversResponse, driversErrorResponse, makeDriver, DRIVERS_ENDPOINT } from "@/test/msw/driverFixtures";
import type { Driver } from "@/api/types";
import Search from "@/pages/Search";

const user = userEvent.setup();

// A zoneId in the URL is what makes Search actually call the API
// (see the `if (!searchParams.zoneId) return null;` guard in Search.tsx).
const SEARCH_URL = "/search?zoneQuery=Palermo&zoneId=5";

test("renders each driver returned by the API", async () => {
  const drivers = [makeDriver({ id: 1, username: "juan_perez" }), makeDriver({ id: 2, username: "maria_gomez" })];
  server.use(driversResponse(drivers));

  renderRoute("/search", SEARCH_URL, Search);

  expect(await screen.findByText("juan_perez")).toBeInTheDocument();
  expect(screen.getByText("maria_gomez")).toBeInTheDocument();
});

test("shows a no-results message when the API returns an empty list", async () => {
  server.use(driversResponse([]));

  renderRoute("/search", SEARCH_URL, Search);

  expect(await screen.findByText("No drivers found for your search.")).toBeInTheDocument();
});

test("falls back to the no-results state when the API request fails", async () => {
  // Search.tsx / api.searchDrivers currently treats any non-ok response as
  // "no drivers" rather than surfacing an error — this test documents that
  // behavior. If you add explicit error-state UI later, update this test.
  server.use(driversErrorResponse(500));

  renderRoute("/search", SEARCH_URL, Search);

  expect(await screen.findByText("No drivers found for your search.")).toBeInTheDocument();
});

test("shows pagination and requests the next page on click", async () => {
  server.use(
    driversByPage({
      1: [makeDriver({ id: 1, username: "page_one_driver" })],
      2: [makeDriver({ id: 2, username: "page_two_driver" })],
    })
  );

  renderRoute("/search", SEARCH_URL + "&page=1", Search);

  expect(await screen.findByText("page_one_driver")).toBeInTheDocument();
  expect(screen.getByText("1 / 3")).toBeInTheDocument();

  await user.click(screen.getByRole("button", { name: "→" }));

  expect(await screen.findByText("page_two_driver")).toBeInTheDocument();
});

// Small local helper for the pagination test: responds differently
// depending on the requested `page` query param, and always reports 3
// total pages via the Link header.
function driversByPage(pages: Record<number, Driver[]>) {
  return http.get(DRIVERS_ENDPOINT, ({ request }) => {
    const url = new URL(request.url);
    const page = Number(url.searchParams.get("page") ?? "1");
    return HttpResponse.json(pages[page] ?? [], {
      headers: {
        Link: `<${DRIVERS_ENDPOINT}?page=1>; rel="first", <${DRIVERS_ENDPOINT}?page=3>; rel="last"`,
      },
    });
  });
}
