import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { CustomerService } from '../../../services/customer.service';
import {
  CustomerSupportService,
  SupportTicketResponse,
} from '../../../services/customer-support.service';
import { Customer } from '../../../models/customer.model';

@Component({
  selector: 'app-customer-support',
  imports: [CommonModule, FormsModule],
  templateUrl: './support.html',
  styleUrl: './support.css',
})
export class CustomerSupport implements OnInit {
  private auth = inject(AuthService);
  private customerService = inject(CustomerService);
  private supportService = inject(CustomerSupportService);
  private cdr = inject(ChangeDetectorRef);

  customer?: Customer;
  tickets: SupportTicketResponse[] = [];
  loading = true;
  submitting = false;
  successMsg = '';
  errorMsg = '';

  showForm = false;
  form = { subject: '', description: '', priority: 'MEDIUM' };

  ngOnInit() {
    const user = this.auth.getUser();
    if (user?.userId) {
      this.customerService.getCustomerByUserId(user.userId).subscribe({
        next: (data) => {
          this.customer = data;
          if (data.id) this.loadTickets(data.id);
          else this.loading = false;
          this.cdr.markForCheck();
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

  private loadTickets(customerId: number) {
    this.supportService.getByCustomer(customerId).subscribe({
      next: (data) => {
        this.tickets = data;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  openForm() {
    this.showForm = true;
    this.form = { subject: '', description: '', priority: 'MEDIUM' };
    this.cdr.markForCheck();
  }

  closeForm() {
    this.showForm = false;
    this.cdr.markForCheck();
  }

  submitTicket() {
    if (!this.customer?.id || !this.form.subject.trim() || !this.form.description.trim()) return;
    this.submitting = true;
    this.successMsg = '';
    this.errorMsg = '';

    this.supportService
      .create({
        customerId: this.customer.id,
        subject: this.form.subject,
        description: this.form.description,
        priority: this.form.priority,
      })
      .subscribe({
        next: () => {
          this.submitting = false;
          this.successMsg = 'Support ticket created successfully!';
          this.showForm = false;
          this.form = { subject: '', description: '', priority: 'MEDIUM' };
          this.loadTickets(this.customer!.id!);
          this.cdr.markForCheck();
          setTimeout(() => (this.successMsg = ''), 3000);
        },
        error: (err: any) => {
          this.submitting = false;
          this.errorMsg = err.error?.message || 'Failed to create ticket.';
          this.cdr.markForCheck();
        },
      });
  }

  closeTicket(id: number) {
    if (!confirm('Close this support ticket?')) return;
    this.supportService.close(id).subscribe({
      next: () => {
        this.tickets = this.tickets.map((t) => (t.id === id ? { ...t, status: 'CLOSED' } : t));
        this.cdr.markForCheck();
      },
    });
  }

  statusClass(status: string): string {
    switch (status) {
      case 'RESOLVED':
        return 'bg-success';
      case 'IN_PROGRESS':
        return 'bg-info text-dark';
      case 'CLOSED':
        return 'bg-secondary';
      case 'PENDING':
        return 'bg-warning text-dark';
      default:
        return 'bg-light text-dark';
    }
  }

  priorityClass(priority: string): string {
    switch (priority) {
      case 'URGENT':
        return 'bg-danger';
      case 'HIGH':
        return 'bg-warning text-dark';
      case 'MEDIUM':
        return 'bg-primary';
      case 'LOW':
        return 'bg-secondary';
      default:
        return 'bg-secondary';
    }
  }
}
