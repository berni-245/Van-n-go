export interface ApiError {
  type: string;
  description: string;
}

type Language = "english" | "spanish";

export interface Client {
  id: number;
  username: string;
  email: string;
  preferredLanguage: Language;
  self: string;
  profilePic: string;
  zone: string;
  pendingBookings: string;
  acceptedBookings: string;
  finishedBookings: string;
  rejectedBookings: string;
  canceledBookings: string;
}

export interface Driver {
  id: number;
  username: string;
  email: string;
  preferredLanguage: Language;
  description: string;
  rating: number;
  cbu: string;
  self: string;
  profilePic: string;
  pendingBookings: string;
  acceptedBookings: string;
  finishedBookings: string;
  rejectedBookings: string;
  canceledBookings: string;
  vehicles: string;
}
