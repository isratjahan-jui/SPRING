export interface InvoiceRequest {
  bookingId: number;
  paymentId: number;
  customerId: number;
  commissionId: number;
  totalAmount: number;
  taxAmount: number;
  discountAmount: number;
}

export interface InvoiceResponse {
  id: number;
  invoiceNumber: string;
  totalAmount: number;
  taxAmount: number;
  discountAmount: number;
  netAmount: number;
  status: string;
  bookingId: number;
  paymentId: number;
  customerId: number;
  commissionId: number;
  issuedAt: string;
  createdAt: string;
  updatedAt: string;
}
