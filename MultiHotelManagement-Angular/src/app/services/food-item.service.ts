import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../constants';
import { FoodItem } from '../models/food-item.model';

export interface FoodItemRequest {
  itemName: string;
  description: string;
  foodPrice: number;
  category: string;
  hotelId: number;
}

@Injectable({ providedIn: 'root' })
export class FoodItemService {
  constructor(private http: HttpClient) {}

  create(data: FoodItemRequest) {
    return this.http.post<FoodItem>(`${API_URL}/food-items`, data);
  }

  update(id: number, data: FoodItemRequest) {
    return this.http.put<FoodItem>(`${API_URL}/food-items/${id}`, data);
  }

  getById(id: number) {
    return this.http.get<FoodItem>(`${API_URL}/food-items/${id}`);
  }

  getByHotel(hotelId: number) {
    return this.http.get<FoodItem[]>(`${API_URL}/food-items/hotel/${hotelId}`);
  }

  delete(id: number) {
    return this.http.delete<void>(`${API_URL}/food-items/${id}`);
  }
}
