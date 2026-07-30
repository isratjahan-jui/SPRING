import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environments';

export interface PlatformSummary {
  startDate: string;
  endDate: string;
  totalHotels: number;
  totalBookings: number;
  totalRevenue: number;
}

@Injectable({ providedIn: 'root' })
export class ReportService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  getPlatformSummary(startDate?: string, endDate?: string): Observable<PlatformSummary> {
    let params: { [key: string]: string } = {};
    if (startDate) params['startDate'] = startDate;
    if (endDate) params['endDate'] = endDate;
    return this.http.get<PlatformSummary>(`${this.apiUrl}reports/platform-summary`, { params });
  }
}
