export interface Customer {

  // Response Fields
  id?: number;
  userId?: number;

  // User Information
  name: string;
  email: string;
  phone: string;
  password?: string;
  role?: string;

  // Customer Profile
  customerName: string;
  address: string;
  gender: string;
  dateOfBirth: string;
  image?: string;
}