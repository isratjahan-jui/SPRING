import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { WishlistRequest, WishlistResponse } from '../models/wishlist.model';
import { environment } from '../../environments/environments';

@Injectable({ providedIn: 'root' })
export class WishlistService {
  private API_URL = environment.apiUrl + 'wishlists';

  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<WishlistResponse[]>(this.API_URL);
  }

  getByUser(userId: number) {
    return this.http.get<WishlistResponse[]>(`${this.API_URL}/user/${userId}`);
  }

  getByCustomer(customerId: number) {
    return this.http.get<WishlistResponse[]>(`${this.API_URL}/customer/${customerId}`);
  }

  getByHotel(hotelId: number) {
    return this.http.get<WishlistResponse[]>(`${this.API_URL}/hotel/${hotelId}`);
  }

  exists(customerId: number, hotelId: number) {
    return this.http.get<boolean>(`${this.API_URL}/exists/customer`, {
      params: { customerId, hotelId },
    });
  }

  existsByUser(userId: number, hotelId: number) {
    return this.http.get<boolean>(`${this.API_URL}/exists/user`, {
      params: { userId, hotelId },
    });
  }

  create(data: WishlistRequest) {
    return this.http.post<WishlistResponse>(this.API_URL, data);
  }

  delete(id: number) {
    return this.http.delete(`${this.API_URL}/${id}`);
  }
}
