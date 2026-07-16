import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { CustomerService } from '../../../services/customer.service';
import { BookingService } from '../../../services/booking.service';
import { InvoiceService } from '../../../services/invoice.service';
import { Customer } from '../../../models/customer.model';
import { Booking } from '../../../models/booking.model';
import { InvoiceResponse } from '../../../models/invoice.model';
import { StorageService } from '../../../services/storage.service';
import { LoginResponse } from '../../../models/auth.model';
import { environment } from '../../../../environments/environments';

@Component({
  selector: 'app-customer-dashboard',
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class CustomerDashboard implements OnInit {
  private auth = inject(AuthService);
  private customerService = inject(CustomerService);
  private bookingService = inject(BookingService);
  private invoiceService = inject(InvoiceService);
  private storage = inject(StorageService);
  private cdr = inject(ChangeDetectorRef);

  user: LoginResponse | null = null;
  userId!: number;
  customer?: Customer;
  bookings: Booking[] = [];
  invoices: InvoiceResponse[] = [];
  loading = true;

  imageBaseUrl = environment.imageBaseUrl;

  upcomingBookings: Booking[] = [];
  pastBookings: Booking[] = [];
  currentStay: Booking | null = null;
  totalSpent = 0;
  pendingCount = 0;

  ngOnInit() {
    this.user = this.storage.getUser();
    if (this.user?.userId) {
      this.userId = this.user.userId;
    }

    if (this.userId) {
      this.customerService.getCustomerByUserId(this.userId).subscribe({
        next: (data) => {
          this.customer = data;
          this.cdr.markForCheck();
          if (data.id) {
            this.loadBookings(data.id);
            this.loadInvoices(data.id);
          } else {
            this.loading = false;
            this.cdr.markForCheck();
          }
        },
        error: () => {
          this.loading = false;
          this.cdr.markForCheck();
        },
      });
    } else {
      this.loading = false;
    }
  }

  private loadBookings(customerId: number) {
    this.bookingService.getByCustomer(customerId).subscribe({
      next: (data) => {
        this.bookings = data;
        this.classifyBookings();
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  private loadInvoices(customerId: number) {
    this.invoiceService.getByCustomer(customerId).subscribe({
      next: (data) => {
        this.invoices = data;
        this.totalSpent = data
          .filter((i) => i.status === 'PAID')
          .reduce((sum, i) => sum + (i.netAmount || 0), 0);
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  private classifyBookings() {
    const now = new Date();
    this.upcomingBookings = [];
    this.pastBookings = [];
    this.currentStay = null;
    this.pendingCount = 0;

    for (const b of this.bookings) {
      const checkIn = new Date(b.checkInDate);
      const checkOut = new Date(b.checkOutDate);

      if (b.status === 'CHECKED_IN') {
        this.currentStay = b;
      } else if (b.status === 'PENDING' || b.status === 'CONFIRMED') {
        if (checkIn > now) {
          this.upcomingBookings.push(b);
        }
      } else {
        this.pastBookings.push(b);
      }

      if (b.status === 'PENDING') {
        this.pendingCount++;
      }
    }
  }

  statusClass(status: string): string {
    switch (status) {
      case 'CONFIRMED':
        return 'bg-success';
      case 'PENDING':
        return 'bg-warning text-dark';
      case 'CHECKED_IN':
        return 'bg-info text-dark';
      case 'CHECKED_OUT':
        return 'bg-secondary';
      case 'CANCELLED':
        return 'bg-danger';
      default:
        return 'bg-light text-dark';
    }
  }
}
