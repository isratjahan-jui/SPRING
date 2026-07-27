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
  commissionId?: number;
  hotelName?: string;
  customerName?: string;
  roomType?: string;
  bookingStatus?: string;
  issuedAt: string;
  createdAt: string;
  updatedAt: string;
}
