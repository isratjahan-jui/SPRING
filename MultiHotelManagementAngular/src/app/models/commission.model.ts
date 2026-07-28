export interface CommissionResponse {
  id: number;
  commissionRate: number;
  adminEarnings: number;
  hotelOwnerEarnings: number;
  commissionStatus: string;
  bookingId: number;
  bookingReference: string;
  bookingTotalPrice: number;
  bookingStatus: string;
  hotelId: number;
  hotelName: string;
  ownerId: number;
  ownerName: string;
  customerName: string;
  paymentId: number;
  paymentMethod: string;
  paymentStatus: string;
  createdAt: string;
}
