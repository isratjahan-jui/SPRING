import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../constants';
import { Room } from '../models/room.model';

@Injectable({ providedIn: 'root' })
export class RoomService {
  constructor(private http: HttpClient) {}

  getByHotel(hotelId: number) {
    return this.http.get<Room[]>(`${API_URL}/rooms/hotel/${hotelId}`);
  }

  getAvailable(hotelId: number) {
    return this.http.get<Room[]>(`${API_URL}/rooms/hotel/${hotelId}/available`);
  }

  getById(id: number) {
    return this.http.get<Room>(`${API_URL}/rooms/${id}`);
  }

  create(data: FormData) {
    return this.http.post<Room>(`${API_URL}/rooms`, data);
  }

  update(id: number, data: FormData) {
    return this.http.put<Room>(`${API_URL}/rooms/${id}`, data);
  }

  delete(id: number) {
    return this.http.delete<void>(`${API_URL}/rooms/${id}`);
  }

  updateAvailability(id: number, count: number) {
    return this.http.patch<void>(`${API_URL}/rooms/${id}/availability`, null, {
      params: { count },
    });
  }
}
