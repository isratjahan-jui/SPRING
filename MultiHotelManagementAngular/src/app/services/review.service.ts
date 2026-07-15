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
}
