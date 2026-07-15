import { createSignal } from "solid-js";
import { screen, waitFor } from "@solidjs/testing-library";
import userEvent from "@testing-library/user-event";
import { http, HttpResponse } from "msw";
import { server } from "@/test/msw/server";
import { renderRoute } from "@/test/renderRoute";
import { makeZone, zonesResponse, zonesErrorResponse, ZONES_ENDPOINT } from "@/test/msw/zoneFixtures";
import ZoneInput from "@/components/inputs/ZoneInput";

const user = userEvent.setup();

const SOHO_LABEL = "Palermo Soho, Buenos Aires, Argentina";
const HOLLYWOOD_LABEL = "Palermo Hollywood, Buenos Aires, Argentina";

const palermoSoho = makeZone({ id: 1, neighborhood: { name: "Palermo Soho" } });
const palermoHollywood = makeZone({ id: 2, neighborhood: { name: "Palermo Hollywood" } });

// Controlled component harness; renderRoute gives it the Router/AppContext useApi() needs.
function ZoneInputHarness(props: { excludeIds?: number[]; includeIds?: number[] }) {
  const [value, setValue] = createSignal("");
  const [selectedId, setSelectedId] = createSignal<number | null>(null);

  return (
    <div>
      <ZoneInput
        label="Zone"
        value={value()}
        onInput={setValue}
        onSelect={setSelectedId}
        excludeIds={props.excludeIds}
        includeIds={props.includeIds}
      />
      <p data-testid="selected-zone-id">{selectedId() === null ? "none" : selectedId()}</p>
    </div>
  );
}

function renderHarness(excludeIds?: number[], includeIds?: number[]) {
  return renderRoute("/zone-harness", "/zone-harness", () => (
    <ZoneInputHarness excludeIds={excludeIds} includeIds={includeIds} />
  ));
}

test("shows matching suggestions once the zones API call resolves", async () => {
  server.use(zonesResponse([palermoSoho, palermoHollywood]));
  renderHarness();

  const input = screen.getByRole("textbox");
  await user.click(input); // triggers the onFocus fetch
  await user.type(input, "Palermo");

  expect(await screen.findByText(SOHO_LABEL)).toBeInTheDocument();
  expect(screen.getByText(HOLLYWOOD_LABEL)).toBeInTheDocument();
});

test("selecting a suggestion with the mouse fills the input and reports the zone id", async () => {
  server.use(zonesResponse([palermoSoho, palermoHollywood]));
  renderHarness();

  const input = screen.getByRole("textbox");
  await user.click(input);
  await user.type(input, "Hollywood");

  await user.click(await screen.findByText(HOLLYWOOD_LABEL));

  expect(input).toHaveValue(HOLLYWOOD_LABEL);
  expect(screen.getByTestId("selected-zone-id")).toHaveTextContent("2");
  // suggestion list closes after selecting
  expect(screen.queryByText(HOLLYWOOD_LABEL)).not.toBeInTheDocument();
});

test("arrow keys navigate suggestions and Enter selects the highlighted one", async () => {
  server.use(zonesResponse([palermoSoho, palermoHollywood]));
  renderHarness();

  const input = screen.getByRole("textbox");
  await user.click(input);
  await user.type(input, "Palermo");
  await screen.findByText(SOHO_LABEL);

  await user.keyboard("{ArrowDown}"); // highlights Soho (index 0)
  await user.keyboard("{ArrowDown}"); // highlights Hollywood (index 1)
  await user.keyboard("{Enter}");

  expect(input).toHaveValue(HOLLYWOOD_LABEL);
  expect(screen.getByTestId("selected-zone-id")).toHaveTextContent("2");
});

test("falls back to no suggestions when the zones API call fails", async () => {
  const consoleError = vi.spyOn(console, "error").mockImplementation(() => {});
  server.use(zonesErrorResponse(500));
  renderHarness();

  const input = screen.getByRole("textbox");
  await user.click(input);
  await user.type(input, "Palermo");

  await waitFor(() => expect(consoleError).toHaveBeenCalled());
  expect(screen.queryByText(/Palermo/)).not.toBeInTheDocument();
  expect(screen.getByTestId("selected-zone-id")).toHaveTextContent("none");

  consoleError.mockRestore();
});

test("does not refetch zones after the first successful load", async () => {
  let requestCount = 0;
  server.use(
    http.get(ZONES_ENDPOINT, () => {
      requestCount++;
      return HttpResponse.json([palermoSoho, palermoHollywood]);
    })
  );
  renderHarness();

  const input = screen.getByRole("textbox");
  await user.click(input); // 1st fetch, triggered on focus
  await user.type(input, "Palermo Soho");
  await screen.findByText(SOHO_LABEL);

  await user.keyboard("{Escape}"); // close suggestions without selecting
  await user.type(input, "!"); // another keystroke -> another debounced fetch *attempt*

  // let the 180ms debounce fire
  await new Promise((resolve) => setTimeout(resolve, 300));
  expect(requestCount).toBe(1);
});

test("auto-selects the zone on blur when the typed text exactly matches one suggestion", async () => {
  server.use(zonesResponse([palermoSoho, palermoHollywood]));
  renderHarness();

  const input = screen.getByRole("textbox");
  await user.click(input);
  await user.type(input, SOHO_LABEL);
  await user.tab(); // blur

  await waitFor(() => expect(screen.getByTestId("selected-zone-id")).toHaveTextContent("1"));
});

test("clears the selection on blur when the typed text doesn't match any zone", async () => {
  server.use(zonesResponse([palermoSoho, palermoHollywood]));
  renderHarness();

  const input = screen.getByRole("textbox");
  await user.click(input);
  await user.type(input, "Hollywood");
  await user.click(await screen.findByText(HOLLYWOOD_LABEL));
  expect(screen.getByTestId("selected-zone-id")).toHaveTextContent("2");

  await user.clear(input);
  await user.type(input, "Somewhere else entirely");
  await user.tab(); // blur

  await waitFor(() => expect(screen.getByTestId("selected-zone-id")).toHaveTextContent("none"));
});

test("omits excluded zone ids from the suggestion list", async () => {
  server.use(zonesResponse([palermoSoho, palermoHollywood]));
  renderHarness([palermoHollywood.id]);

  const input = screen.getByRole("textbox");
  await user.click(input);
  await user.type(input, "Palermo");

  expect(await screen.findByText(SOHO_LABEL)).toBeInTheDocument();
  expect(screen.queryByText(HOLLYWOOD_LABEL)).not.toBeInTheDocument();
});

test("does not auto-select an excluded zone on blur even on an exact text match", async () => {
  server.use(zonesResponse([palermoSoho, palermoHollywood]));
  renderHarness([palermoSoho.id]);

  const input = screen.getByRole("textbox");
  await user.click(input);
  await user.type(input, SOHO_LABEL);
  await user.tab(); // blur

  await waitFor(() => expect(screen.getByTestId("selected-zone-id")).toHaveTextContent("none"));
});

test("restricts suggestions to includeIds when provided", async () => {
  server.use(zonesResponse([palermoSoho, palermoHollywood]));
  renderHarness(undefined, [palermoSoho.id]);

  const input = screen.getByRole("textbox");
  await user.click(input);
  await user.type(input, "Palermo");

  expect(await screen.findByText(SOHO_LABEL)).toBeInTheDocument();
  expect(screen.queryByText(HOLLYWOOD_LABEL)).not.toBeInTheDocument();
});

test("does not auto-select a zone outside includeIds on blur even on an exact text match", async () => {
  server.use(zonesResponse([palermoSoho, palermoHollywood]));
  renderHarness(undefined, [palermoSoho.id]);

  const input = screen.getByRole("textbox");
  await user.click(input);
  await user.type(input, HOLLYWOOD_LABEL);
  await user.tab(); // blur

  await waitFor(() => expect(screen.getByTestId("selected-zone-id")).toHaveTextContent("none"));
});

test("an empty includeIds list excludes every zone from suggestions", async () => {
  server.use(zonesResponse([palermoSoho, palermoHollywood]));
  renderHarness(undefined, []);

  const input = screen.getByRole("textbox");
  await user.click(input);
  await user.type(input, "Palermo");

  await waitFor(() => expect(screen.queryByText(SOHO_LABEL)).not.toBeInTheDocument());
  expect(screen.queryByText(HOLLYWOOD_LABEL)).not.toBeInTheDocument();
});
