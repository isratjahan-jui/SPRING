export interface Invoice {
  id: number;
  invoiceNumber: string;
  totalAmount: number;
  taxAmount: number;
  discountAmount: number;
  netAmount: number;
  status: 'ISSUED' | 'PAID' | 'CANCELLED';
  bookingId: number;
  paymentId: number;
  customerId: number;
  commissionId: number;
  hotelName: string;
  customerName: string;
  roomType: string;
  bookingStatus: string;
  issuedAt: string;
  createdAt: string;
  updatedAt: string;
}

export interface Receipt {
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

export interface Payment {
  id: number;
  method: string;
  amount: number;
  status: string;
  bookingId: number;
  bookingReference: string;
  customerId: number;
  customerName: string;
  extraServiceId: number;
  serviceType: string;
  transactionDate: string;
}

export interface User {
  id: number;
  name: string;
  email: string;
  role: string;
}

export interface LoginResponseDTO {
  message: string;
  token: string;
  tokenType: string;
  userId: number;
  name: string;
  email: string;
  phone: string;
  role: string;
  hotelId: number;
  hotelName: string;
  ownerId: number;
  ownerName: string;
}
