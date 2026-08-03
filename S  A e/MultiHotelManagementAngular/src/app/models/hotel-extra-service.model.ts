export interface HotelExtraService {
  id: number;
  serviceName: string;
  description: string;
  price: number;
  isActive: boolean;
  hotelId: number;
  hotelName?: string;
}

export interface HotelExtraServiceRequest {
  serviceName: string;
  description: string;
  price: number;
  hotelId: number;
}
