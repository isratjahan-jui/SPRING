export interface PaymentRequest {
  method: string;
  amount: number;
  status: string;
  bookingId: number;
  customerId?: number;
  extraServiceId?: number;
}

export interface PaymentResponse {
  id: number;
  method: string;
  amount: number;
  status: string;
  bookingId: number;
  bookingReference: string;
  customerId: number;
  customerName: string;
  extraServiceId?: number;
  serviceType?: string;
  transactionDate: string;
}
