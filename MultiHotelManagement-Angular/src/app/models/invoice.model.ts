export interface Invoice {
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
}
