import { ChangeDetectorRef, Component, inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../services/auth.service';
import { CustomerService } from '../../../services/customer.service';
import { NotificationService, NotificationItem } from '../../../services/notification.service';

@Component({
  selector: 'app-customer-notifications',
  imports: [CommonModule],
  templateUrl: './notifications.html',
  styleUrl: './notifications.css',
})
export class CustomerNotifications implements OnInit, OnDestroy {
  private authService = inject(AuthService);
  private customerService = inject(CustomerService);
  private notificationService = inject(NotificationService);
  private cdr = inject(ChangeDetectorRef);

  notifications: NotificationItem[] = [];
  loading = true;
  customerId: number | null = null;

  ngOnInit() {
    const user = this.authService.getUser();
    if (user) {
      this.customerService.getCustomerByUserId(user.userId).subscribe({
        next: (customer) => {
          this.customerId = customer.id!;
        },
        error: () => {},
      });
    }

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

  getTypeIcon(type: string): string {
    switch (type?.toUpperCase()) {
      case 'BOOKING':
        return 'bi-calendar-check';
      case 'PAYMENT':
        return 'bi-credit-card';
      case 'CHECKIN':
        return 'bi-box-arrow-in-right';
      case 'CHECKOUT':
        return 'bi-box-arrow-right';
      case 'REVIEW':
        return 'bi-star';
      case 'SUPPORT':
        return 'bi-headset';
      case 'PROMOTION':
        return 'bi-megaphone';
      case 'SYSTEM':
        return 'bi-gear';
      default:
        return 'bi-bell';
    }
  }

  getTypeIconClass(type: string): string {
    switch (type?.toUpperCase()) {
      case 'BOOKING':
        return 'icon-booking';
      case 'PAYMENT':
        return 'icon-payment';
      case 'CHECKIN':
        return 'icon-checkin';
      case 'CHECKOUT':
        return 'icon-checkout';
      case 'REVIEW':
        return 'icon-review';
      case 'SUPPORT':
        return 'icon-support';
      case 'PROMOTION':
        return 'icon-promotion';
      case 'SYSTEM':
        return 'icon-system';
      default:
        return 'icon-default';
    }
  }
}
