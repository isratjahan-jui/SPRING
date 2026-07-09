import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { CustomerService } from '../../../services/customer.service';
import { CustomerSupportService } from '../../../services/customer-support.service';
import {
  CustomerSupportResponse,
  CustomerSupportRequest,
} from '../../../models/customer-support.model';

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

  tickets: CustomerSupportResponse[] = [];
  loading = true;
  showForm = false;
  customerId?: number;
  newTicket: CustomerSupportRequest = {
    subject: '',
    description: '',
    status: 'PENDING',
    priority: 'MEDIUM',
    customerId: 0,
  };

  ngOnInit() {
    const userId = this.auth.userId();
    if (userId) {
      this.customerService.getCustomerByUserId(userId).subscribe({
        next: (customer) => {
          if (customer.id) {
            this.customerId = customer.id;
            this.newTicket.customerId = customer.id;
            this.supportService.getByCustomer(customer.id).subscribe({
              next: (data) => {
                this.tickets = data;
                this.loading = false;
              },
              error: () => (this.loading = false),
            });
          } else {
            this.loading = false;
          }
        },
        error: () => (this.loading = false),
      });
    } else {
      this.loading = false;
    }
  }

  submitTicket() {
    if (!this.newTicket.subject || !this.newTicket.description) return;
    this.supportService.create(this.newTicket).subscribe({
      next: (ticket) => {
        this.tickets.unshift(ticket);
        this.showForm = false;
        this.newTicket.subject = '';
        this.newTicket.description = '';
        this.newTicket.priority = 'MEDIUM';
      },
    });
  }
}
