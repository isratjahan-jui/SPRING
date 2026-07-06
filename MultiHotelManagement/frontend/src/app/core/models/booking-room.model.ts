export interface BookingRoomRequest {
  bookingId: number;
  roomId: number;
  numberOfRooms: number;
  adults: number;
  children: number;
}

export interface BookingRoomResponse {
  id: number;
  numberOfRooms: number;
  adults: number;
  children: number;
  price: number;
  bookingId: number;
  bookingStatus: string;
  checkIn: string;
  checkOut: string;
  roomId: number;
  roomType: string;
  roomPrice: number;
  amenities: string;
  hotelId: number;
  hotelName: string;
  customerId: number;
  customerName: string;
}
