export interface Facility {
  id: number;
  facilityName: string;
  description: string;
  hotelId: number;
  hotelName?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface FacilityRequest {
  facilityName: string;
  description: string;
  hotelId: number;
}
