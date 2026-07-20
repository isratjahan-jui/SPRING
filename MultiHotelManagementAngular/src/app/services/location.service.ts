import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Location } from '../models/location.model';
import { environment } from '../../environments/environments';

@Injectable({ providedIn: 'root' })
export class LocationService {
  private apiUrl = environment.apiUrl + 'locations';

  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<Location[]>(this.apiUrl);
  }

  create(data: FormData) {
    return this.http.post<Location>(this.apiUrl, data);
  }

  update(id: number, data: FormData) {
    return this.http.put<Location>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number) {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
