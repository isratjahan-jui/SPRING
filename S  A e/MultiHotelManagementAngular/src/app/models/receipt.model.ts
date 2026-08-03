export interface ReceiptResponse {
  id: number;
  receiptNumber: string;
  paymentId: number;
  paymentMethod: string;
  invoiceId: number;
  invoiceNumber: string;
  bookingId: number;
  bookingReference: string;
  customerId: number;
  customerName: string;
  customerEmail: string;
  amount: number;
  taxAmount: number;
  totalAmount: number;
  transactionId: string;
  issuedAt: string;
  createdAt: string;
}
