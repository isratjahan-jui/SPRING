import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Invoice } from '../models';

@Injectable({ providedIn: 'root' })
export class InvoiceService {
  private baseUrl = 'http://localhost:8080/api/invoices';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Invoice[]> {
    return this.http.get<Invoice[]>(this.baseUrl);
  }

  getById(id: number): Observable<Invoice> {
    return this.http.get<Invoice>(`${this.baseUrl}/${id}`);
  }

  getByCustomer(customerId: number): Observable<Invoice[]> {
    return this.http.get<Invoice[]>(`${this.baseUrl}/customer/${customerId}`);
  }

  getByBooking(bookingId: number): Observable<Invoice[]> {
    return this.http.get<Invoice[]>(`${this.baseUrl}/booking/${bookingId}`);
  }

  getByHotelId(hotelId: number): Observable<Invoice[]> {
    return this.http.get<Invoice[]>(`${this.baseUrl}/hotel/${hotelId}`);
  }

  getByOwnerId(ownerId: number): Observable<Invoice[]> {
    return this.http.get<Invoice[]>(`${this.baseUrl}/owner/${ownerId}`);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
