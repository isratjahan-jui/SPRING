export interface PaymentRequest {
  method: string;
  amount: number;
  status: string;
  bookingId: number;
  extraServiceId?: number;
}

export interface PaymentResponse {
  id: number;
  method: string;
  amount: number;
  status: string;
  bookingId: number;
  bookingReference: string;
  extraServiceId: number;
  serviceType: string;
}
