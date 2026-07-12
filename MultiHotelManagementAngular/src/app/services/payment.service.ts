import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PaymentRequest, PaymentResponse } from '../models/payment.model';
import { environment } from '../../environments/environments';

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private API_URL = environment.apiUrl + 'payments';

  constructor(private http: HttpClient) {}

  create(data: PaymentRequest) {
    return this.http.post<PaymentResponse>(this.API_URL, data);
  }

  update(id: number, data: PaymentRequest) {
    return this.http.put<PaymentResponse>(`${this.API_URL}/${id}`, data);
  }

  getAll() {
    return this.http.get<PaymentResponse[]>(this.API_URL);
  }

  getById(id: number) {
    return this.http.get<PaymentResponse>(`${this.API_URL}/${id}`);
  }

  getByBooking(bookingId: number) {
    return this.http.get<PaymentResponse>(`${this.API_URL}/booking/${bookingId}`);
  }

  getByCustomer(customerId: number) {
    return this.http.get<PaymentResponse[]>(`${this.API_URL}/customer/${customerId}`);
  }

  refund(bookingId: number) {
    return this.http.post<PaymentResponse>(`${this.API_URL}/${bookingId}/refund`, {});
  }

  delete(id: number) {
    return this.http.delete(`${this.API_URL}/${id}`);
  }
}
