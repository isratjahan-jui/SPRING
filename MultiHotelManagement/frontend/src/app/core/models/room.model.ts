export interface RoomRequest {
  hotelId: number;
  roomType: string;
  description: string;
  amenities: string;
  price: number;
  totalRooms: number;
  availableRooms: number;
  bookedRooms: number;
  adults: number;
  children: number;
  isAvailable: boolean;
}

export interface RoomResponse {
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
  hotelName: string;
  createdAt: string;
  updatedAt: string;
}
