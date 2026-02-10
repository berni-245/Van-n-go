type Version = "v1";

export function createMediaType(version: Version) {
  return {
    clientAll: `application/vnd.client.${version}+json`,
    clientPublic: `application/vnd.publicClient.${version}+json`,
    clientUpdate: `application/vnd.clientUpdate.${version}+json`,
    driverAll: `application/vnd.driver.${version}+json`,
    driverPublic: `application/vnd.publicDriver.${version}+json`,
    driverUpdate: `application/vnd.driverUpdate.${version}+json`,
  } as const;
}
