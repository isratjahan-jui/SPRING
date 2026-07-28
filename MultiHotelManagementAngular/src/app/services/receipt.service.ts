import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ReceiptResponse } from '../models/receipt.model';
import { environment } from '../../environments/environments';

@Injectable({ providedIn: 'root' })
export class ReceiptService {
  private API_URL = environment.apiUrl + 'receipts';

  constructor(private http: HttpClient) {}

  generateReceipt(paymentId: number) {
    return this.http.post<ReceiptResponse>(`${this.API_URL}/generate/${paymentId}`, {});
  }

  getById(id: number) {
    return this.http.get<ReceiptResponse>(`${this.API_URL}/${id}`);
  }

  getByNumber(receiptNumber: string) {
    return this.http.get<ReceiptResponse>(`${this.API_URL}/number/${receiptNumber}`);
  }

  getByPayment(paymentId: number) {
    return this.http.get<ReceiptResponse>(`${this.API_URL}/payment/${paymentId}`);
  }

  getByCustomer(customerId: number) {
    return this.http.get<ReceiptResponse[]>(`${this.API_URL}/customer/${customerId}`);
  }

  getByHotel(hotelId: number) {
    return this.http.get<ReceiptResponse[]>(`${this.API_URL}/hotel/${hotelId}`);
  }

  getByOwner(ownerId: number) {
    return this.http.get<ReceiptResponse[]>(`${this.API_URL}/owner/${ownerId}`);
  }

  countByHotel(hotelId: number) {
    return this.http.get<number>(`${this.API_URL}/hotel/${hotelId}/count`);
  }

  totalByHotel(hotelId: number) {
    return this.http.get<number>(`${this.API_URL}/hotel/${hotelId}/total`);
  }

  getAll() {
    return this.http.get<ReceiptResponse[]>(this.API_URL);
  }
}
