import { screen, waitFor } from "@solidjs/testing-library";
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
import {
  makeMessage,
  messagesResponse,
  messagesNotFoundResponse,
  messagesNetworkErrorResponse,
  sendMessageResponse,
  MESSAGES_ENDPOINT,
} from "@/test/msw/chatFixtures";
import Chat from "@/pages/Chat";

const user = userEvent.setup();

function headerName() {
  return document.querySelector(".border-b span") as HTMLElement;
}
function messageBubbles() {
  return document.querySelectorAll(".whitespace-pre-wrap.break-words");
}
function textarea() {
  return document.querySelector("textarea") as HTMLTextAreaElement;
}
function sendButton() {
  return document.querySelector(".border-t button") as HTMLButtonElement;
}
function loadOlderButton() {
  return document.querySelector(".mb-4.text-center button") as HTMLButtonElement;
}

test("shows messages oldest-first and the other party's name", async () => {
  seedLoggedInUser("client", { id: 9 });

  // The API returns newest-first; Chat.tsx reverses it for display.
  const newer = makeMessage({ content: "Yes, morning works.", timeSent: "2026-07-10T09:00:00Z", sentByDriver: false });
  const older = makeMessage({
    content: "Hi, are you available Friday?",
    timeSent: "2026-07-09T09:00:00Z",
    sentByDriver: true,
  });

  server.use(messagesResponse([newer, older]), driverUserResponse(makeDriverUser({ id: 7, username: "juan_driver" })));

  renderRoute("/chat", "/chat?driverId=7&clientId=9", Chat);

  await waitFor(() => expect(headerName()).toHaveTextContent("juan_driver"));

  const bubbles = messageBubbles();
  expect(bubbles).toHaveLength(2);
  expect(bubbles[0]).toHaveTextContent("Hi, are you available Friday?");
  expect(bubbles[1]).toHaveTextContent("Yes, morning works.");
});

test("sends a new message, appends it, and clears the input", async () => {
  seedLoggedInUser("client", { id: 9 });
  server.use(
    messagesResponse([]),
    driverUserResponse(makeDriverUser({ id: 7, username: "juan_driver" })),
    sendMessageResponse(
      makeMessage({ content: "See you then!", sentByDriver: false, timeSent: "2026-07-10T10:00:00Z" })
    )
  );

  renderRoute("/chat", "/chat?driverId=7&clientId=9", Chat);
  await screen.findByText("juan_driver");

  await user.type(textarea(), "See you then!");
  await user.click(sendButton());

  expect(await screen.findByText("See you then!")).toBeInTheDocument();
  expect(textarea()).toHaveValue("");
});

test("redirects to the bookings list when there's no conversation to load", async () => {
  seedLoggedInUser("client", { id: 9 });
  server.use(messagesNotFoundResponse(404), driverUserResponse(makeDriverUser({ id: 7 })));

  renderRoute("/chat", "/chat?driverId=7&clientId=9", Chat, [
    { path: "/client/bookings", component: () => <p>bookings-list-stub</p> },
  ]);

  expect(await screen.findByText("bookings-list-stub")).toBeInTheDocument();
});

test("redirects to the bookings list if loading messages fails outright (network error)", async () => {
  seedLoggedInUser("driver", { id: 7 });
  server.use(messagesNetworkErrorResponse(), clientResponse(makeClient({ id: 9 })));

  renderRoute("/chat", "/chat?driverId=7&clientId=9", Chat, [
    { path: "/driver/bookings", component: () => <p>bookings-list-stub</p> },
  ]);

  expect(await screen.findByText("bookings-list-stub")).toBeInTheDocument();
});

test("shows a load-older button when there are more pages, and prepends older messages when clicked", async () => {
  seedLoggedInUser("client", { id: 9 });

  const page1 = [makeMessage({ content: "Newest message", timeSent: "2026-07-10T09:00:00Z" })];
  const page2 = [makeMessage({ content: "Oldest message", timeSent: "2026-07-01T09:00:00Z" })];

  server.use(
    http.get(MESSAGES_ENDPOINT, ({ request }) => {
      const url = new URL(request.url);
      const body = url.searchParams.get("page") === "2" ? page2 : page1;
      return HttpResponse.json(body, {
        headers: { Link: `<http://localhost:3000/api/messages?page=2>; rel="last"` },
      });
    }),
    driverUserResponse(makeDriverUser({ id: 7, username: "juan_driver" }))
  );

  renderRoute("/chat", "/chat?driverId=7&clientId=9", Chat);
  await screen.findByText("Newest message");

  await user.click(loadOlderButton());

  await screen.findByText("Oldest message");
  const bubbles = messageBubbles();
  expect(bubbles[0]).toHaveTextContent("Oldest message"); // prepended, so it's now on top
  expect(bubbles[1]).toHaveTextContent("Newest message");
});
