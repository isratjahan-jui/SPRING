import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../constants';
import { Invoice } from '../models/invoice.model';

export interface InvoiceRequest {
  bookingId: number;
  paymentId: number;
  customerId: number;
  commissionId?: number;
  totalAmount: number;
  taxAmount: number;
  discountAmount: number;
}

@Injectable({ providedIn: 'root' })
export class InvoiceService {
  constructor(private http: HttpClient) {}

  create(data: InvoiceRequest) {
    return this.http.post<Invoice>(`${API_URL}/invoices`, data);
  }

  getById(id: number) {
    return this.http.get<Invoice>(`${API_URL}/invoices/${id}`);
  }

  getByCustomer(customerId: number) {
    return this.http.get<Invoice[]>(`${API_URL}/invoices/customer/${customerId}`);
  }

  getAll() {
    return this.http.get<Invoice[]>(`${API_URL}/invoices`);
  }

  delete(id: number) {
    return this.http.delete<string>(`${API_URL}/invoices/${id}`);
  }
}
