import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../constants';
import { Customer } from '../models/customer.model';

@Injectable({ providedIn: 'root' })
export class CustomerService {
  constructor(private http: HttpClient) {}

  getByUserId(userId: number) {
    return this.http.get<Customer>(`${API_URL}/customers/user/${userId}`);
  }

  update(id: number, formData: FormData) {
    return this.http.put<Customer>(`${API_URL}/customers/${id}`, formData);
  }
}
