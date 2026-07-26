import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CustomerService } from '../../../services/customer.service';
import { BookingService } from '../../../services/booking.service';
import { PaymentService } from '../../../services/payment.service';
import { Customer } from '../../../models/customer.model';
import { Booking } from '../../../models/booking.model';
import { PaymentResponse } from '../../../models/payment.model';
import { environment } from '../../../../environments/environments';

@Component({
  selector: 'app-customer-details',
  imports: [CommonModule, RouterLink],
  templateUrl: './customer-details.html',
  styleUrl: './customer-details.css',
})
export class CustomerDetails implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private customerService = inject(CustomerService);
  private bookingService = inject(BookingService);
  private paymentService = inject(PaymentService);
  private cdr = inject(ChangeDetectorRef);

  customer: Customer | null = null;
  bookings: Booking[] = [];
  payments: PaymentResponse[] = [];
  loading = true;
  imageBaseUrl = environment.imageBaseUrl;

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.loading = false;
      return;
    }

    this.customerService.getCustomerById(id).subscribe({
      next: (data) => {
        this.customer = data;
        this.cdr.markForCheck();
        if (data.id) {
          this.bookingService.getByCustomer(data.id).subscribe({
            next: (b) => {
              this.bookings = b;
              this.cdr.markForCheck();
            },
            error: () => {},
          });
          this.paymentService.getByCustomer(data.id).subscribe({
            next: (p) => {
              this.payments = p;
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
  }

  goBack() {
    this.router.navigate(['/admin/customers']);
  }

  getImageUrl(image?: string): string {
    return image ? `${this.imageBaseUrl}/customers/${image}` : '';
  }

  getBookingStatusBadge(status: string): string {
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

  getPaymentStatusBadge(status: string): string {
    switch (status) {
      case 'PAID':
        return 'bg-success';
      case 'PENDING':
        return 'bg-warning text-dark';
      case 'REFUNDED':
        return 'bg-info';
      default:
        return 'bg-secondary';
    }
  }
}
