export interface BookingRequest {
  customerId: number;
  hotelId: number;
  roomId: number;
  checkInDate: string;
  checkOutDate: string;
  numberOfRooms: number;
  totalGuests: number;
  discountRate: number;
  advanceAmount: number;
  foodItemIds: number[];
}

export interface BookingResponse {
  id: number;
  customerName: string;
  hotelName: string;
  roomType: string;
  checkInDate: string;
  checkOutDate: string;
  numberOfRooms: number;
  totalGuests: number;
  totalAmount: number;
  dueAmount: number;
  status: string;
  foodItems: string[];
}
