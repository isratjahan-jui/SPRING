export interface HotelOwnerRequest {
  name: string;
  email: string;
  phone: string;
  address: string;
  gender: string;
  dateOfBirth: string;
  image?: string;
  userId: number;
}

export interface HotelOwnerResponse {
  id: number;
  name: string;
  email: string;
  phone: string;
  address: string;
  gender: string;
  dateOfBirth: string;
  image: string;
  userId: number;
  createdAt: string;
  updatedAt: string;
}
