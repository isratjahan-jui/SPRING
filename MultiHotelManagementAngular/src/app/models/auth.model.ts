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
 
  role: 'ADMIN' | 'HOTEL_OWNER' | 'CUSTOMER';
  hotelId?: number;
  hotelName?: string;
  ownerId?: number;
  ownerName?: string;
}


export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
}