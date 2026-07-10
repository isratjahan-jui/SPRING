import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Facility, FacilityRequest } from '../models/facility.model';
import { environment } from '../../environments/environments';

@Injectable({ providedIn: 'root' })
export class FacilityService {
  private API_URL = environment.apiUrl + 'facilities';

  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<Facility[]>(this.API_URL);
  }

  getById(id: number) {
    return this.http.get<Facility>(`${this.API_URL}/${id}`);
  }

  getByHotel(hotelId: number) {
    return this.http.get<Facility[]>(`${this.API_URL}/hotel/${hotelId}`);
  }

  create(data: FacilityRequest) {
    return this.http.post<Facility>(this.API_URL, data);
  }

  createBulk(hotelId: number, data: FacilityRequest[]) {
    return this.http.post<Facility[]>(`${this.API_URL}/bulk/${hotelId}`, data);
  }

  update(id: number, data: FacilityRequest) {
    return this.http.put<Facility>(`${this.API_URL}/${id}`, data);
  }

  delete(id: number) {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }

  deleteByHotel(hotelId: number) {
    return this.http.delete<void>(`${this.API_URL}/hotel/${hotelId}`);
  }
}
