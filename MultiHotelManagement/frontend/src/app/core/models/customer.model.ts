export interface CustomerRequest {
  name: string;
  email: string;
  phone: string;
  password: string;
  customerName: string;
  address: string;
  gender: string;
  dateOfBirth: string;
}

export interface CustomerResponse {
  id: number;
  userId: number;
  name: string;
  email: string;
  phone: string;
  role: string;
  customerName: string;
  address: string;
  gender: string;
  dateOfBirth: string;
  image: string;
}
