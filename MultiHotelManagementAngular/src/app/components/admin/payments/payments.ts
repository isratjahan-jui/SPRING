import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PaymentService } from '../../../services/payment.service';
import { PaymentResponse, PaymentRevenueSummary } from '../../../models/payment.model';

@Component({
  selector: 'app-admin-payments',
  imports: [CommonModule],
  templateUrl: './payments.html',
  styleUrl: './payments.css',
})
export class AdminPayments implements OnInit {
  private paymentService = inject(PaymentService);
  private cdr = inject(ChangeDetectorRef);

  payments: PaymentResponse[] = [];
  summary: PaymentRevenueSummary[] = [];
  totalRevenue = 0;
  loading = true;

  ngOnInit() {
    this.paymentService.getAll().subscribe({
      next: (data) => {
        this.payments = data;
        this.cdr.markForCheck();
      },
      error: () => {},
    });
    this.paymentService.getSummary().subscribe({
      next: (data) => {
        this.summary = data;
        this.totalRevenue = data.reduce((s, p) => s + p.totalPaymentAmount, 0);
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }
}
