export interface User {
  id: number;
  email: string;
  password?: string;
  role: string;
  enabled: boolean;
  createdAt?: string;
  updatedAt?: string;
}
