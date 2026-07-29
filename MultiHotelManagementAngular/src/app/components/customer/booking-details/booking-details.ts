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
    if (!this.booking) return;
    const b = this.booking;
    const pw = window.open('', '_blank');
    if (!pw) return;
    pw.document.write(`
      <!DOCTYPE html>
      <html><head><title>Booking Voucher #${b.id}</title>
      <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Arial, sans-serif; padding: 40px; color: #333; }
        .voucher-box { max-width: 800px; margin: 0 auto; border: 1px solid #ddd; padding: 30px; }
        .header { display: flex; justify-content: space-between; align-items: flex-start; border-bottom: 3px solid #2563eb; padding-bottom: 20px; margin-bottom: 25px; }
        .header h1 { font-size: 28px; color: #2563eb; }
        .header .meta { text-align: right; }
        .header .meta p { margin: 2px 0; font-size: 14px; }
        .header .meta .ref { font-size: 18px; font-weight: bold; color: #2563eb; }
        .section { margin-bottom: 20px; }
        .section h3 { font-size: 14px; text-transform: uppercase; color: #666; margin-bottom: 8px; border-bottom: 1px solid #eee; padding-bottom: 5px; }
        .grid { display: grid; grid-template-columns: 1fr 1fr; gap: 5px 30px; }
        .grid p { font-size: 14px; line-height: 1.6; }
        .grid .label { color: #888; }
        table { width: 100%; border-collapse: collapse; margin: 15px 0; }
        th { background: #f8f9fa; text-align: left; padding: 8px 12px; font-size: 13px; text-transform: uppercase; color: #666; border-bottom: 2px solid #ddd; }
        td { padding: 8px 12px; border-bottom: 1px solid #eee; font-size: 14px; }
        .text-right { text-align: right; }
        .total-row { font-size: 16px; font-weight: bold; border-top: 2px solid #333; }
        .status { display: inline-block; padding: 4px 12px; border-radius: 4px; font-weight: bold; font-size: 12px; color: white; }
        .status-CONFIRMED { background: #16a34a; }
        .status-PENDING { background: #eab308; color: #333; }
        .status-CHECKED_IN { background: #0891b2; color: #333; }
        .status-CHECKED_OUT { background: #6b7280; }
        .status-CANCELLED { background: #dc2626; }
        .status-NO_SHOW { background: #1f2937; }
        .footer { text-align: center; margin-top: 30px; padding-top: 15px; border-top: 2px solid #eee; color: #888; font-size: 13px; }
        @media print { body { padding: 20px; } .voucher-box { border: none; } }
      </style></head>
      <body>
        <div class="voucher-box">
          <div class="header">
            <div>
              <h1>BOOKING VOUCHER</h1>
              <p style="color:#666; font-size:14px;">TripNest Hotel Management</p>
            </div>
            <div class="meta">
              <p class="ref">Booking #${b.id}</p>
              <p>Status: <span class="status status-${b.status}">${b.status}</span></p>
            </div>
          </div>

          <div class="section">
            <h3>Hotel Information</h3>
            <div class="grid">
              <p><span class="label">Hotel:</span> <strong>${b.hotelName || 'N/A'}</strong></p>
              <p><span class="label">Room Type:</span> ${b.roomType || 'N/A'}</p>
            </div>
          </div>

          <div class="section">
            <h3>Guest Information</h3>
            <div class="grid">
              <p><span class="label">Name:</span> ${b.customerName || 'N/A'}</p>
              <p><span class="label">Guests:</span> ${b.totalGuests}</p>
              <p><span class="label">Rooms:</span> ${b.numberOfRooms}</p>
            </div>
          </div>

          <div class="section">
            <h3>Stay Details</h3>
            <div class="grid">
              <p><span class="label">Check-in:</span> <strong>${b.checkInDate ? new Date(b.checkInDate).toLocaleDateString('en-BD', { weekday: 'long', day: '2-digit', month: 'long', year: 'numeric' }) : 'N/A'}</strong></p>
              <p><span class="label">Check-out:</span> <strong>${b.checkOutDate ? new Date(b.checkOutDate).toLocaleDateString('en-BD', { weekday: 'long', day: '2-digit', month: 'long', year: 'numeric' }) : 'N/A'}</strong></p>
            </div>
          </div>

          <div class="section">
            <h3>Payment Summary</h3>
            <table>
              <tbody>
                <tr><td>Total Amount</td><td class="text-right fw-bold">BDT ${b.totalAmount || 0}</td></tr>
                <tr><td>Paid Amount</td><td class="text-right fw-bold">BDT ${b.totalAmount - b.dueAmount}</td></tr>
                <tr class="total-row"><td>Due at Hotel</td><td class="text-right">BDT ${b.dueAmount || 0}</td></tr>
              </tbody>
            </table>
          </div>

          ${b.digitalKey ? '<div class="section"><h3>Digital Key</h3><p style="font-size:18px; font-weight:bold; color:#2563eb;">' + b.digitalKey + '</p></div>' : ''}

          <div class="section">
            <h3>Important Information</h3>
            <ul style="font-size:13px; color:#666; padding-left:20px; line-height:1.8;">
              <li>Please carry a valid government ID (NID/Passport) for check-in.</li>
              <li>Check-in starts from 12:00 PM. Check-out by 11:00 AM.</li>
              ${b.cancellationDeadline ? '<li>Free cancellation deadline: ' + new Date(b.cancellationDeadline).toLocaleString() + '</li>' : '<li>Free cancellation up to 24 hours before check-in.</li>'}
            </ul>
          </div>

          <div class="footer">
            <p>TripNest Hotel Management System</p>
          </div>
        </div>
        <script>window.onload = function() { window.print(); }</script>
      </body></html>
    `);
    pw.document.close();
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
      case 'NO_SHOW':
        return 'bg-dark';
      case 'EXPIRED':
        return 'bg-orange text-white';
      default:
        return 'bg-light text-dark';
    }
  }
}
