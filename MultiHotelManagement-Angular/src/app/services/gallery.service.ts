import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../constants';
import { GalleryImage } from '../models/gallery.model';

@Injectable({ providedIn: 'root' })
export class GalleryService {
  constructor(private http: HttpClient) {}

  getByHotel(hotelId: number) {
    return this.http.get<GalleryImage[]>(`${API_URL}/gallery/hotel/${hotelId}`);
  }

  upload(hotelId: number, file: File, category: string, caption?: string) {
    const fd = new FormData();
    fd.append('data', new Blob([JSON.stringify({ hotelId, category, caption })], { type: 'application/json' }));
    fd.append('image', file);
    return this.http.post<GalleryImage>(`${API_URL}/gallery/upload`, fd);
  }

  uploadMultiple(hotelId: number, files: File[], category: string) {
    const fd = new FormData();
    files.forEach(f => fd.append('images', f));
    return this.http.post<GalleryImage[]>(`${API_URL}/gallery/upload/multiple/${hotelId}?category=${category}`, fd);
  }

  delete(id: number) {
    return this.http.delete<void>(`${API_URL}/gallery/${id}`);
  }
}
