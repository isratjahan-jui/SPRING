export interface User {
  id: number;
  name: string;
  email: string;
  phone?: string;
  password?: string;
  image?: string;
  role: string;
  active: boolean;
}
