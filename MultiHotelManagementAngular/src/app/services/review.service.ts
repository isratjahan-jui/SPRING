import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ReviewRequest, ReviewResponse } from '../models/review.model';
import { environment } from '../../environments/environments';

@Injectable({ providedIn: 'root' })
export class ReviewService {
  private API_URL = environment.apiUrl + 'reviews';

  constructor(private http: HttpClient) {}

  getByHotel(hotelId: number) {
    return this.http.get<ReviewResponse[]>(`${this.API_URL}/hotel/${hotelId}`);
  }

  getByHotelAll(hotelId: number) {
    return this.http.get<ReviewResponse[]>(`${this.API_URL}/hotel/${hotelId}/all`);
  }

  getByCustomer(customerId: number) {
    return this.http.get<ReviewResponse[]>(`${this.API_URL}/customer/${customerId}`);
  }

  getById(id: number) {
    return this.http.get<ReviewResponse>(`${this.API_URL}/${id}`);
  }

  create(data: ReviewRequest) {
    return this.http.post<ReviewResponse>(this.API_URL, data);
  }

  update(id: number, data: ReviewRequest) {
    return this.http.put<ReviewResponse>(`${this.API_URL}/${id}`, data);
  }

  delete(id: number) {
    return this.http.delete(`${this.API_URL}/${id}`);
  }

  checkReview(customerId: number, bookingId: number) {
    return this.http.get<{ reviewed: boolean }>(`${this.API_URL}/check`, {
      params: { customerId: customerId.toString(), bookingId: bookingId.toString() },
    });
  }

  getAll() {
    return this.http.get<ReviewResponse[]>(this.API_URL);
  }

  getByStatus(status: string) {
    return this.http.get<ReviewResponse[]>(`${this.API_URL}/status/${status}`);
  }

  approve(id: number) {
    return this.http.put<ReviewResponse>(`${this.API_URL}/${id}/approve`, {});
  }

  reject(id: number) {
    return this.http.put<ReviewResponse>(`${this.API_URL}/${id}/reject`, {});
  }
}
