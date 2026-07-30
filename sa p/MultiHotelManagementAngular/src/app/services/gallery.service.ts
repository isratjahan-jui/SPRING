import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Gallery, GalleryRequest } from '../models/gallery.model';
import { environment } from '../../environments/environments';

@Injectable({ providedIn: 'root' })
export class GalleryService {
  private API_URL = environment.apiUrl + 'gallery';

  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<Gallery[]>(this.API_URL);
  }

  getById(id: number) {
    return this.http.get<Gallery>(`${this.API_URL}/${id}`);
  }

  getByHotel(hotelId: number) {
    return this.http.get<Gallery[]>(`${this.API_URL}/hotel/${hotelId}`);
  }

  getByCategory(hotelId: number, category: string) {
    return this.http.get<Gallery[]>(`${this.API_URL}/hotel/${hotelId}/category/${category}`);
  }

  create(data: GalleryRequest, image: File) {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(data)], { type: 'application/json' }));
    formData.append('image', image);
    return this.http.post<Gallery>(`${this.API_URL}/upload`, formData);
  }

  uploadMultiple(hotelId: number, images: File[], category: string = 'GENERAL') {
    const formData = new FormData();
    images.forEach((img) => formData.append('images', img));
    return this.http.post<Gallery[]>(
      `${this.API_URL}/upload/multiple/${hotelId}?category=${category}`,
      formData,
    );
  }

  update(id: number, data: GalleryRequest) {
    return this.http.put<Gallery>(`${this.API_URL}/${id}`, data);
  }

  delete(id: number) {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }

  deleteByHotel(hotelId: number) {
    return this.http.delete<void>(`${this.API_URL}/hotel/${hotelId}`);
  }

  deleteByCategory(hotelId: number, category: string) {
    return this.http.delete<void>(`${this.API_URL}/hotel/${hotelId}/category/${category}`);
  }
}
