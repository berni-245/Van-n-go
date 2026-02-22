import { delay, http, passthrough } from "msw";
import { setupWorker } from "msw/browser";
import { authorizedResponse, handleAuthorization, publicResponse } from "./auth";
import { getMockUser } from "./users";
import { VEHICLES } from "./vehicles";

const baseUrl = "http://localhost:8080/";
export const worker = setupWorker(
  http.get(
    `${baseUrl}/*`,
    async ({request}) => {
      console.log("In msw delay handler: ", request.url);
      await delay(2000);
      passthrough();
    }
  )
);

// export const worker = setupWorker(
//   http.all(
//     ({ request }) => {
//       const { pathname } = new URL(request.url);
//       return pathname.startsWith("/api");
//     },
//     ({ request, requestId }) => {
//       console.log(`In globe req ${requestId}:`, request);
//       handleAuthorization(request);
//     }
//   ),
//
//   // TODO: for testing, remove later
//   http.get("/api/auth-required", ({ request }) => {
//     return authorizedResponse({
//       request,
//       response: (_) => ({
//         body: {
//           message: "Mocked response",
//         },
//         status: 200,
//       }),
//     });
//   }),
//
//   http.get("/api", ({ request }) => {
//     return publicResponse({
//       request,
//       response: {
//         body: {
//           links: ["/api/drivers", "/api/drivers/{id}/vehicles", "..."],
//         },
//         status: 200,
//       },
//     });
//   }),
//
//   http.get<{ id: string }>("/api/clients/:id", async ({ request, params }) => {
//     await delay(500);
//     const id = parseInt(params.id);
//     return authorizedResponse({
//       request,
//       response: (payload) => {
//         const user = getMockUser(payload, { type: "client", id });
//         if (!user) {
//           return {
//             body: {
//               type: "unautrhorized",
//               description: "User unautrhorized for this resource",
//             },
//             status: 401,
//           };
//         }
//         return {
//           body: user,
//           status: 200,
//         };
//       },
//     });
//   }),
//
//   http.get<{ id: string }>("/api/drivers/:id", async ({ request, params }) => {
//     await delay(500);
//     const id = parseInt(params.id);
//     return authorizedResponse({
//       request,
//       response: (payload) => {
//         const user = getMockUser(payload, { type: "driver", id });
//         if (!user) {
//           return {
//             body: {
//               type: "unautrhorized",
//               description: "User unautrhorized for this resource",
//             },
//             status: 401,
//           };
//         }
//         return {
//           body: user,
//           status: 200,
//         };
//       },
//     });
//   }),
//
//   http.get<{ id: string }>("/api/drivers/:id/vehicles", async ({ request, params }) => {
//     await delay(500);
//     const id = parseInt(params.id);
//
//     const vehicles = VEHICLES.filter((v) => v.ownerId === id).map((v) => v.vehicle);
//
//     return publicResponse({
//       request,
//       response: {
//         body: vehicles,
//         status: 200,
//       },
//     });
//   }),
//
//   http.get<{ driverId: string; vehicleId: string }>("/api/drivers/:driverId/vehicles/:vehicleId/picture", async () => {
//     await delay(500);
//
//     const res = fetch(
//       "https://imgs.search.brave.com/X4PjJfhp27qepM18ze0ZcCHKKRaAD5FL9f2r9JNndRc/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9pLmVi/YXlpbWcuY29tL2lt/YWdlcy9nL0R3c0FB/ZVN3MzhOcFpWelov/cy1sNDAwLndlYnA"
//     );
//     return res;
//   })
//   // http.get("/api/drivers", async ({ request, requestId }) => {
//   //   console.log(`In drivers req ${requestId}:`, request);
//   //   await delay(1000);
//   //   return HttpResponse.json(drivers);
//   // })
// );
