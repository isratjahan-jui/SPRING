import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Customer } from '../models/customer.model';
import { environment } from '../../environments/environments';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {


   private apiUrl = environment.apiUrl + 'customers';

  constructor(private http: HttpClient) {}

  // ================= Register Customer =================
  register(customer: Customer, image?: File): Observable<Customer> {

    const formData = new FormData();

    formData.append(
      'data',
      new Blob(
        [JSON.stringify(customer)],
        { type: 'application/json' }
      )
    );

    if (image) {
      formData.append('image', image);
    }

    return this.http.post<Customer>(this.apiUrl, formData);
  }

  // ================= Get All =================
  getAllCustomers(): Observable<Customer[]> {
    return this.http.get<Customer[]>(this.apiUrl);
  }

  // ================= Get By Id =================
  getCustomerById(id: number): Observable<Customer> {
    return this.http.get<Customer>(`${this.apiUrl}/${id}`);
  }

  // ================= Get By User Id =================
  getCustomerByUserId(userId: number): Observable<Customer> {
    return this.http.get<Customer>(`${this.apiUrl}/user/${userId}`);
  }

  // ================= Update =================
  updateCustomer(
    id: number,
    customer: Customer,
    image?: File
  ): Observable<Customer> {

    const formData = new FormData();

    formData.append(
      'data',
      new Blob(
        [JSON.stringify(customer)],
        { type: 'application/json' }
      )
    );

    if (image) {
      formData.append('image', image);
    }

    return this.http.put<Customer>(
      `${this.apiUrl}/${id}`,
      formData
    );
  }

  // ================= Delete =================
  deleteCustomer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

}