export interface CouponRequest {
  code: string;
  discountPercent: number;
  discountAmount: number;
  validFrom: string;
  validUntil: string;
  hotelId: number;
}

export interface CouponResponse {
  id: number;
  code: string;
  discountPercent: number;
  discountAmount: number;
  validFrom: string;
  validUntil: string;
  hotelName: string;
  active: boolean;
}
