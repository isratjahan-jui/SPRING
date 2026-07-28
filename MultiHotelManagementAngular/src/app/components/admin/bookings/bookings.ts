import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookingService } from '../../../services/booking.service';
import { Booking } from '../../../models/booking.model';

@Component({
  selector: 'app-admin-bookings',
  imports: [CommonModule, FormsModule],
  templateUrl: './bookings.html',
  styleUrl: './bookings.css',
})
export class AdminBookings implements OnInit {
  private bookingService = inject(BookingService);
  private cdr = inject(ChangeDetectorRef);

  bookings: Booking[] = [];
  allBookings: Booking[] = [];
  loading = true;
  filterStatus = '';

  ngOnInit() {
    this.loadBookings();
  }

  loadBookings() {
    this.loading = true;
    this.bookingService.getAll().subscribe({
      next: (data) => {
        this.allBookings = data;
        this.bookings = data;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  filterByStatus() {
    if (!this.filterStatus) {
      this.bookings = this.allBookings;
    } else {
      this.bookings = this.allBookings.filter((b) => b.status === this.filterStatus);
    }
  }

  get totalCount(): number {
    return this.allBookings.length;
  }

  get expiredCount(): number {
    return this.allBookings.filter((b) => b.status === 'EXPIRED').length;
  }

  get noShowCount(): number {
    return this.allBookings.filter((b) => b.status === 'NO_SHOW').length;
  }

  get activeCount(): number {
    return this.allBookings.filter(
      (b) => b.status === 'PENDING' || b.status === 'CONFIRMED' || b.status === 'CHECKED_IN',
    ).length;
  }

  getStatusBadgeClass(status: string): string {
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
      case 'NO_SHOW':
        return 'bg-dark';
      case 'EXPIRED':
        return 'bg-orange text-white';
      default:
        return 'bg-secondary';
    }
  }

  getDisplayStatus(status: string): string {
    if (status === 'NO_SHOW') return 'No Show';
    if (status === 'EXPIRED') return 'Expired';
    return status;
  }
}
