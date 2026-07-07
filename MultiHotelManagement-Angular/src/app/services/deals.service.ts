import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { API_URL } from '../constants';
import { Deals } from '../models/deals.model';

export interface DealsRequest {
  dealTitle: string;
  description: string;
  discountPercent?: number;
  discountAmount?: number;
  startDate: string;
  endDate: string;
  hotelId: number;
  roomId?: number;
  dealType: string;
}

@Injectable({ providedIn: 'root' })
export class DealsService {
  constructor(private http: HttpClient) {}

  create(data: DealsRequest) {
    return this.http.post<Deals>(`${API_URL}/deals`, data);
  }

  delete(id: number) {
    return this.http.delete<void>(`${API_URL}/deals/${id}`);
  }

  getByHotel(hotelId: number) {
    return this.http.get<Deals[]>(`${API_URL}/deals/hotel/${hotelId}`);
  }

  getByRoom(roomId: number) {
    return this.http.get<Deals[]>(`${API_URL}/deals/room/${roomId}`);
  }

  search(keyword: string) {
    const params = new HttpParams().set('keyword', keyword);
    return this.http.get<Deals[]>(`${API_URL}/deals/search`, { params });
  }
}
