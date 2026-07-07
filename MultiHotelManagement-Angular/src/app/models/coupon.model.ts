export interface Coupon {
  id: number;
  code: string;
  discountPercent: number;
  discountAmount: number;
  validFrom: string;
  validUntil: string;
  hotelName: string;
  active: boolean;
}

export interface CouponRequest {
  code: string;
  discountPercent: number;
  discountAmount: number;
  validFrom: string;
  validUntil: string;
  hotelId: number;
}
