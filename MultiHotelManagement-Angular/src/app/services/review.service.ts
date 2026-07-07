import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../constants';
import { Review, ReviewRequest } from '../models/review.model';

@Injectable({ providedIn: 'root' })
export class ReviewService {
  constructor(private http: HttpClient) {}

  create(data: ReviewRequest) {
    return this.http.post<Review>(`${API_URL}/reviews`, data);
  }

  update(id: number, data: ReviewRequest) {
    return this.http.put<Review>(`${API_URL}/reviews/${id}`, data);
  }

  getById(id: number) {
    return this.http.get<Review>(`${API_URL}/reviews/${id}`);
  }

  getByHotel(hotelId: number) {
    return this.http.get<Review[]>(`${API_URL}/reviews/hotel/${hotelId}`);
  }

  delete(id: number) {
    return this.http.delete<string>(`${API_URL}/reviews/${id}`);
  }
}
