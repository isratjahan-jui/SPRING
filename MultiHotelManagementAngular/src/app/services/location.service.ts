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
}
