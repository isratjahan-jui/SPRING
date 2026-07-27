export interface ReviewRequest {
  rating: number;
  comment: string;
  hotelId: number;
  customerId: number;
  bookingId: number;
}

export interface ReviewResponse {
  id: number;
  rating: number;
  comment: string;
  customerId: number;
  hotelId: number;
  bookingId: number;
  customerName: string;
  hotelName: string;
  hotelAddress?: string;
  roomType?: string;
  bookingStatus?: string;
  status: string;
  editCount: number;
  editedAt?: string;
  createdAt: string;
  updatedAt: string;
}
