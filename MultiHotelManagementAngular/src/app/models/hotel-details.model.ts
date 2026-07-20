export interface HotelDetails {
  id: number;
  hotelId: number;
  hotelName: string;
  ownerSpeach: string;
  description: string;
  hotelPolicy: string;
  pricePerNight: number;
  contactEmail: string;
  contactPhone: string;
  checkInTime: string;
  checkOutTime: string;
  cancellationPolicy: string;
  petPolicy: string;
  smokingPolicy: string;
  childPolicy: string;
  languages: string;
  nearbyAttractions: string;
  paymentOption: string;
  depositPercentage: number;
  preAuthRequired: boolean;
  cancellationDepositRefundable: string;
}
export interface HotelDetailsRequest {
  hotelId: number;
  ownerSpeach: string;
  description: string;
  hotelPolicy: string;
  pricePerNight: number;
  checkInTime: string;
  checkOutTime: string;
  contactEmail: string;
  contactPhone: string;
  cancellationPolicy: string;
  petPolicy: string;
  smokingPolicy: string;
  childPolicy: string;
  languages: string;
  nearbyAttractions: string;
  paymentOption: string;
  depositPercentage: number;
  preAuthRequired: boolean;
  cancellationDepositRefundable: string;
}
