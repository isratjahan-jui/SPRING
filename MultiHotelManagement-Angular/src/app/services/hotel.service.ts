import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { API_URL } from '../constants';
import { Hotel, HotelRequest } from '../models/hotel.model';

@Injectable({ providedIn: 'root' })
export class HotelService {
  constructor(private http: HttpClient) {}

  getAllApproved() {
    return this.http.get<Hotel[]>(`${API_URL}/hotels/approved`);
  }

  getById(id: number) {
    return this.http.get<Hotel>(`${API_URL}/hotels/${id}`);
  }

  getByOwner(ownerId: number) {
    return this.http.get<Hotel[]>(`${API_URL}/hotels/owner/${ownerId}`);
  }

  getByCity(city: string) {
    return this.http.get<Hotel[]>(`${API_URL}/hotels/city/${city}`);
  }

  create(data: HotelRequest) {
    return this.http.post<Hotel>(`${API_URL}/hotels`, data);
  }

  update(id: number, data: HotelRequest) {
    return this.http.put<Hotel>(`${API_URL}/hotels/${id}`, data);
  }

  delete(id: number) {
    return this.http.delete<string>(`${API_URL}/hotels/${id}`);
  }
}
