export interface FacilityRequest {
  hotelId: number;
  facilityName: string;
  description: string;
}

export interface FacilityResponse {
  id: number;
  facilityName: string;
  description: string;
  hotelId: number;
  hotelName: string;
  createdAt: string;
  updatedAt: string;
}
