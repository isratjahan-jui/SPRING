import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { BookingService } from '../../../services/booking.service';
import { CustomerService } from '../../../services/customer.service';
import { AuthService } from '../../../services/auth.service';
import { Booking } from '../../../models/booking.model';

@Component({
  selector: 'app-customer-bookings',
  imports: [CommonModule, RouterLink],
  templateUrl: './bookings.html',
  styleUrl: './bookings.css',
})
export class CustomerBookings implements OnInit {
  private auth = inject(AuthService);
  private customerService = inject(CustomerService);
  private bookingService = inject(BookingService);
  private cdr = inject(ChangeDetectorRef);

  bookings: Booking[] = [];
  loading = true;
  cancellingId: number | null = null;
  showCancelModal = false;
  cancelTargetId: number | null = null;

  ngOnInit() {
    const userId = this.auth.getUser()?.userId;
    if (userId) {
      this.customerService.getCustomerByUserId(userId).subscribe({
        next: (customer) => {
          if (customer.id) {
            this.bookingService.getByCustomer(customer.id).subscribe({
              next: (data) => {
                this.bookings = data;
                this.loading = false;
                this.cdr.markForCheck();
              },
              error: () => {
                this.loading = false;
                this.cdr.markForCheck();
              },
            });
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

  openCancelModal(id: number) {
    this.cancelTargetId = id;
    this.showCancelModal = true;
  }

  closeCancelModal() {
    this.showCancelModal = false;
    this.cancelTargetId = null;
  }

  confirmCancel() {
    if (!this.cancelTargetId) return;
    this.showCancelModal = false;
    this.cancellingId = this.cancelTargetId;
    this.bookingService.updateStatus(this.cancelTargetId, 'CANCELLED').subscribe({
      next: () => {
        const b = this.bookings.find((x) => x.id === this.cancelTargetId);
        if (b) b.status = 'CANCELLED';
        this.cancellingId = null;
        this.cancelTargetId = null;
        this.cdr.markForCheck();
      },
      error: () => {
        this.cancellingId = null;
        this.cdr.markForCheck();
      },
    });
  }

  requestCheckIn(id: number) {
    if (
      !confirm(
        'By requesting check-in, you confirm that you have arrived at the hotel and are ready to check in. Continue?',
      )
    )
      return;
    this.cancellingId = id;
    this.bookingService.updateStatus(id, 'CHECKED_IN').subscribe({
      next: () => {
        const b = this.bookings.find((x) => x.id === id);
        if (b) b.status = 'CHECKED_IN';
        this.cancellingId = null;
        this.cdr.markForCheck();
      },
      error: () => {
        this.cancellingId = null;
        this.cdr.markForCheck();
      },
    });
  }

  requestCheckOut(id: number) {
    if (
      !confirm(
        'By requesting check-out, you confirm that you are ready to leave. Any extra charges (minibar, food) will be settled at the hotel. Continue?',
      )
    )
      return;
    this.cancellingId = id;
    this.bookingService.updateStatus(id, 'CHECKED_OUT').subscribe({
      next: () => {
        const b = this.bookings.find((x) => x.id === id);
        if (b) b.status = 'CHECKED_OUT';
        this.cancellingId = null;
        this.cdr.markForCheck();
      },
      error: () => {
        this.cancellingId = null;
        this.cdr.markForCheck();
      },
    });
  }
}
