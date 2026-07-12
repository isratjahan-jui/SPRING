import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Booking } from '../../../models/booking.model';
import { Hotel } from '../../../models/hotel.model';
import { BookingService } from '../../../services/booking.service';
import { HotelService } from '../../../services/hotel.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-owner-bookings',
  imports: [CommonModule, FormsModule],
  templateUrl: './bookings.html',
  styleUrl: './bookings.css',
})
export class OwnerBookings implements OnInit {
  hotels: Hotel[] = [];
  bookings: Booking[] = [];
  allBookings: Booking[] = [];
  selectedHotelId = 0;
  filterStatus = '';
  extraChargeAmount: { [bookingId: number]: number } = {};
  private authService = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);

  constructor(
    private bookingService: BookingService,
    private hotelService: HotelService,
  ) {}

  ngOnInit() {
    const ownerId = this.authService.getUser()?.ownerId;
    if (ownerId) {
      this.hotelService.getByOwner(ownerId).subscribe({
        next: (data) => {
          this.hotels = data;
          this.cdr.markForCheck();
        },
        error: () => alert('Failed to load hotels'),
      });
    }
  }

  onHotelChange() {
    this.bookings = [];
    this.allBookings = [];
    this.filterStatus = '';
    this.extraChargeAmount = {};
    if (this.selectedHotelId) {
      this.bookingService.getByHotel(this.selectedHotelId).subscribe({
        next: (data) => {
          this.allBookings = data;
          this.bookings = data;
          this.cdr.markForCheck();
        },
        error: () => alert('Failed to load bookings'),
      });
    }
  }

  filterByStatus() {
    if (!this.filterStatus) {
      this.bookings = this.allBookings;
    } else {
      this.bookings = this.allBookings.filter((b) => b.status === this.filterStatus);
    }
  }

  confirmBooking(booking: Booking) {
    if (!confirm(`Confirm booking #${booking.id}?`)) return;
    this.bookingService.updateStatus(booking.id, 'CONFIRMED').subscribe({
      next: () => this.onHotelChange(),
      error: () => alert('Failed to confirm booking'),
    });
  }

  markNoShow(booking: Booking) {
    if (!confirm(`Mark booking #${booking.id} as No-Show? The guest did not arrive.`)) return;
    this.bookingService.markNoShow(booking.id).subscribe({
      next: () => this.onHotelChange(),
      error: () => alert('Failed to mark as no-show'),
    });
  }

  checkIn(booking: Booking) {
    if (!confirm(`Check-in guest ${booking.customerName}?`)) return;
    this.bookingService.updateStatus(booking.id, 'CHECKED_IN').subscribe({
      next: () => this.onHotelChange(),
      error: () => alert('Failed to check-in'),
    });
  }

  checkOutWithExtraCharges(booking: Booking) {
    const extra = this.extraChargeAmount[booking.id] || 0;
    let msg = `Check-out guest ${booking.customerName}?`;
    if (extra > 0) {
      msg = `Add extra charges BDT ${extra} and check-out guest ${booking.customerName}?`;
    }
    if (!confirm(msg)) return;

    const doCheckout = () => {
      this.bookingService.updateStatus(booking.id, 'CHECKED_OUT').subscribe({
        next: () => this.onHotelChange(),
        error: () => alert('Failed to check-out'),
      });
    };

    if (extra > 0) {
      this.bookingService.addExtraCharges(booking.id, extra).subscribe({
        next: () => doCheckout(),
        error: () => alert('Failed to add extra charges'),
      });
    } else {
      doCheckout();
    }
  }

  cancelBooking(booking: Booking) {
    if (!confirm(`Cancel booking #${booking.id}?`)) return;
    this.bookingService.updateStatus(booking.id, 'CANCELLED').subscribe({
      next: () => this.onHotelChange(),
      error: () => alert('Failed to cancel booking'),
    });
  }
}
