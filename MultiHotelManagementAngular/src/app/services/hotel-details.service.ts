import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HotelDetails } from '../models/hotel-details.model';
import { environment } from '../../environments/environments';

export interface HotelDetailsRequest {
  hotelId: number;
  ownerSpeach: string;
  description: string;
  hotelPolicy: string;
  pricePerNight: number;
  checkInTime: string;
  checkOutTime: string;
  contactEmail: string;
  contactPhone: string;
  cancellationPolicy: string;
  petPolicy: string;
  smokingPolicy: string;
  childPolicy: string;
  languages: string;
  nearbyAttractions: string;
}

@Injectable({ providedIn: 'root' })
export class HotelDetailsService {
  private API_URL = environment.apiUrl + 'hotel-details';

  constructor(private http: HttpClient) {}

  getByHotelId(hotelId: number) {
    return this.http.get<HotelDetails>(`${this.API_URL}/hotel/${hotelId}`);
  }

  create(data: HotelDetailsRequest) {
    return this.http.post<HotelDetails>(this.API_URL, data);
  }

  update(id: number, data: HotelDetailsRequest) {
    return this.http.put<HotelDetails>(`${this.API_URL}/${id}`, data);
  }

  delete(id: number) {
    return this.http.delete<string>(`${this.API_URL}/${id}`);
  }
}
