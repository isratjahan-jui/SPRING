import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { CustomerService } from '../../../services/customer.service';
import { BookingService } from '../../../services/booking.service';
import { HotelDetailsService } from '../../../services/hotel-details.service';
import { Booking } from '../../../models/booking.model';
import { HotelDetails } from '../../../models/hotel-details.model';

@Component({
  selector: 'app-customer-checkin-checkout',
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './checkin-checkout.html',
  styleUrl: './checkin-checkout.css',
})
export class CustomerCheckinCheckout implements OnInit {
  private auth = inject(AuthService);
  private customerService = inject(CustomerService);
  private bookingService = inject(BookingService);
  private hotelDetailsService = inject(HotelDetailsService);
  private cdr = inject(ChangeDetectorRef);

  customerId: number | null = null;
  loading = true;
  bookings: Booking[] = [];
  hotelDetailsMap: Map<number, HotelDetails> = new Map();

  selectedFile: File | null = null;
  processing = false;
  processingBookingId: number | null = null;
  uploadMessage = '';
  uploadBookingId: number | null = null;

  activeTab: 'checkin' | 'checkout' = 'checkin';

  ngOnInit() {
    const userId = this.auth.getUser()?.userId;
    if (userId) {
      this.customerService.getCustomerByUserId(userId).subscribe({
        next: (c) => {
          this.customerId = c.id ?? null;
          if (this.customerId) {
            this.loadBookings();
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

  private loadBookings() {
    if (!this.customerId) return;
    this.bookingService.getByCustomer(this.customerId).subscribe({
      next: (data) => {
        this.bookings = data.filter(
          (b) => b.status === 'CONFIRMED' || b.status === 'CHECKED_IN',
        );
        this.bookings.forEach((b) => this.loadHotelDetails(b.hotelId));
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  private loadHotelDetails(hotelId: number) {
    if (this.hotelDetailsMap.has(hotelId)) return;
    this.hotelDetailsService.getByHotelId(hotelId).subscribe({
      next: (data) => {
        this.hotelDetailsMap.set(hotelId, data);
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  get checkinBookings(): Booking[] {
    return this.bookings.filter((b) => b.status === 'CONFIRMED');
  }

  get checkoutBookings(): Booking[] {
    return this.bookings.filter((b) => b.status === 'CHECKED_IN');
  }

  isFullOnlinePayment(booking: Booking): boolean {
    const details = this.hotelDetailsMap.get(booking.hotelId);
    if (details?.paymentOption === 'FULL_ONLINE') return true;
    return booking.dueAmount <= 0;
  }

  onFileSelected(event: Event, bookingId: number) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.uploadBookingId = bookingId;
      this.uploadMessage = '';
      this.cdr.markForCheck();
    }
  }

  submitOnlineCheckIn(bookingId: number) {
    if (!this.selectedFile || this.uploadBookingId !== bookingId) return;
    this.processing = true;
    this.processingBookingId = bookingId;

    this.bookingService.onlineCheckIn(bookingId, this.selectedFile).subscribe({
      next: () => {
        const booking = this.bookings.find((b) => b.id === bookingId);
        if (booking) {
          booking.status = 'CHECKED_IN';
          booking.onlineCheckIn = true;
        }
        this.processing = false;
        this.processingBookingId = null;
        this.selectedFile = null;
        this.uploadBookingId = null;
        this.uploadMessage = 'Online check-in successful!';
        this.cdr.markForCheck();
      },
      error: () => {
        this.processing = false;
        this.processingBookingId = null;
        this.uploadMessage = 'Check-in failed. Please try again.';
        this.cdr.markForCheck();
      },
    });
  }

  expressCheckOut(bookingId: number) {
    if (!confirm('Are you sure you want to check out?')) return;
    this.processing = true;
    this.processingBookingId = bookingId;

    this.bookingService.expressCheckOut(bookingId).subscribe({
      next: () => {
        this.bookings = this.bookings.filter((b) => b.id !== bookingId);
        this.processing = false;
        this.processingBookingId = null;
        this.uploadMessage = 'Check-out successful!';
        this.cdr.markForCheck();
      },
      error: () => {
        this.processing = false;
        this.processingBookingId = null;
        this.cdr.markForCheck();
      },
    });
  }

  requestCheckIn(bookingId: number) {
    this.processing = true;
    this.processingBookingId = bookingId;
    this.bookingService.updateStatus(bookingId, 'CHECKED_IN').subscribe({
      next: () => {
        const booking = this.bookings.find((b) => b.id === bookingId);
        if (booking) booking.status = 'CHECKED_IN';
        this.processing = false;
        this.processingBookingId = null;
        this.cdr.markForCheck();
      },
      error: () => {
        this.processing = false;
        this.processingBookingId = null;
        this.cdr.markForCheck();
      },
    });
  }

  requestCheckOut(bookingId: number) {
    this.processing = true;
    this.processingBookingId = bookingId;
    this.bookingService.updateStatus(bookingId, 'CHECKED_OUT').subscribe({
      next: () => {
        this.bookings = this.bookings.filter((b) => b.id !== bookingId);
        this.processing = false;
        this.processingBookingId = null;
        this.cdr.markForCheck();
      },
      error: () => {
        this.processing = false;
        this.processingBookingId = null;
        this.cdr.markForCheck();
      },
    });
  }
}
