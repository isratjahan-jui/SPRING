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
  notes: string;
  isActive: boolean;
}
