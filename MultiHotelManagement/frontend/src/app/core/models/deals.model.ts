import { DealType } from './enums.model';

export interface DealsRequest {
  dealTitle: string;
  description: string;
  discountPercent: number;
  discountAmount: number;
  startDate: string;
  endDate: string;
  hotelId: number;
  roomId?: number;
  dealType: DealType;
}

export interface DealsResponse {
  id: number;
  dealTitle: string;
  description: string;
  discountPercent: number;
  discountAmount: number;
  startDate: string;
  endDate: string;
  dealType: DealType;
  hotelName: string;
  roomType: string;
  isActive: boolean;
}
