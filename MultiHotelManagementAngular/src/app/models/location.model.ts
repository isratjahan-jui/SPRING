export interface Location {
  id: number;
  locationName: string;
  locationImage?: string;
  city: string;
  district?: string;
  division?: string;
  upazila?: string;
  totalHotels?: number;
  createdAt?: string;
}

export interface PaginatedLocations {
  content: Location[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}
