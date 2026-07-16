import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HotelExtraService, HotelExtraServiceRequest } from '../models/hotel-extra-service.model';
import { environment } from '../../environments/environments';

@Injectable({ providedIn: 'root' })
export class HotelExtraServiceService {
  private API_URL = environment.apiUrl + 'hotel-extra-services';

  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<HotelExtraService[]>(this.API_URL);
  }

  getById(id: number) {
    return this.http.get<HotelExtraService>(`${this.API_URL}/${id}`);
  }

  getByHotel(hotelId: number) {
    return this.http.get<HotelExtraService[]>(`${this.API_URL}/hotel/${hotelId}`);
  }

  getActiveByHotel(hotelId: number) {
    return this.http.get<HotelExtraService[]>(`${this.API_URL}/hotel/${hotelId}/active`);
  }

  create(data: HotelExtraServiceRequest) {
    return this.http.post<HotelExtraService>(this.API_URL, data);
  }

  update(id: number, data: HotelExtraServiceRequest) {
    return this.http.put<HotelExtraService>(`${this.API_URL}/${id}`, data);
  }

  delete(id: number) {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
