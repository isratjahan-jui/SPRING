import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../constants';
import { HotelOwner } from '../models/hotel-owner.model';

export interface HotelOwnerRequest {
  name: string;
  email: string;
  phone: string;
  address?: string;
  gender?: string;
  dateOfBirth?: string;
  image?: string;
  userId: number;
}

@Injectable({ providedIn: 'root' })
export class HotelOwnerService {
  constructor(private http: HttpClient) {}

  create(data: HotelOwnerRequest) {
    return this.http.post<HotelOwner>(`${API_URL}/hotel-owners`, data);
  }

  getById(id: number) {
    return this.http.get<HotelOwner>(`${API_URL}/hotel-owners/${id}`);
  }

  getByUserId(userId: number) {
    return this.http.get<HotelOwner>(`${API_URL}/hotel-owners/user/${userId}`);
  }

  update(id: number, data: HotelOwnerRequest) {
    return this.http.put<HotelOwner>(`${API_URL}/hotel-owners/${id}`, data);
  }

  delete(id: number) {
    return this.http.delete<string>(`${API_URL}/hotel-owners/${id}`);
  }
}
