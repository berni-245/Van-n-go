import { expect, test } from "vitest";
import { createMediaType } from "@/api/media-types";

test("templates the version into each vnd.* media type", () => {
  const mediaTypes = createMediaType("v1");
  expect(mediaTypes.clientAll).toBe("application/vnd.client.v1+json");
  expect(mediaTypes.driverList).toBe("application/vnd.driverList.v1+json");
  expect(mediaTypes.bookingReview).toBe("application/vnd.bookingReview.v1+json");
  expect(mediaTypes.message).toBe("application/vnd.message.v1+json");
  expect(mediaTypes.recovery).toBe("application/vnd.recovery.v1+json");
});

test("actually uses the version argument for templated entries, not a hardcoded 'v1' (documents the templating is real)", () => {
  // Version is currently typed as the literal "v1", so this cast is needed
  // to exercise the templating logic itself independent of what versions
  // the type system happens to allow today.
  const mediaTypes = createMediaType("v2" as unknown as "v1");
  expect(mediaTypes.clientAll).toBe("application/vnd.client.v2+json");
  expect(mediaTypes.zone).toBe("application/vnd.zone.v2+json");
});

test("four entries are hardcoded to v1 regardless of the version argument (documents current behavior)", () => {
  // vehicleUpdate/availabilityUpdate/vehicleCreate/bookingCreate are written
  // as literal strings in media-types.ts instead of using the `version`
  // template, unlike every other entry. If that's unintentional, this test
  // should start failing the moment someone templates them too -- which is
  // exactly the signal to update it (and drop this test).
  const mediaTypes = createMediaType("v2" as unknown as "v1");
  expect(mediaTypes.vehicleUpdate).toBe("application/vnd.vehicleUpdate.v1+json");
  expect(mediaTypes.availabilityUpdate).toBe("application/vnd.availabilityUpdate.v1+json");
  expect(mediaTypes.vehicleCreate).toBe("application/vnd.vehicleCreate.v1+json");
  expect(mediaTypes.bookingCreate).toBe("application/vnd.bookingCreate.v1+json");
});

test("returns a stable, fully-enumerated set of media type keys", () => {
  const mediaTypes = createMediaType("v1");
  expect(Object.keys(mediaTypes).sort()).toEqual(
    [
      "clientAll",
      "clientCreate",
      "clientPublic",
      "clientUpdate",
      "driverAll",
      "driverList",
      "driverCreate",
      "driverPublic",
      "driverUpdate",
      "vehicle",
      "availability",
      "zones",
      "zone",
      "bookingReview",
      "bookingUpdateState",
      "messageList",
      "messageCreate",
      "message",
      "vehicleUpdate",
      "availabilityUpdate",
      "vehicleCreate",
      "bookingCreate",
      "recovery",
      "passwordToken",
      "passwordChange",
    ].sort()
  );
});
