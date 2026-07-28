import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommissionResponse } from '../models/commission.model';
import { environment } from '../../environments/environments';

@Injectable({ providedIn: 'root' })
export class CommissionService {
  private API_URL = environment.apiUrl + 'commissions';

  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<CommissionResponse[]>(this.API_URL);
  }

  getById(id: number) {
    return this.http.get<CommissionResponse>(`${this.API_URL}/${id}`);
  }

  getByOwner(ownerId: number) {
    return this.http.get<CommissionResponse[]>(`${this.API_URL}/owner/${ownerId}`);
  }

  getByHotel(hotelId: number) {
    return this.http.get<CommissionResponse[]>(`${this.API_URL}/hotel/${hotelId}`);
  }

  getAdminTotal() {
    return this.http.get<number>(`${this.API_URL}/admin/total`);
  }

  getOwnerTotal(ownerId: number) {
    return this.http.get<number>(`${this.API_URL}/owner/${ownerId}/total`);
  }
}
