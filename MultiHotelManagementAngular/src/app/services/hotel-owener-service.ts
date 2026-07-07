import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { HotelOwner } from '../models/hotel-owner.model';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environments';

@Injectable({
  providedIn: 'root',
})
export class HotelOwenerService {

  private apiUrl = environment.apiUrl+ "hotel-owners";

  constructor(private http: HttpClient) { }

  createOwner(owner: HotelOwner): Observable<HotelOwner> {
    return this.http.post<HotelOwner>(this.apiUrl, owner);
  }

  getOwnerById(id: number): Observable<HotelOwner> {
    return this.http.get<HotelOwner>(`${this.apiUrl}/${id}`);
  }

  getOwnerByUserId(userId: number): Observable<HotelOwner> {
    return this.http.get<HotelOwner>(`${this.apiUrl}/user/${userId}`);
  }

  updateOwner(id: number, owner: HotelOwner): Observable<HotelOwner> {
    return this.http.put<HotelOwner>(`${this.apiUrl}/${id}`, owner);
  }

  deleteOwner(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  getAllOwners(): Observable<HotelOwner[]> {
    return this.http.get<HotelOwner[]>(this.apiUrl);
  }

}
