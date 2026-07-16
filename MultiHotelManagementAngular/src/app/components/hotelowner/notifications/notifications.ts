import { ChangeDetectorRef, Component, inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService, NotificationItem } from '../../../services/notification.service';

@Component({
  selector: 'app-owner-notifications',
  imports: [CommonModule],
  templateUrl: './notifications.html',
  styleUrl: './notifications.css',
})
export class OwnerNotifications implements OnInit, OnDestroy {
  private notificationService = inject(NotificationService);
  private cdr = inject(ChangeDetectorRef);

  notifications: NotificationItem[] = [];
  loading = true;

  ngOnInit() {
    this.notificationService.connect();
    this.notificationService.notifications$.subscribe((data) => {
      this.notifications = data;
      this.loading = false;
      this.cdr.markForCheck();
    });
  }

  ngOnDestroy() {
    this.notificationService.disconnect();
  }

  markAsRead(id: number) {
    this.notificationService.markAsRead(id);
  }

  markAllAsRead() {
    this.notificationService.markAllAsRead();
  }

  deleteNotification(id: number) {
    this.notificationService.delete(id);
  }

  get unreadCount(): number {
    return this.notifications.filter((n) => !n.readStatus).length;
  }
}
