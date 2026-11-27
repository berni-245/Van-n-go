import { http, HttpResponse } from "msw";
import type { Message } from "@/api/types";

export const MESSAGES_ENDPOINT = "*/api/messages";

export function makeMessage(overrides: Partial<Message> = {}): Message {
  return {
    content: "Hey, what time works for you?",
    timeSent: "2026-07-10T09:00:00Z",
    sentByDriver: false,
    self: "http://localhost:3000/api/messages/1",
    ...overrides,
  };
}

/**
 * `messages` should be given newest-first, the same order the real API
 * returns them in (Chat.tsx reverses it locally before rendering).
 * `parseTotalPages` (api.ts) is the regex-based Link parser (same one
 * Search.tsx uses), so the Link header just needs to contain a
 * `?page=N` + `rel="last"` pair — no real URL required.
 */
export function messagesResponse(messages: Message[], maxPages = 1) {
  return http.get(MESSAGES_ENDPOINT, ({ request }) => {
    if (request.method !== "GET") return undefined;
    return HttpResponse.json(messages, {
      headers: { Link: `<http://localhost:3000/api/messages?page=${maxPages}>; rel="last"` },
    });
  });
}

/** getMessages() treats any non-ok response as "no conversation" (returns null), not an error. */
export function messagesNotFoundResponse(status = 404) {
  return http.get(MESSAGES_ENDPOINT, () => new HttpResponse(null, { status }));
}

/** A network-level failure, distinct from messagesNotFoundResponse: this throws instead of resolving. */
export function messagesNetworkErrorResponse() {
  return http.get(MESSAGES_ENDPOINT, () => HttpResponse.error());
}

export function sendMessageResponse(message: Message) {
  return http.post(MESSAGES_ENDPOINT, () => HttpResponse.json(message, { status: 201 }));
}
