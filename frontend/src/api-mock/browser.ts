// 1. Import the library.
import { delay, http, HttpResponse } from "msw";
import { setupWorker } from "msw/browser";

// 2. Describe network behavior with request handlers.
export const worker = setupWorker(
  http.get("/api/users", () => {
    return HttpResponse.json(
      {
        message: "Mocked response",
      },
      {
        status: 202,
        statusText: "Mocked status",
      }
    );
  }),

  http.get("/api/drivers", async () => {
    await delay(1000);
    return HttpResponse.json(
      [
        { id: 1, name: "Jorge" },
        { id: 2, name: "Pedro" },
      ],
      {
        status: 200,
        statusText: "OK",
      }
    );
  })
);
