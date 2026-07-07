export interface Payment {
  id: number;
  method: string;
  amount: number;
  status: string;
  bookingId: number;
}
