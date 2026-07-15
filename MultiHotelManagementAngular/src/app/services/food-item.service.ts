import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FoodItem, FoodItemRequest } from '../models/food-item.model';
import { environment } from '../../environments/environments';

@Injectable({ providedIn: 'root' })
export class FoodItemService {
  private API_URL = environment.apiUrl + 'food-items';

  constructor(private http: HttpClient) {}

  getById(id: number) {
    return this.http.get<FoodItem>(`${this.API_URL}/${id}`);
  }

  getByHotel(hotelId: number) {
    return this.http.get<FoodItem[]>(`${this.API_URL}/hotel/${hotelId}`);
  }

  create(data: FoodItemRequest, image?: File) {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(data)], { type: 'application/json' }));
    if (image) formData.append('image', image);
    return this.http.post<FoodItem>(this.API_URL, formData);
  }

  update(id: number, data: FoodItemRequest, image?: File) {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(data)], { type: 'application/json' }));
    if (image) formData.append('image', image);
    return this.http.put<FoodItem>(`${this.API_URL}/${id}`, formData);
  }

  delete(id: number) {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
