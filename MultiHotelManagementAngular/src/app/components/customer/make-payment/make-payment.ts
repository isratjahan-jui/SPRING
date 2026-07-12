import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { PaymentService } from '../../../services/payment.service';
import { BookingService } from '../../../services/booking.service';
import { AuthService } from '../../../services/auth.service';
import { CustomerService } from '../../../services/customer.service';
import { Booking } from '../../../models/booking.model';

@Component({
  selector: 'app-make-payment',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './make-payment.html',
  styleUrl: './make-payment.css',
})
export class MakePayment implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private paymentService = inject(PaymentService);
  private bookingService = inject(BookingService);
  private auth = inject(AuthService);
  private customerService = inject(CustomerService);
  private cdr = inject(ChangeDetectorRef);

  booking: Booking | null = null;
  customerId: number | null = null;
  loading = true;
  submitting = false;
  success = false;
  error = '';

  methods = [
    { id: 'CASH', label: 'Cash' },
    { id: 'CARD', label: 'Card (Visa/MasterCard)' },
    { id: 'BKASH', label: 'bKash' },
    { id: 'NAGAD', label: 'Nagad' },
    { id: 'ROCKET', label: 'Rocket' },
    { id: 'SSLCOMMERZ', label: 'SSLCommerz' },
    { id: 'STRIPE', label: 'Stripe' },
    { id: 'PAYPAL', label: 'PayPal' },
  ];

  selectedMethod = 'CARD';
  payAmount = 0;

  ngOnInit() {
    const bookingId = Number(this.route.snapshot.paramMap.get('bookingId'));
    if (!bookingId) {
      this.error = 'Invalid booking';
      this.loading = false;
      return;
    }

    const userId = this.auth.getUser()?.userId;
    if (userId) {
      this.customerService.getCustomerByUserId(userId).subscribe({
        next: (c) => {
          this.customerId = c.id ?? null;
          this.cdr.markForCheck();
        },
      });
    }

    this.bookingService.getById(bookingId).subscribe({
      next: (data) => {
        this.booking = data;
        this.payAmount = data.dueAmount;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.error = 'Failed to load booking';
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  submitPayment() {
    if (!this.booking || this.payAmount <= 0) return;
    this.submitting = true;
    this.error = '';

    this.paymentService
      .create({
        method: this.selectedMethod,
        amount: this.payAmount,
        status: 'PAID',
        bookingId: this.booking.id,
        customerId: this.customerId ?? undefined,
      })
      .subscribe({
        next: () => {
          this.success = true;
          this.submitting = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.error = err.error?.message || 'Payment failed. Please try again.';
          this.submitting = false;
          this.cdr.markForCheck();
        },
      });
  }

  goToBookings() {
    this.router.navigate(['/customer/bookings']);
  }
}
