import { delay, http, HttpResponse } from "msw";
import { setupWorker } from "msw/browser";
import { authorizedResponse, handleAuthorization, publicResponse } from "./auth";
import { drivers } from "./data/drivers";
import { getMockUser } from "./users";

export const worker = setupWorker(
  http.all(
    ({ request }) => {
      const { pathname } = new URL(request.url);
      return pathname.startsWith("/api");
    },
    ({ request, requestId }) => {
      console.log(`In globe req ${requestId}:`, request);
      handleAuthorization(request);
    }
  ),

  // TODO: for testing, remove later
  http.get("/api/auth-required", ({ request }) => {
    return authorizedResponse({
      request,
      response: (_) => ({
        body: {
          message: "Mocked response",
        },
        status: 200,
      }),
    });
  }),

  http.get("/api", ({ request }) => {
    return publicResponse({
      request,
      response: {
        body: {
          links: ["/api/drivers", "/api/drivers/{id}/vehicles", "..."],
        },
        status: 200,
      },
    });
  }),

  http.get<{ id: string }>("/api/clients/:id", async ({ request, params }) => {
    await delay(500);
    const id = parseInt(params.id);
    return authorizedResponse({
      request,
      response: (payload) => {
        const user = getMockUser(payload, { type: "client", id });
        if (!user) {
          return {
            body: {
              type: "unautrhorized",
              description: "User unautrhorized for this resource",
            },
            status: 401,
          };
        }
        return {
          body: user,
          status: 200,
        };
      },
    });
  }),

  http.get<{ id: string }>("/api/drivers/:id", async ({ request, params }) => {
    await delay(500);
    const id = parseInt(params.id);
    return authorizedResponse({
      request,
      response: (payload) => {
        const user = getMockUser(payload, { type: "driver", id });
        if (!user) {
          return {
            body: {
              type: "unautrhorized",
              description: "User unautrhorized for this resource",
            },
            status: 401,
          };
        }
        return {
          body: user,
          status: 200,
        };
      },
    });
  }),

  // http.get("/api/drivers", async ({ request, requestId }) => {
  //   console.log(`In drivers req ${requestId}:`, request);
  //   await delay(1000);
  //   return HttpResponse.json(drivers);
  // })
);
