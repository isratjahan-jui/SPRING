export interface DealRequest {
  dealTitle: string;
  description: string;
  discountPercent: number;
  discountAmount: number;
  startDate: string;
  endDate: string;
  hotelId: number;
  roomId?: number;
  dealType: 'PERCENTAGE' | 'FIXED_AMOUNT' | 'SEASONAL';
}

export interface DealResponse {
  id: number;
  dealTitle: string;
  description: string;
  discountPercent: number;
  discountAmount: number;
  startDate: string;
  endDate: string;
  dealType: 'PERCENTAGE' | 'FIXED_AMOUNT' | 'SEASONAL';
  hotelName: string;
  roomType: string;
  isActive: boolean;
}
