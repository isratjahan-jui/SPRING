export interface ReviewRequest {
  rating: number;
  comment: string;
  hotelId: number;
  customerId: number;
}

export interface ReviewResponse {
  id: number;
  rating: number;
  comment: string;
  customerName: string;
  hotelName: string;
  createdAt: string;
  updatedAt: string;
}
