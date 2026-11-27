export type SizeUnit = "B" | "KB" | "MB" | "GB" | "TB";

export const SizeMultiplier: Record<SizeUnit, number> = {
  B: 1,
  KB: 2 ** 10,
  MB: 2 ** 20,
  GB: 2 ** 30,
  TB: 2 ** 40,
} as const;

// `mimeType` is a full MIME type (e.g. "image/png"); `allowedTypes` are subtype names
// (e.g. "png", "jpeg"), matched case-insensitively -- mirroring the drag-and-drop upload's
// accepted-extensions list.
export function isAllowedFileType(mimeType: string, allowedTypes: string[]): boolean {
  const subtype = mimeType.split("/").pop()!.toLowerCase();
  return allowedTypes.map((t) => t.toLowerCase()).includes(subtype);
}

export function isWithinSizeLimit(sizeBytes: number, maxSize: number, maxSizeUnit: SizeUnit): boolean {
  return sizeBytes <= maxSize * SizeMultiplier[maxSizeUnit];
}
