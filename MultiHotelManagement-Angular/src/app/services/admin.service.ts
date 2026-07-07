import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../constants';
import { Admin } from '../models/admin.model';

@Injectable({ providedIn: 'root' })
export class AdminService {
  constructor(private http: HttpClient) {}

  getProfile(email: string) {
    return this.http.get<Admin>(`${API_URL}/admins/profile/${email}`);
  }

  getAll() {
    return this.http.get<Admin[]>(`${API_URL}/admins`);
  }

  delete(id: number) {
    return this.http.delete<void>(`${API_URL}/admins/${id}`);
  }
}
