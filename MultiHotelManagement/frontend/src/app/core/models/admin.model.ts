export interface AdminRequest {
  userId?: number;
  name: string;
  email: string;
  phone: string;
  address: string;
  gender: string;
  dateOfBirth: string;
  image?: string;
}

export interface AdminResponse {
  id: number;
  name: string;
  email: string;
  phone: string;
  address: string;
  gender: string;
  dateOfBirth: string;
  image: string;
}
