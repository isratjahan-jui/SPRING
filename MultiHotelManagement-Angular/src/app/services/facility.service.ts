import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../constants';
import { Facility } from '../models/room.model';

@Injectable({ providedIn: 'root' })
export class FacilityService {
  constructor(private http: HttpClient) {}

  getByHotel(hotelId: number) {
    return this.http.get<Facility[]>(`${API_URL}/facilities/hotel/${hotelId}`);
  }

  create(data: { hotelId: number; name: string; description?: string }) {
    return this.http.post<Facility>(`${API_URL}/facilities`, data);
  }

  createBulk(hotelId: number, items: { name: string; description?: string }[]) {
    return this.http.post<Facility[]>(`${API_URL}/facilities/bulk/${hotelId}`, items);
  }

  delete(id: number) {
    return this.http.delete<void>(`${API_URL}/facilities/${id}`);
  }
}
