import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CustomerSupportRequest, CustomerSupportResponse } from '../models/customer-support.model';
import { environment } from '../../environments/environments';

@Injectable({ providedIn: 'root' })
export class CustomerSupportService {
  private API_URL = environment.apiUrl + 'support';

  constructor(private http: HttpClient) {}

  getByCustomer(customerId: number) {
    return this.http.get<CustomerSupportResponse[]>(`${this.API_URL}/customer/${customerId}`);
  }

  create(data: CustomerSupportRequest) {
    return this.http.post<CustomerSupportResponse>(this.API_URL, data);
  }

  update(id: number, data: CustomerSupportRequest) {
    return this.http.put<CustomerSupportResponse>(`${this.API_URL}/${id}`, data);
  }

  close(id: number) {
    return this.http.put(`${this.API_URL}/${id}/close`, {});
  }
}
