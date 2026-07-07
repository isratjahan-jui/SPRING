import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../constants';
import { Notification } from '../models/notification.model';

export interface NotificationRequest {
  message: string;
  type: string;
  channel: string;
  userId: number;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  constructor(private http: HttpClient) {}

  create(data: NotificationRequest) {
    return this.http.post<Notification>(`${API_URL}/notifications`, data);
  }

  markAsRead(id: number) {
    return this.http.put<void>(`${API_URL}/notifications/${id}/read`, {});
  }

  delete(id: number) {
    return this.http.delete<void>(`${API_URL}/notifications/${id}`);
  }

  getByUser(userId: number) {
    return this.http.get<Notification[]>(`${API_URL}/notifications/user/${userId}`);
  }

  getUnread(userId: number) {
    return this.http.get<Notification[]>(`${API_URL}/notifications/user/${userId}/unread`);
  }
}
