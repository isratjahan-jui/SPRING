import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { SupportService } from '../../../services/support.service';
import { BookingService } from '../../../services/booking.service';
import { SupportTicket } from '../../../models/support.model';

@Component({
  selector: 'app-customer-support',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './support.component.html',
  styleUrls: ['./support.component.css'],
})
export class CustomerSupportComponent implements OnInit {
  private auth = inject(AuthService);
  private supportSvc = inject(SupportService);
  private bookingSvc = inject(BookingService);

  tickets: SupportTicket[] = [];
  customerId: number | null = null;
  newTicket = { subject: '', description: '' };

  ngOnInit() {
    this.bookingSvc.getCustomerByUserId(this.auth.userId()!).subscribe(cust => {
      this.customerId = cust.id;
      this.supportSvc.getByCustomer(cust.id).subscribe(d => this.tickets = d);
    });
  }

  createTicket() {
    if (!this.customerId || !this.newTicket.subject) return;
    this.supportSvc.create({ ...this.newTicket, customerId: this.customerId }).subscribe(() => {
      this.supportSvc.getByCustomer(this.customerId!).subscribe(d => this.tickets = d);
      this.newTicket = { subject: '', description: '' };
    });
  }

  closeTicket(id: number) {
    this.supportSvc.close(id).subscribe(() => {
      this.tickets = this.tickets.map(t => t.id === id ? { ...t, status: 'CLOSED' } : t);
    });
  }
}
