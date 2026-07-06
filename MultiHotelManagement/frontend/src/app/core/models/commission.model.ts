export interface CommissionRequest {
  bookingId?: number;
  paymentId?: number;
  extraServiceId?: number;
  commissionRate: number;
  adminEarnings: number;
  hotelOwnerEarnings: number;
}

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
  extraServiceId: number;
  serviceType: string;
  extraServicePrice: number;
  createdBy: string;
  updatedBy: string;
  createdAt: string;
  updatedAt: string;
}
