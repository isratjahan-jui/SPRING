export interface CouponRequest {
  code: string;
  discountPercent: number;
  discountAmount: number;
  validFrom: string;
  validUntil: string;
  usageLimit?: number | null;
  hotelId: number;
}

export interface CouponResponse {
  id: number;
  code: string;
  discountPercent: number;
  discountAmount: number;
  validFrom: string;
  validUntil: string;
  usageLimit?: number | null;
  usedCount?: number | null;
  hotelId: number;
  hotelName: string;
  active: boolean;
}
