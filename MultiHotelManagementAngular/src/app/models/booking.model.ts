export interface Booking {
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
  foodItems?: string[];
}
