import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { HotelOwner } from '../models/hotel-owner.model';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environments';

@Injectable({
  providedIn: 'root',
})
export class HotelOwnerService {
  
  private apiUrl = environment.apiUrl + 'hotel-owners';

  constructor(private http: HttpClient) {}

  createOwner(owner: HotelOwner, image?: File): Observable<HotelOwner> {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(owner)], { type: 'application/json' }));
    if (image) {
      formData.append('image', image);
    }
    return this.http.post<HotelOwner>(this.apiUrl, formData);
  }

  getOwnerById(id: number): Observable<HotelOwner> {
    return this.http.get<HotelOwner>(`${this.apiUrl}/${id}`);
  }

  getOwnerByUserId(userId: number): Observable<HotelOwner> {
    return this.http.get<HotelOwner>(`${this.apiUrl}/user/${userId}`);
  }

  updateOwner(id: number, owner: HotelOwner, image?: File): Observable<HotelOwner> {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(owner)], { type: 'application/json' }));
    if (image) {
      formData.append('image', image);
    }
    return this.http.put<HotelOwner>(`${this.apiUrl}/${id}`, formData);
  }

  deleteOwner(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  getAllOwners(): Observable<HotelOwner[]> {
    return this.http.get<HotelOwner[]>(this.apiUrl);
  }
}
