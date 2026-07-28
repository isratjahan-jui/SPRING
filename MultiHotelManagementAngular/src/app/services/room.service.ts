import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Room, RoomRequest } from '../models/room.model';
import { environment } from '../../environments/environments';

export interface RoomAvailabilityResponse {
  totalRooms: number;
  availableForDates: number;
  roomType: string;
  price: number;
}

@Injectable({ providedIn: 'root' })
export class RoomService {
  private API_URL = environment.apiUrl + 'rooms';

  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<Room[]>(this.API_URL);
  }

  getById(id: number) {
    return this.http.get<Room>(`${this.API_URL}/${id}`);
  }

  getByHotel(hotelId: number) {
    return this.http.get<Room[]>(`${this.API_URL}/hotel/${hotelId}`);
  }

  getAvailable(hotelId: number) {
    return this.http.get<Room[]>(`${this.API_URL}/hotel/${hotelId}/available`);
  }

  create(data: RoomRequest, image?: File) {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(data)], { type: 'application/json' }));
    if (image) formData.append('image', image);
    return this.http.post<Room>(this.API_URL, formData);
  }

  update(id: number, data: RoomRequest, image?: File) {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(data)], { type: 'application/json' }));
    if (image) formData.append('image', image);
    return this.http.put<Room>(`${this.API_URL}/${id}`, formData);
  }

  delete(id: number) {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }

  updateAvailability(id: number, count: number) {
    return this.http.patch<void>(`${this.API_URL}/${id}/availability?count=${count}`, {});
  }

  getAvailabilityForDates(
    roomId: number,
    checkIn: string,
    checkOut: string,
  ): Observable<RoomAvailabilityResponse> {
    return this.http.get<RoomAvailabilityResponse>(
      `${this.API_URL}/${roomId}/availability?checkIn=${checkIn}&checkOut=${checkOut}`,
    );
  }
}
