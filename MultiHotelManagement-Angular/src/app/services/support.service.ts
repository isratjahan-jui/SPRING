import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../constants';
import { SupportTicket } from '../models/support.model';

@Injectable({ providedIn: 'root' })
export class SupportService {
  constructor(private http: HttpClient) {}

  getByCustomer(customerId: number) {
    return this.http.get<SupportTicket[]>(`${API_URL}/support/customer/${customerId}`);
  }

  create(data: { subject: string; description: string; customerId: number }) {
    return this.http.post<SupportTicket>(`${API_URL}/support`, data);
  }

  close(id: number) {
    return this.http.put<void>(`${API_URL}/support/${id}/close`, {});
  }
}
