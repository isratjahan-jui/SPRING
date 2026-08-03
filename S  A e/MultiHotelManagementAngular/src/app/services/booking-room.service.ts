import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BookingRoom, BookingRoomRequest } from '../models/booking-room.model';
import { environment } from '../../environments/environments';

@Injectable({ providedIn: 'root' })
export class BookingRoomService {
  private API_URL = environment.apiUrl + 'booking-rooms';

  constructor(private http: HttpClient) {}

  getById(id: number) {
    return this.http.get<BookingRoom>(`${this.API_URL}/${id}`);
  }

  getByBookingId(bookingId: number) {
    return this.http.get<BookingRoom[]>(`${this.API_URL}/booking/${bookingId}`);
  }

  getByRoomId(roomId: number) {
    return this.http.get<BookingRoom[]>(`${this.API_URL}/room/${roomId}`);
  }

  getTotalPrice(bookingId: number) {
    return this.http.get<number>(`${this.API_URL}/booking/${bookingId}/total-price`);
  }

  create(dto: BookingRoomRequest) {
    return this.http.post<BookingRoom>(this.API_URL, dto);
  }

  createMultiple(dtoList: BookingRoomRequest[]) {
    return this.http.post<BookingRoom[]>(`${this.API_URL}/multiple`, dtoList);
  }

  update(id: number, dto: BookingRoomRequest) {
    return this.http.put<BookingRoom>(`${this.API_URL}/${id}`, dto);
  }

  delete(id: number) {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }

  deleteAllByBookingId(bookingId: number) {
    return this.http.delete<void>(`${this.API_URL}/booking/${bookingId}`);
  }
}
