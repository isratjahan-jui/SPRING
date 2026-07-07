export interface Room {
  id: number;
  roomType: string;
  image: string;
  totalRooms: number;
  adults: number;
  children: number;
  pricePerNight: number;
  availableRooms: number;
  bookedRooms: number;
  amenities: string;
  description: string;
  isAvailable: boolean;
  hotelId: number;
}

export interface Facility {
  id: number;
  name: string;
  description: string;
  icon: string;
  hotelId: number;
}
