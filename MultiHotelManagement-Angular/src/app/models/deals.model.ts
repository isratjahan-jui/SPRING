export interface Deals {
  id: number;
  dealTitle: string;
  description: string;
  discountPercent: number;
  discountAmount: number;
  startDate: string;
  endDate: string;
  dealType: string;
  hotelName: string;
  roomType: string;
  isActive: boolean;
}
