import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Receipt } from '../models';

@Injectable({ providedIn: 'root' })
export class ReceiptService {
  private baseUrl = 'http://localhost:8080/api/receipts';

  constructor(private http: HttpClient) {}

  generateReceipt(paymentId: number): Observable<Receipt> {
    return this.http.post<Receipt>(`${this.baseUrl}/generate/${paymentId}`, {});
  }

  getById(id: number): Observable<Receipt> {
    return this.http.get<Receipt>(`${this.baseUrl}/${id}`);
  }

  getByNumber(receiptNumber: string): Observable<Receipt> {
    return this.http.get<Receipt>(`${this.baseUrl}/number/${receiptNumber}`);
  }

  getByPaymentId(paymentId: number): Observable<Receipt> {
    return this.http.get<Receipt>(`${this.baseUrl}/payment/${paymentId}`);
  }

  getByCustomerId(customerId: number): Observable<Receipt[]> {
    return this.http.get<Receipt[]>(`${this.baseUrl}/customer/${customerId}`);
  }

  getByHotelId(hotelId: number): Observable<Receipt[]> {
    return this.http.get<Receipt[]>(`${this.baseUrl}/hotel/${hotelId}`);
  }

  getByOwnerId(ownerId: number): Observable<Receipt[]> {
    return this.http.get<Receipt[]>(`${this.baseUrl}/owner/${ownerId}`);
  }
}
