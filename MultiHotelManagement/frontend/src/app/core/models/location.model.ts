export interface LocationRequest {
  locationName: string;
  city: string;
}

export interface HotelBasicInfo {
  id: number;
  name: string;
  pricePerNight: number;
  rating: string;
  status: string;
}

export interface LocationResponse {
  id: number;
  locationName: string;
  locationImage: string;
  city: string;
  totalHotels: number;
  hotels: HotelBasicInfo[];
}
