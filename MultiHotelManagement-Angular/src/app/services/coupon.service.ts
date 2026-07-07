import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../constants';
import { Coupon, CouponRequest } from '../models/coupon.model';

@Injectable({ providedIn: 'root' })
export class CouponService {
  constructor(private http: HttpClient) {}

  create(data: CouponRequest) {
    return this.http.post<Coupon>(`${API_URL}/coupons`, data);
  }

  deactivate(id: number) {
    return this.http.put<void>(`${API_URL}/coupons/${id}/deactivate`, {});
  }

  getByCode(code: string) {
    return this.http.get<Coupon>(`${API_URL}/coupons/code/${code}`);
  }

  getByHotel(hotelId: number) {
    return this.http.get<Coupon[]>(`${API_URL}/coupons/hotel/${hotelId}`);
  }
}
