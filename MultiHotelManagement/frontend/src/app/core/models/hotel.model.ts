export interface HotelRequest {
  hotelName: string;
  address: string;
  description: string;
  rating: string;
  image: string;
  status: string;
  locationId: number;
  ownerId: number;
}

export interface HotelResponse {
  id: number;
  hotelName: string;
  address: string;
  description: string;
  rating: string;
  image: string;
  status: string;
  locationId: number;
  ownerId: number;
  locationName: string;
  ownerName: string;
}
