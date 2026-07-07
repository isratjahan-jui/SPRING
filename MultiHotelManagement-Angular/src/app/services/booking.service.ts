import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../constants';
import { Booking, BookingRequest } from '../models/booking.model';
import { Customer } from '../models/customer.model';

@Injectable({ providedIn: 'root' })
export class BookingService {
  constructor(private http: HttpClient) {}

  create(data: BookingRequest) {
    return this.http.post<Booking>(`${API_URL}/bookings`, data);
  }

  getById(id: number) {
    return this.http.get<Booking>(`${API_URL}/bookings/${id}`);
  }

  getByCustomer(customerId: number) {
    return this.http.get<Booking[]>(`${API_URL}/bookings/customer/${customerId}`);
  }

  getByHotel(hotelId: number) {
    return this.http.get<Booking[]>(`${API_URL}/bookings/hotel/${hotelId}`);
  }

  cancel(id: number) {
    return this.http.delete<string>(`${API_URL}/bookings/${id}`);
  }

  addFoodItems(bookingId: number, foodItemIds: number[]) {
    return this.http.post<Booking>(`${API_URL}/bookings/${bookingId}/food-items`, foodItemIds);
  }

  getCustomerByUserId(userId: number) {
    return this.http.get<Customer>(`${API_URL}/customers/user/${userId}`);
  }

  getOwnerByUserId(userId: number) {
    return this.http.get<{ id: number; name: string; email: string; phone: string; address: string }>(`${API_URL}/hotel-owners/user/${userId}`);
  }
}
