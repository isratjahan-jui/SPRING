import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Booking } from '../models/booking.model';
import { environment } from '../../environments/environments';
import { BookingRequest } from '../models/booking.model';

@Injectable({ providedIn: 'root' })
export class BookingService {
  private API_URL = environment.apiUrl + 'bookings';

  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<Booking[]>(this.API_URL);
  }

  create(data: BookingRequest) {
    return this.http.post<Booking>(this.API_URL, data);
  }

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

  updateStatus(id: number, status: string) {
    return this.http.patch<Booking>(`${this.API_URL}/${id}/status?status=${status}`, {});
  }

  onlineCheckIn(id: number, idImage: File) {
    const formData = new FormData();
    formData.append('idImage', idImage);
    return this.http.post<Booking>(`${this.API_URL}/${id}/online-checkin`, formData);
  }

  expressCheckOut(id: number) {
    return this.http.post<Booking>(`${this.API_URL}/${id}/express-checkout`, {});
  }

  getByOwner(ownerId: number) {
    return this.http.get<Booking[]>(`${this.API_URL}/owner/${ownerId}`);
  }

  markNoShow(id: number) {
    return this.http.post<Booking>(`${this.API_URL}/${id}/no-show`, {});
  }

  addExtraCharges(id: number, amount: number) {
    return this.http.patch<Booking>(`${this.API_URL}/${id}/extra-charges?amount=${amount}`, {});
  }
}
