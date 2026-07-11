import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { OwnerDashboardStats } from '../models/owner-dashboard.model';
import { environment } from '../../environments/environments';

@Injectable({ providedIn: 'root' })
export class OwnerDashboardService {
  private API_URL = environment.apiUrl + 'hotel-owner-dashboard';

  constructor(private http: HttpClient) {}

  getStats(ownerId: number) {
    return this.http.get<OwnerDashboardStats>(`${this.API_URL}/stats/${ownerId}`);
  }
}
