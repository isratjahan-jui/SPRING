import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../constants';
import { Location } from '../models/location.model';

@Injectable({ providedIn: 'root' })
export class LocationService {
  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<Location[]>(`${API_URL}/locations`);
  }

  getById(id: number) {
    return this.http.get<Location>(`${API_URL}/locations/${id}`);
  }

  search(keyword: string) {
    return this.http.get<Location[]>(`${API_URL}/locations/search`, {
      params: { keyword },
    });
  }
}
