import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DealRequest, DealResponse } from '../models/deal.model';
import { environment } from '../../environments/environments';

@Injectable({ providedIn: 'root' })
export class DealService {
  private API_URL = environment.apiUrl + 'deals';

  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<DealResponse[]>(this.API_URL);
  }

  getByHotel(hotelId: number) {
    return this.http.get<DealResponse[]>(`${this.API_URL}/hotel/${hotelId}`);
  }

  getByRoom(roomId: number) {
    return this.http.get<DealResponse[]>(`${this.API_URL}/room/${roomId}`);
  }

  create(data: DealRequest) {
    return this.http.post<DealResponse>(this.API_URL, data);
  }

  update(id: number, data: DealRequest) {
    return this.http.put<DealResponse>(`${this.API_URL}/${id}`, data);
  }

  delete(id: number) {
    return this.http.delete(`${this.API_URL}/${id}`);
  }

  search(keyword: string) {
    return this.http.get<DealResponse[]>(`${this.API_URL}/search`, {
      params: { keyword },
    });
  }
}
