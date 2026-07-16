export interface Booking {
  id: number;
  hotelId: number;
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
  onlineCheckIn: boolean;
  idImagePath?: string;
  digitalKey?: string;
  cancellationDeadline?: string;
  cancellationPolicyText?: string;
  extraCharges: number;
  phone?: string;
  foodItems?: string[];
  extraServices?: { id: number; serviceType: string; price: number; serviceStatus: string }[];
}

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
  foodItemIds?: number[];
  extraServiceIds?: number[];
}
