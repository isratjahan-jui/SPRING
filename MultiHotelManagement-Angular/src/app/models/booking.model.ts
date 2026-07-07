export interface Booking {
  id: number;
  contractPersonName: string;
  phone: string;
  bookingDate: string;
  checkInDate: string;
  checkOutDate: string;
  totalGuests: number;
  totalPrice: number;
  numberOfRooms: number;
  discountRate: number;
  totalAmount: number;
  advanceAmount: number;
  dueAmount: number;
  status: string;
  customerId: number;
  hotelId: number;
  roomId: number;
}

export interface BookingRequest {
  contractPersonName: string;
  phone: string;
  checkInDate: string;
  checkOutDate: string;
  totalGuests: number;
  numberOfRooms: number;
  customerId: number;
  hotelId: number;
  roomId: number;
}
