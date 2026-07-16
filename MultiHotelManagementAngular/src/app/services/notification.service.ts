import { Injectable, NgZone, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environments';
import { AuthService } from './auth.service';
import { StorageService } from './storage.service';
import { BehaviorSubject, Observable } from 'rxjs';

export interface NotificationItem {
  id: number;
  message: string;
  type: string;
  channel: string;
  readStatus: boolean;
  userName: string;
  createdAt: string;
  updatedAt: string;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private http = inject(HttpClient);
  private auth = inject(AuthService);
  private storage = inject(StorageService);
  private zone = inject(NgZone);
  private API_URL = environment.apiUrl + 'notifications';

  private notificationsSubject = new BehaviorSubject<NotificationItem[]>([]);
  notifications$ = this.notificationsSubject.asObservable();

  private unreadCountSubject = new BehaviorSubject<number>(0);
  unreadCount$ = this.unreadCountSubject.asObservable();

  private eventSource: EventSource | null = null;

  connect() {
    const user = this.auth.getUser();
    const token = this.storage.getToken();
    if (!user || !token || this.eventSource) return;

    this.disconnect();
    this.loadNotifications();

    this.eventSource = new EventSource(`${this.API_URL}/subscribe/${user.userId}?token=${token}`);

    this.eventSource.addEventListener('notification', (event: MessageEvent) => {
      this.zone.run(() => {
        try {
          const notification: NotificationItem = JSON.parse(event.data);
          const current = this.notificationsSubject.value;
          this.notificationsSubject.next([notification, ...current]);
          this.unreadCountSubject.next(this.unreadCountSubject.value + 1);
        } catch {}
      });
    });

    this.eventSource.onerror = () => {
      this.disconnect();
    };
  }

  disconnect() {
    if (this.eventSource) {
      this.eventSource.close();
      this.eventSource = null;
    }
  }

  loadNotifications() {
    const user = this.auth.getUser();
    if (!user) return;

    this.http.get<NotificationItem[]>(`${this.API_URL}/user/${user.userId}`).subscribe((data) => {
      this.notificationsSubject.next(data);
      this.unreadCountSubject.next(data.filter((n) => !n.readStatus).length);
    });
  }

  getUnreadCount(): Observable<number> {
    return this.unreadCount$;
  }

  markAsRead(id: number) {
    const current = this.notificationsSubject.value.map((n) =>
      n.id === id ? { ...n, readStatus: true } : n,
    );
    this.notificationsSubject.next(current);
    this.unreadCountSubject.next(current.filter((n) => !n.readStatus).length);
    this.http.put(`${this.API_URL}/${id}/read`, {}).subscribe();
  }

  markAllAsRead() {
    const current = this.notificationsSubject.value.map((n) => ({ ...n, readStatus: true }));
    this.notificationsSubject.next(current);
    this.unreadCountSubject.next(0);
    current.forEach((n) => this.http.put(`${this.API_URL}/${n.id}/read`, {}).subscribe());
  }

  delete(id: number) {
    this.http.delete(`${this.API_URL}/${id}`).subscribe(() => {
      const current = this.notificationsSubject.value.filter((n) => n.id !== id);
      this.notificationsSubject.next(current);
      this.unreadCountSubject.next(current.filter((n) => !n.readStatus).length);
    });
  }
}
