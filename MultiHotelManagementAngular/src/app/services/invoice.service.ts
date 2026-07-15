import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { InvoiceResponse } from '../models/invoice.model';
import { environment } from '../../environments/environments';

@Injectable({ providedIn: 'root' })
export class InvoiceService {
  private API_URL = environment.apiUrl + 'invoices';

  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<InvoiceResponse[]>(this.API_URL);
  }

  getById(id: number) {
    return this.http.get<InvoiceResponse>(`${this.API_URL}/${id}`);
  }

  getByCustomer(customerId: number) {
    return this.http.get<InvoiceResponse[]>(`${this.API_URL}/customer/${customerId}`);
  }
}
