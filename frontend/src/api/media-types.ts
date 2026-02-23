type Version = "v1";

export function createMediaType(version: Version) {
  return {
    clientAll: `application/vnd.client.${version}+json`,
    clientCreate: `application/vnd.clientCreate.${version}+json`,
    clientPublic: `application/vnd.publicClient.${version}+json`,
    clientUpdate: `application/vnd.clientUpdate.${version}+json`,
    driverAll: `application/vnd.driver.${version}+json`,
    driverList: `application/vnd.driverList.${version}+json`,
    driverCreate: `application/vnd.driverCreate.${version}+json`,
    driverPublic: `application/vnd.publicDriver.${version}+json`,
    driverUpdate: `application/vnd.driverUpdate.${version}+json`,
      vehicle: `application/vnd.vehicle.${version}+json`,
      availability: `application/vnd.availability.${version}+json`,
      zones: `application/vnd.zoneList.${version}+json`,
      zone: `application/vnd.zone.${version}+json`,
    bookingReview: `application/vnd.bookingReview.${version}+json`,
    messageList: `application/vnd.messageList.${version}+json`,
    messageCreate: `application/vnd.messageCreate.${version}+json`,
    message: `application/vnd.message.${version}+json`,
  } as const;
}
