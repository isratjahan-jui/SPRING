export interface Hotel {
  id: number;
  hotelName: string;
  address: string;
  description: string;
  rating: string;
  image: string;
  status: string;
  locationId: number;
  ownerId: number;
  locationName?: string;
  city?: string;
  ownerName?: string;
}

export interface HotelRequest {
  hotelName: string;
  address: string;
  description: string;
  rating: string;
  status: string;
  locationId: number;
  ownerId: number;
}
