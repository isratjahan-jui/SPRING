import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../constants';
import { WishlistItem } from '../models/wishlist.model';

@Injectable({ providedIn: 'root' })
export class WishlistService {
  constructor(private http: HttpClient) {}

  getByUserId(userId: number) {
    return this.http.get<WishlistItem[]>(`${API_URL}/wishlists/user/${userId}`);
  }

  getByCustomerId(customerId: number) {
    return this.http.get<WishlistItem[]>(`${API_URL}/wishlists/customer/${customerId}`);
  }

  add(data: { customerId: number; hotelId: number }) {
    return this.http.post<WishlistItem>(`${API_URL}/wishlists`, data);
  }

  remove(id: number) {
    return this.http.delete<void>(`${API_URL}/wishlists/${id}`);
  }

  exists(customerId: number, hotelId: number) {
    return this.http.get<boolean>(`${API_URL}/wishlists/exists/customer`, {
      params: { customerId, hotelId },
    });
  }
}
