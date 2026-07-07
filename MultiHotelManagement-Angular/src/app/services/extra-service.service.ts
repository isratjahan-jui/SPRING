import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../constants';
import { ExtraService } from '../models/extra-service.model';

export interface ExtraServiceRequest {
  serviceType: string;
  price: number;
  serviceStatus: string;
  bookingId: number;
}

@Injectable({ providedIn: 'root' })
export class ExtraServiceService {
  constructor(private http: HttpClient) {}

  create(data: ExtraServiceRequest) {
    return this.http.post<ExtraService>(`${API_URL}/extra-services`, data);
  }

  update(id: number, data: ExtraServiceRequest) {
    return this.http.put<ExtraService>(`${API_URL}/extra-services/${id}`, data);
  }

  getById(id: number) {
    return this.http.get<ExtraService>(`${API_URL}/extra-services/${id}`);
  }

  getByBooking(bookingId: number) {
    return this.http.get<ExtraService[]>(`${API_URL}/extra-services/booking/${bookingId}`);
  }

  delete(id: number) {
    return this.http.delete<void>(`${API_URL}/extra-services/${id}`);
  }
}
