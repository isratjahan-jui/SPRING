import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Hotel, HotelRequest } from '../models/hotel.model';
import { environment } from '../../environments/environments';

@Injectable({ providedIn: 'root' })
export class HotelService {
  private API_URL = environment.apiUrl + 'hotels';

  constructor(private http: HttpClient) {}

  getAllApproved() {
    return this.http.get<Hotel[]>(`${this.API_URL}/approved`);
  }

  getById(id: number) {
    return this.http.get<Hotel>(`${this.API_URL}/${id}`);
  }

  getByOwner(ownerId: number) {
    return this.http.get<Hotel[]>(`${this.API_URL}/owner/${ownerId}`);
  }

  getByCity(city: string) {
    return this.http.get<Hotel[]>(`${this.API_URL}/city/${city}`);
  }

  create(data: HotelRequest, image?: File) {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(data)], { type: 'application/json' }));
    if (image) {
      formData.append('image', image);
    }
    return this.http.post<Hotel>(this.API_URL, formData);
  }

  update(id: number, data: HotelRequest, image?: File) {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(data)], { type: 'application/json' }));
    if (image) {
      formData.append('image', image);
    }
    return this.http.put<Hotel>(`${this.API_URL}/${id}`, formData);
  }

  delete(id: number) {
    return this.http.delete<string>(`${this.API_URL}/${id}`);
  }
  getAll() {
    return this.http.get<Hotel[]>(`${this.API_URL}/all`);
  }

  getPending() {
    return this.http.get<Hotel[]>(`${this.API_URL}/pending`);
  }

  approveHotel(id: number) {
    return this.http.put<Hotel>(`${this.API_URL}/${id}/approve`, {});
  }

  rejectHotel(id: number, reason: string) {
    return this.http.put<Hotel>(`${this.API_URL}/${id}/reject`, { reason });
  }
}
