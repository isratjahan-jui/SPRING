export interface WishlistRequest {
  userId: number;
  customerId: number;
  hotelId: number;
  notes?: string;
}

export interface WishlistResponse {
  id: number;
  userId: number;
  userName: string;
  customerId: number;
  customerName: string;
  hotelId: number;
  hotelName: string;
  hotelImage: string;
  hotelAddress: string;
  notes: string;
  isActive: boolean;
}
