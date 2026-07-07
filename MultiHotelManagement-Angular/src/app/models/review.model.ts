export interface Review {
  id: number;
  rating: number;
  comment: string;
  customerName: string;
  hotelName: string;
}

export interface ReviewRequest {
  rating: number;
  comment: string;
  hotelId: number;
  customerId: number;
}
