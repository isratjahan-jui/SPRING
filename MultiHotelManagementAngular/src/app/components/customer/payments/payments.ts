import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PaymentService } from '../../../services/payment.service';
import { AuthService } from '../../../services/auth.service';
import { CustomerService } from '../../../services/customer.service';
import { PaymentResponse } from '../../../models/payment.model';

@Component({
  selector: 'app-customer-payments',
  imports: [CommonModule, RouterLink],
  templateUrl: './payments.html',
  styleUrl: './payments.css',
})
export class CustomerPayments implements OnInit {
  private paymentService = inject(PaymentService);
  private auth = inject(AuthService);
  private customerService = inject(CustomerService);
  private cdr = inject(ChangeDetectorRef);

  payments: PaymentResponse[] = [];
  loading = true;

  ngOnInit() {
    const userId = this.auth.getUser()?.userId;
    if (userId) {
      this.customerService.getCustomerByUserId(userId).subscribe({
        next: (customer) => {
          if (customer.id) {
            this.paymentService.getByCustomer(customer.id).subscribe({
              next: (data) => {
                this.payments = data;
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

  getStatusClass(status: string): string {
    switch (status) {
      case 'PAID':
        return 'bg-success';
      case 'PENDING':
        return 'bg-warning text-dark';
      case 'FAILED':
        return 'bg-danger';
      case 'REFUNDED':
        return 'bg-info text-dark';
      case 'UNPAID':
        return 'bg-secondary';
      default:
        return 'bg-secondary';
    }
  }
}
