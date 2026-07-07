export interface Hotel {
  id: number;
  hotelName: string;
  address: string;
  description: string;
  pricePerNight: number;
  rating: string;
  image: string;
  status: string;
  foodAvailable: boolean;
  foodServiceHours: string;
  locationId: number;
  ownerId: number;
  locationName?: string;
  city?: string;
}

export interface HotelRequest {
  hotelName: string;
  address: string;
  description: string;
  pricePerNight: number;
  rating: string;
  foodAvailable: boolean;
  foodServiceHours: string;
  locationId: number;
}
