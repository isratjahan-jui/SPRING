import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { NotificationService, NotificationItem } from '../../../services/notification.service';
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-notification-dropdown',
  imports: [DatePipe],
  templateUrl: './notification-dropdown.html',
  styleUrl: './notification-dropdown.css',
})
export class NotificationDropdown {
  private notificationService = inject(NotificationService);
  isOpen = signal(false);

  notifications = toSignal(this.notificationService.notifications$, {
    initialValue: [] as NotificationItem[],
  });
  unreadCount = toSignal(this.notificationService.unreadCount$, { initialValue: 0 });

  toggle() {
    this.isOpen.update((v) => !v);
    if (this.isOpen()) {
      this.notificationService.loadNotifications();
    }
  }

  close() {
    this.isOpen.set(false);
  }

  markAsRead(id: number) {
    this.notificationService.markAsRead(id);
  }

  markAllAsRead() {
    this.notificationService.markAllAsRead();
  }

  delete(id: number, event: MouseEvent) {
    event.stopPropagation();
    this.notificationService.delete(id);
  }
}
