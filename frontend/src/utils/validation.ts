// Vehicle plate: old format (3 letters + 3 digits, e.g. "ABC123") or new
// Mercosur format (2 letters + 3 digits + 2 letters, e.g. "AB123CD").
const PLATE_REGEX = /^([a-zA-Z]{3}[0-9]{3}|[a-zA-Z]{2}[0-9]{3}[a-zA-Z]{2})$/;

export function isValidPlateNumber(plateNumber: string): boolean {
  return PLATE_REGEX.test(plateNumber);
}

export function isValidVehicleVolume(volumeM3: number): boolean {
  return !Number.isNaN(volumeM3) && volumeM3 >= 1 && volumeM3 <= 100;
}

export function isValidHourlyRate(hourlyRate: number): boolean {
  return !Number.isNaN(hourlyRate) && hourlyRate >= 100 && hourlyRate <= 100000;
}

export const MAX_VEHICLE_DESCRIPTION_LENGTH = 255;

export function isValidVehicleDescriptionLength(description: string): boolean {
  return description.length <= MAX_VEHICLE_DESCRIPTION_LENGTH;
}

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export function isValidEmail(email: string): boolean {
  return EMAIL_REGEX.test(email);
}
