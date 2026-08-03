import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environments';

export interface ExtraServiceResponse {
  id: number;
  serviceType: string;
  price: number;
  serviceStatus: string;
  bookingId: number;
  bookingReference: string;
}

export interface ExtraServiceRequest {
  serviceType: string;
  price: number;
  serviceStatus: string;
  bookingId: number;
}

@Injectable({ providedIn: 'root' })
export class ExtraServiceService {
  private apiUrl = environment.apiUrl + 'extra-services';

  constructor(private http: HttpClient) {}

  getByBooking(bookingId: number) {
    return this.http.get<ExtraServiceResponse[]>(`${this.apiUrl}/booking/${bookingId}`);
  }

  create(data: ExtraServiceRequest) {
    return this.http.post<ExtraServiceResponse>(this.apiUrl, data);
  }

  update(id: number, data: ExtraServiceRequest) {
    return this.http.put<ExtraServiceResponse>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number) {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
