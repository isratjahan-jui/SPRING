import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../constants';
import { Payment } from '../models/payment.model';

export interface PaymentRequest {
  method: string;
  amount: number;
  status: string;
  bookingId: number;
  extraServiceId?: number;
}

@Injectable({ providedIn: 'root' })
export class PaymentService {
  constructor(private http: HttpClient) {}

  create(data: PaymentRequest) {
    return this.http.post<Payment>(`${API_URL}/payments`, data);
  }

  update(id: number, data: PaymentRequest) {
    return this.http.put<Payment>(`${API_URL}/payments/${id}`, data);
  }

  getById(id: number) {
    return this.http.get<Payment>(`${API_URL}/payments/${id}`);
  }

  getByBooking(bookingId: number) {
    return this.http.get<Payment>(`${API_URL}/payments/booking/${bookingId}`);
  }

  delete(id: number) {
    return this.http.delete<void>(`${API_URL}/payments/${id}`);
  }
}
