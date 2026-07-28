import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environments';
import { NotificationItem } from '../../../services/notification.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-admin-notifications',
  imports: [CommonModule, FormsModule],
  templateUrl: './notifications.html',
  styleUrl: './notifications.css',
})
export class AdminNotifications implements OnInit {
  private http = inject(HttpClient);
  private cdr = inject(ChangeDetectorRef);
  private auth = inject(AuthService);
  private API_URL = environment.apiUrl + 'notifications';

  notifications: NotificationItem[] = [];
  loading = false;
  searchUserId: number | null = null;

  showForm = false;
  form = {
    userId: null as number | null,
    message: '',
    type: 'GENERAL',
    channel: 'WEB',
  };

  types = [
    'BOOKING_CONFIRMED',
    'BOOKING_CANCELLED',
    'BOOKING_REMINDER',
    'PAYMENT_SUCCESSFUL',
    'PAYMENT_FAILED',
    'PAYMENT_REFUNDED',
    'HOTEL_APPROVED',
    'HOTEL_REJECTED',
    'REVIEW_RECEIVED',
    'SUPPORT_REPLIED',
    'GENERAL',
    'PROMOTIONAL',
  ];

  channels = ['EMAIL', 'SMS', 'PUSH', 'WEB', 'SYSTEM'];

  ngOnInit() {
    const user = this.auth.getUser();
    if (user) {
      this.loadByRole(user.role);
    }
  }

  loadByRole(role: string) {
    const user = this.auth.getUser();
    if (!user) return;
    this.loading = true;
    this.http
      .get<NotificationItem[]>(`${this.API_URL}/user/${user.userId}/role/${role}`)
      .subscribe({
        next: (data) => {
          this.notifications = data;
          this.loading = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.notifications = [];
          this.loading = false;
          this.cdr.markForCheck();
        },
      });
  }

  openCreate() {
    this.form = { userId: null, message: '', type: 'GENERAL', channel: 'WEB' };
    this.showForm = true;
    this.cdr.markForCheck();
  }

  closeForm() {
    this.showForm = false;
    this.cdr.markForCheck();
  }

  send() {
    if (!this.form.userId || !this.form.message.trim()) return;

    this.http.post(this.API_URL, this.form).subscribe({
      next: () => {
        this.closeForm();
        if (this.searchUserId === this.form.userId) {
          this.loadByUser(this.form.userId!);
        }
      },
      error: (err) => console.error('Send failed', err),
    });
  }

  loadByUser(userId: number) {
    if (!userId) return;
    this.searchUserId = userId;
    this.loading = true;
    this.http.get<NotificationItem[]>(`${this.API_URL}/user/${userId}`).subscribe({
      next: (data) => {
        this.notifications = data;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.notifications = [];
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  markAsRead(id: number) {
    this.http.put(`${this.API_URL}/${id}/read`, {}).subscribe({
      next: () => {
        this.notifications = this.notifications.map((n) =>
          n.id === id ? { ...n, readStatus: true } : n,
        );
        this.cdr.markForCheck();
      },
    });
  }

  deleteNotification(id: number) {
    if (!confirm('Delete this notification?')) return;
    this.http.delete(`${this.API_URL}/${id}`).subscribe({
      next: () => {
        this.notifications = this.notifications.filter((n) => n.id !== id);
        this.cdr.markForCheck();
      },
    });
  }

  getStatusClass(type: string): string {
    if (type.startsWith('PAYMENT')) return 'bg-success';
    if (type.startsWith('BOOKING')) return 'bg-primary';
    if (type === 'PROMOTIONAL') return 'bg-warning text-dark';
    if (type === 'GENERAL') return 'bg-secondary';
    return 'bg-info text-dark';
  }

  getChannelClass(channel: string): string {
    switch (channel) {
      case 'EMAIL':
        return 'bg-primary';
      case 'SMS':
        return 'bg-success';
      case 'PUSH':
        return 'bg-warning text-dark';
      case 'WEB':
        return 'bg-info text-dark';
      case 'SYSTEM':
        return 'bg-dark';
      default:
        return 'bg-secondary';
    }
  }

  get unreadCount(): number {
    return this.notifications.filter((n) => !n.readStatus).length;
  }
}
