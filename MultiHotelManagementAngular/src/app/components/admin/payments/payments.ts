import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PaymentService } from '../../../services/payment.service';
import { PaymentRequest, PaymentResponse } from '../../../models/payment.model';

@Component({
  selector: 'app-admin-payments',
  imports: [CommonModule, FormsModule],
  templateUrl: './payments.html',
  styleUrl: './payments.css',
})
export class AdminPayments implements OnInit {
  private paymentService = inject(PaymentService);
  private cdr = inject(ChangeDetectorRef);

  payments: PaymentResponse[] = [];
  loading = true;

  showForm = false;
  editing = false;
  selectedId: number | null = null;
  form: PaymentRequest = {
    method: '',
    amount: 0,
    status: 'PENDING',
    bookingId: 0,
    customerId: undefined,
    extraServiceId: undefined,
  };

  refundingId: number | null = null;

  ngOnInit() {
    this.loadPayments();
  }

  loadPayments() {
    this.loading = true;
    this.paymentService.getAll().subscribe({
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
  }

  openCreate() {
    this.editing = false;
    this.selectedId = null;
    this.form = {
      method: '',
      amount: 0,
      status: 'PAID',
      bookingId: 0,
      customerId: undefined,
      extraServiceId: undefined,
    };
    this.showForm = true;
    this.cdr.markForCheck();
  }

  openEdit(p: PaymentResponse) {
    this.editing = true;
    this.selectedId = p.id;
    this.form = {
      method: p.method,
      amount: p.amount,
      status: p.status,
      bookingId: p.bookingId,
      customerId: p.customerId,
      extraServiceId: p.extraServiceId,
    };
    this.showForm = true;
    this.cdr.markForCheck();
  }

  closeForm() {
    this.showForm = false;
    this.cdr.markForCheck();
  }

  save() {
    if (this.editing && this.selectedId) {
      this.paymentService.update(this.selectedId, this.form).subscribe({
        next: () => {
          this.closeForm();
          this.loadPayments();
        },
        error: (err) => {
          console.error('Update failed', err);
        },
      });
    } else {
      this.paymentService.create(this.form).subscribe({
        next: () => {
          this.closeForm();
          this.loadPayments();
        },
        error: (err) => {
          console.error('Create failed', err);
        },
      });
    }
  }

  confirmRefund(p: PaymentResponse) {
    if (!confirm(`Refund payment BDT ${p.amount} for booking #${p.bookingId}?`)) return;
    this.refundingId = p.id;
    this.paymentService.refund(p.bookingId).subscribe({
      next: () => {
        this.refundingId = null;
        this.loadPayments();
      },
      error: () => {
        this.refundingId = null;
        this.cdr.markForCheck();
      },
    });
  }

  confirmDelete(id: number) {
    if (!confirm('Are you sure you want to delete this payment?')) return;
    this.paymentService.delete(id).subscribe({
      next: () => this.loadPayments(),
      error: (err) => console.error('Delete failed', err),
    });
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
