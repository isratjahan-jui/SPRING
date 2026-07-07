import { User } from './user.model';

export type { User };

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  phone: string;
  role: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
  userId: number;
  name: string;
  email: string;
  phone: string;
  role: string;
  hotelId?: number;
  hotelName?: string;
  ownerId?: number;
  ownerName?: string;
}
