import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../constants';
import { Report } from '../models/report.model';

export interface ReportRequest {
  totalBookings: number;
  income: number;
  occupancyRate: number;
  type: string;
  hotelId: number;
}

@Injectable({ providedIn: 'root' })
export class ReportService {
  constructor(private http: HttpClient) {}

  create(data: ReportRequest) {
    return this.http.post<Report>(`${API_URL}/reports`, data);
  }

  getByHotel(hotelId: number) {
    return this.http.get<Report[]>(`${API_URL}/reports/hotel/${hotelId}`);
  }

  getByType(type: string) {
    return this.http.get<Report[]>(`${API_URL}/reports/type/${type}`);
  }
}
