export interface Room {
  id: number;
  roomType: string;
  description: string;
  amenities: string;
  image: string;
  price: number;
  totalRooms: number;
  availableRooms: number;
  bookedRooms: number;
  adults: number;
  children: number;
  isAvailable: boolean;
  hotelId: number;
  hotelName?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface RoomRequest {
  roomType: string;
  description: string;
  amenities: string;
  price: number;
  totalRooms: number;
  availableRooms: number;
  adults: number;
  children: number;
  hotelId: number;
}
