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
    if (!confirm('Confirm this booking?')) return;
    this.bookingService.updateStatus(booking.id, 'CONFIRMED').subscribe({
      next: () => this.onHotelChange(),
      error: () => alert('Failed to confirm booking'),
    });
  }

  checkIn(booking: Booking) {
    if (!confirm('Check-in this guest?')) return;
    this.bookingService.updateStatus(booking.id, 'CHECKED_IN').subscribe({
      next: () => this.onHotelChange(),
      error: () => alert('Failed to check-in'),
    });
  }

  checkOut(booking: Booking) {
    if (!confirm('Check-out this guest?')) return;
    this.bookingService.updateStatus(booking.id, 'CHECKED_OUT').subscribe({
      next: () => this.onHotelChange(),
      error: () => alert('Failed to check-out'),
    });
  }

  cancelBooking(booking: Booking) {
    if (!confirm('Cancel this booking?')) return;
    this.bookingService.updateStatus(booking.id, 'CANCELLED').subscribe({
      next: () => this.onHotelChange(),
      error: () => alert('Failed to cancel booking'),
    });
  }
}
