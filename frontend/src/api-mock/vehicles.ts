import { Vehicle } from "@/api/types";

interface VehicleOwner {
  ownerId: number;
  vehicle: Vehicle;
}

export const VEHICLES: VehicleOwner[] = [
  {
    ownerId: 0,
    vehicle: {
      id: 0,
      plateNumber: "AA123AA",
      volumeM3: 150.3,
      hourlyRate: 150.4,
      description: "This is a vehicle",
      self: "/api/drivers/0/vehicles/0",
      picture: "/api/drivers/0/vehicles/0/picture",
      driver: "/api/drivers/0",
      availability: "/api/drivers/0/vehicles/0/availability",
      bookedTimes: "/api/drivers/0/vehicles/0/booked-times",
    },
  },
  {
    ownerId: 0,
    vehicle: {
      id: 1,
      plateNumber: "AA123AB",
      volumeM3: 50.3,
      hourlyRate: 250.4,
      description: "This is a vehicle",
      self: "/api/drivers/0/vehicles/1",
      picture: "/api/drivers/0/vehicles/1/picture",
      driver: "/api/drivers/0",
      availability: "/api/drivers/0/vehicles/1/availability",
      bookedTimes: "/api/drivers/0/vehicles/1/booked-times",
    },
  },
  {
    ownerId: 1,
    vehicle: {
      id: 2,
      plateNumber: "BB123AA",
      volumeM3: 50,
      hourlyRate: 50,
      description: "This is a vehicle",
      self: "/api/drivers/1/vehicles/2",
      picture: "/api/drivers/1/vehicles/2/picture",
      driver: "/api/drivers/1",
      availability: "/api/drivers/1/vehicles/2/availability",
      bookedTimes: "/api/drivers/1/vehicles/2/booked-times",
    },
  },
];
