import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { API_URL } from '../constants';
import { Commission } from '../models/commission.model';

export interface CommissionRequest {
  bookingId: number;
  paymentId: number;
  extraServiceId?: number;
  commissionRate: number;
  adminEarnings: number;
  hotelOwnerEarnings: number;
}

@Injectable({ providedIn: 'root' })
export class CommissionService {
  constructor(private http: HttpClient) {}

  create(data: CommissionRequest) {
    return this.http.post<Commission>(`${API_URL}/commissions`, data);
  }

  update(id: number, data: CommissionRequest) {
    return this.http.put<Commission>(`${API_URL}/commissions/${id}`, data);
  }

  delete(id: number) {
    return this.http.delete<void>(`${API_URL}/commissions/${id}`);
  }

  getAll() {
    return this.http.get<Commission[]>(`${API_URL}/commissions`);
  }

  getById(id: number) {
    return this.http.get<Commission>(`${API_URL}/commissions/${id}`);
  }

  getByBooking(bookingId: number) {
    return this.http.get<Commission>(`${API_URL}/commissions/booking/${bookingId}`);
  }

  existsByBooking(bookingId: number) {
    return this.http.get<boolean>(`${API_URL}/commissions/booking/${bookingId}/exists`);
  }

  getByPayment(paymentId: number) {
    return this.http.get<Commission>(`${API_URL}/commissions/payment/${paymentId}`);
  }

  getByExtraService(extraServiceId: number) {
    return this.http.get<Commission>(`${API_URL}/commissions/extra/${extraServiceId}`);
  }

  getByOwner(ownerId: number) {
    return this.http.get<Commission[]>(`${API_URL}/commissions/owner/${ownerId}`);
  }

  getByHotel(hotelId: number) {
    return this.http.get<Commission[]>(`${API_URL}/commissions/hotel/${hotelId}`);
  }

  getByRate(rate: number) {
    return this.http.get<Commission[]>(`${API_URL}/commissions/rate/${rate}`);
  }

  getAdminTotal() {
    return this.http.get<number>(`${API_URL}/commissions/admin/total`);
  }

  getOwnerTotal(ownerId: number) {
    return this.http.get<number>(`${API_URL}/commissions/owner/${ownerId}/total`);
  }

  getByDateRange(start: string, end: string) {
    const params = new HttpParams().set('start', start).set('end', end);
    return this.http.get<Commission[]>(`${API_URL}/commissions/range`, { params });
  }

  getMonthlyReport(year: number) {
    const params = new HttpParams().set('year', year);
    return this.http.get<any[]>(`${API_URL}/commissions/report/monthly`, { params });
  }
}
