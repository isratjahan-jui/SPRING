export interface ExtraServiceRequest {
  serviceType: string;
  price: number;
  serviceStatus: string;
  bookingId: number;
}

export interface ExtraServiceResponse {
  id: number;
  serviceType: string;
  price: number;
  serviceStatus: string;
  bookingId: number;
  bookingReference: string;
}
