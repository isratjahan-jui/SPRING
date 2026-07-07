import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';

import { Hotel, HotelRequest } from '../models/hotel.model';
import { environment } from '../../environments/environments';

@Injectable({ providedIn: 'root' })
export class HotelService {

 private API_URL = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getAllApproved() {
    return this.http.get<Hotel[]>(`${this.API_URL}/hotels/approved`);
  }

  getById(id: number) {
    return this.http.get<Hotel>(`${this.API_URL}/hotels/${id}`);
  }

  getByOwner(ownerId: number) {
    return this.http.get<Hotel[]>(`${this.API_URL}/hotels/owner/${ownerId}`);
  }

  getByCity(city: string) {
    return this.http.get<Hotel[]>(`${this.API_URL}/hotels/city/${city}`);
  }

  create(data: HotelRequest) {
    return this.http.post<Hotel>(`${this.API_URL}/hotels`, data);
  }

  update(id: number, data: HotelRequest) {
    return this.http.put<Hotel>(`${this.API_URL}/hotels/${id}`, data);
  }

  delete(id: number) {
    return this.http.delete<string>(`${this.API_URL}/hotels/${id}`);
  }
}
