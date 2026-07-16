import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CouponRequest, CouponResponse } from '../models/coupon.model';
import { environment } from '../../environments/environments';

@Injectable({ providedIn: 'root' })
export class CouponService {
  private API_URL = environment.apiUrl + 'coupons';

  constructor(private http: HttpClient) {}

  getByHotel(hotelId: number) {
    return this.http.get<CouponResponse[]>(`${this.API_URL}/hotel/${hotelId}`);
  }

  create(data: CouponRequest) {
    return this.http.post<CouponResponse>(this.API_URL, data);
  }

  deactivate(id: number) {
    return this.http.put(`${this.API_URL}/${id}/deactivate`, {});
  }

  getByCode(code: string) {
    return this.http.get<CouponResponse>(`${this.API_URL}/code/${code}`);
  }
}
