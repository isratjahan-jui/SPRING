import { ChangeDetectorRef, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { BookingService } from '../../../services/booking.service';
import { HotelDetailsService } from '../../../services/hotel-details.service';
import { Booking } from '../../../models/booking.model';
import { HotelDetails as HotelDetailsModel } from '../../../models/hotel-details.model';

@Component({
  selector: 'app-booking-details',
  imports: [CommonModule, RouterLink],
  templateUrl: './booking-details.html',
  styleUrl: './booking-details.css',
})
export class BookingDetails implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private bookingService = inject(BookingService);
  private hotelDetailsService = inject(HotelDetailsService);
  private cdr = inject(ChangeDetectorRef);

  booking?: Booking;
  hotelDetails?: HotelDetailsModel;
  loading = true;
  error = '';
  processing = false;

  selectedFile: File | null = null;
  uploadMessage = '';
  qrCodeUrl = '';

  countdownText = '';
  isDeadlinePassed = false;
  private countdownInterval: ReturnType<typeof setInterval> | null = null;

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.error = 'Invalid booking ID.';
      this.loading = false;
      return;
    }

    this.loadBooking(id);
  }

  ngOnDestroy() {
    if (this.countdownInterval) {
      clearInterval(this.countdownInterval);
    }
  }

  private loadBooking(id: number) {
    this.bookingService.getById(id).subscribe({
      next: (data) => {
        this.booking = data;
        this.loading = false;
        this.generateQrCodeUrl();
        this.startCountdown();
        this.loadHotelDetails(data.hotelId);
        this.cdr.markForCheck();
      },
      error: () => {
        this.error = 'Failed to load booking details.';
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  private loadHotelDetails(hotelId: number) {
    this.hotelDetailsService.getByHotelId(hotelId).subscribe({
      next: (data) => {
        this.hotelDetails = data;
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  private startCountdown() {
    if (this.countdownInterval) {
      clearInterval(this.countdownInterval);
    }
    this.updateCountdown();
    this.countdownInterval = setInterval(() => {
      this.updateCountdown();
      this.cdr.markForCheck();
    }, 1000);
  }

  private updateCountdown() {
    if (!this.booking?.cancellationDeadline) {
      this.countdownText = '';
      this.isDeadlinePassed = false;
      return;
    }

    const now = Date.now();
    const deadline = new Date(this.booking.cancellationDeadline).getTime();
    const diff = deadline - now;

    if (diff <= 0) {
      this.countdownText = 'Deadline passed';
      this.isDeadlinePassed = true;
      return;
    }

    this.isDeadlinePassed = false;
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    const seconds = Math.floor((diff % (1000 * 60)) / 1000);

    this.countdownText =
      days > 0 ? `${days}d ${hours}h ${minutes}m ${seconds}s` : `${hours}h ${minutes}m ${seconds}s`;
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.uploadMessage = '';
      this.cdr.markForCheck();
    }
  }

  submitOnlineCheckIn() {
    if (!this.booking || !this.selectedFile) return;
    this.processing = true;
    this.uploadMessage = '';

    this.bookingService.onlineCheckIn(this.booking.id, this.selectedFile).subscribe({
      next: (data) => {
        this.booking = data;
        this.processing = false;
        this.selectedFile = null;
        this.uploadMessage = 'Online check-in successful! Your digital key is ready.';
        this.generateQrCodeUrl();
        this.cdr.markForCheck();
      },
      error: () => {
        this.processing = false;
        this.uploadMessage = 'Check-in failed. Please try again.';
        this.cdr.markForCheck();
      },
    });
  }

  expressCheckOut() {
    if (!this.booking) return;
    this.processing = true;

    this.bookingService.expressCheckOut(this.booking.id).subscribe({
      next: (data) => {
        this.booking = data;
        this.processing = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.processing = false;
        this.cdr.markForCheck();
      },
    });
  }

  private generateQrCodeUrl() {
    if (this.booking?.digitalKey) {
      this.qrCodeUrl = `https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${encodeURIComponent(this.booking.digitalKey)}`;
    } else {
      this.qrCodeUrl = '';
    }
  }

  get paidAmount(): number {
    if (!this.booking) return 0;
    return this.booking.totalAmount - this.booking.dueAmount;
  }

  cancelBooking() {
    if (!this.booking) return;

    let msg = 'Are you sure you want to cancel this booking? This action cannot be undone.';
    if (this.hotelDetails?.cancellationDepositRefundable) {
      const policy = this.hotelDetails.cancellationDepositRefundable;
      if (policy === 'NON_REFUNDABLE') {
        msg =
          'This hotel has a NON-REFUNDABLE deposit policy. You will NOT receive any refund. Are you sure?';
      } else if (policy === 'PARTIAL_REFUND') {
        msg =
          'This hotel offers PARTIAL REFUND (50%). 50% of your deposit will be refunded, 50% retained as commission. Proceed?';
      } else if (policy === 'CONDITIONAL_REFUND') {
        if (this.isDeadlinePassed) {
          msg =
            'The free cancellation period has PASSED. Late cancellation: only 30% refund. Are you sure?';
        } else {
          msg = 'You are within the free cancellation period. Full refund will be issued. Proceed?';
        }
      } else if (policy === 'FULL_REFUND') {
        msg = 'Full refund will be issued to your wallet. Are you sure you want to cancel?';
      }
    }

    if (!confirm(msg)) return;

    this.processing = true;
    this.bookingService.cancelBooking(this.booking.id).subscribe({
      next: (data) => {
        this.booking = data;
        this.processing = false;
        this.loadHotelDetails(data.hotelId);
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.processing = false;
        alert(err.error?.message || 'Cancellation failed. Please try again.');
        this.cdr.markForCheck();
      },
    });
  }

  requestCheckIn() {
    if (!this.booking) return;
    this.processing = true;
    this.bookingService.updateStatus(this.booking.id, 'CHECKED_IN').subscribe({
      next: () => {
        if (this.booking) this.booking.status = 'CHECKED_IN';
        this.processing = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.processing = false;
        this.cdr.markForCheck();
      },
    });
  }

  requestCheckOut() {
    if (!this.booking) return;
    this.processing = true;
    this.bookingService.updateStatus(this.booking.id, 'CHECKED_OUT').subscribe({
      next: () => {
        if (this.booking) this.booking.status = 'CHECKED_OUT';
        this.processing = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.processing = false;
        this.cdr.markForCheck();
      },
    });
  }

  printVoucher() {
    window.print();
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
