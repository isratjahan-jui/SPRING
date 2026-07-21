import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { PaymentService } from '../../../services/payment.service';
import { BookingService } from '../../../services/booking.service';
import { AuthService } from '../../../services/auth.service';
import { CustomerService } from '../../../services/customer.service';
import { Booking } from '../../../models/booking.model';

interface PaymentMethod {
  id: string;
  label: string;
  icon: string;
  description: string;
}

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

  selectedMethod: string = '';
  amount: number = 0;
  payFullAmount: boolean = true;

  methods: PaymentMethod[] = [
    {
      id: 'SSLCOMMERZ',
      label: 'SSLCommerz',
      icon: '🔒',
      description: 'Pay via SSLCommerz secure gateway (bKash, Nagad, Rocket, Cards)',
    },
    {
      id: 'BKASH',
      label: 'bKash',
      icon: '💳',
      description: 'Send money to our bKash merchant number',
    },
    {
      id: 'NAGAD',
      label: 'Nagad',
      icon: '💳',
      description: 'Send money to our Nagad merchant number',
    },
  ];

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
        this.amount = data.dueAmount;
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

  selectMethod(methodId: string) {
    this.selectedMethod = methodId;
    this.error = '';
  }

  onAmountChange() {
    if (!this.booking) return;
    if (this.payFullAmount) {
      this.amount = this.booking.dueAmount;
    }
  }

  submitPayment() {
    if (!this.booking || !this.selectedMethod) {
      this.error = 'Please select a payment method.';
      return;
    }
    if (this.amount <= 0) {
      this.error = 'Amount must be greater than zero.';
      return;
    }
    if (this.amount > this.booking.dueAmount) {
      this.error = `Amount cannot exceed due amount of BDT ${this.booking.dueAmount}.`;
      return;
    }

    this.submitting = true;
    this.error = '';

    if (this.selectedMethod === 'SSLCOMMERZ') {
      this.paymentService.initiateSslCommerz(this.booking.id).subscribe({
        next: (response) => {
          this.submitting = false;
          this.cdr.markForCheck();
          window.location.href = response.gatewayPageUrl;
        },
        error: (err) => {
          this.error = err.error?.message || 'Payment initialization failed. Please try again.';
          this.submitting = false;
          this.cdr.markForCheck();
        },
      });
    } else {
      this.paymentService
        .create({
          method: this.selectedMethod,
          amount: this.amount,
          status: this.selectedMethod === 'CASH' ? 'PENDING' : 'PAID',
          bookingId: this.booking.id,
          customerId: this.customerId || undefined,
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
  }

  goToBookings() {
    this.router.navigate(['/customer/bookings']);
  }
}
