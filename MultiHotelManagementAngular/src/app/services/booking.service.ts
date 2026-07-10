import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Booking } from '../models/booking.model';
import { environment } from '../../environments/environments';

@Injectable({ providedIn: 'root' })
export class BookingService {
  private API_URL = environment.apiUrl + 'bookings';

  constructor(private http: HttpClient) {}

  getById(id: number) {
    return this.http.get<Booking>(`${this.API_URL}/${id}`);
  }

  getByHotel(hotelId: number) {
    return this.http.get<Booking[]>(`${this.API_URL}/hotel/${hotelId}`);
  }

  getByCustomer(customerId: number) {
    return this.http.get<Booking[]>(`${this.API_URL}/customer/${customerId}`);
  }

  getByRoom(roomId: number) {
    return this.http.get<Booking[]>(`${this.API_URL}/room/${roomId}`);
  }

  delete(id: number) {
    return this.http.delete<string>(`${this.API_URL}/${id}`);
  }
}
