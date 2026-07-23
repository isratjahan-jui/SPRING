import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { CustomerService } from '../../../services/customer.service';
import { HotelService } from '../../../services/hotel.service';
import {
  CustomerSupportService,
  SupportTicketResponse,
  SupportReplyResponse,
} from '../../../services/customer-support.service';
import { Customer } from '../../../models/customer.model';
import { Hotel } from '../../../models/hotel.model';

@Component({
  selector: 'app-customer-support',
  imports: [CommonModule, FormsModule],
  templateUrl: './support.html',
  styleUrl: './support.css',
})
export class CustomerSupport implements OnInit {
  private auth = inject(AuthService);
  private customerService = inject(CustomerService);
  private hotelService = inject(HotelService);
  private supportService = inject(CustomerSupportService);
  private cdr = inject(ChangeDetectorRef);

  customer?: Customer;
  hotels: Hotel[] = [];
  tickets: SupportTicketResponse[] = [];
  filteredTickets: SupportTicketResponse[] = [];
  loading = true;
  submitting = false;
  successMsg = '';
  errorMsg = '';

  filterStatus = '';
  filterPriority = '';
  searchTerm = '';

  showForm = false;
  form = { subject: '', description: '', priority: 'MEDIUM', category: '', hotelId: '' };

  showDetailModal = false;
  selectedTicket: SupportTicketResponse | null = null;
  replies: SupportReplyResponse[] = [];
  replyMessage = '';
  sendingReply = false;

  categoryIcons: Record<string, string> = {
    BOOKING: '&#128197;',
    PAYMENT: '&#128179;',
    CANCELATION: '&#10060;',
    REFUND: '&#128176;',
    HOTEL_SERVICE: '&#127976;',
    ROOM: '&#128716;',
    STAFF: '&#128101;',
    BILLING: '&#128196;',
    OTHER: '&#128221;',
  };

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
        this.applyFilters();
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  applyFilters() {
    this.filteredTickets = this.tickets.filter((t) => {
      const matchesStatus = !this.filterStatus || t.status === this.filterStatus;
      const matchesPriority = !this.filterPriority || t.priority === this.filterPriority;
      const search = this.searchTerm.toLowerCase();
      const matchesSearch =
        !search ||
        t.subject.toLowerCase().includes(search) ||
        t.category?.toLowerCase().includes(search) ||
        t.hotelName?.toLowerCase().includes(search);
      return matchesStatus && matchesPriority && matchesSearch;
    });
    this.cdr.markForCheck();
  }

  onFilterChange() {
    this.applyFilters();
  }

  openForm() {
    this.showForm = true;
    this.form = { subject: '', description: '', priority: 'MEDIUM', category: '', hotelId: '' };
    this.loadHotels();
    this.cdr.markForCheck();
  }

  closeForm() {
    this.showForm = false;
    this.cdr.markForCheck();
  }

  loadHotels() {
    this.hotelService.getAllApproved().subscribe({
      next: (data) => {
        this.hotels = data;
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  submitTicket() {
    if (!this.customer?.id || !this.form.subject.trim() || !this.form.description.trim()) return;
    this.submitting = true;
    this.successMsg = '';
    this.errorMsg = '';

    const payload: any = {
      customerId: this.customer.id,
      subject: this.form.subject,
      description: this.form.description,
      priority: this.form.priority,
    };
    if (this.form.category) payload.category = this.form.category;
    if (this.form.hotelId) payload.hotelId = Number(this.form.hotelId);

    this.supportService.create(payload).subscribe({
      next: () => {
        this.submitting = false;
        this.successMsg = 'Support ticket created successfully! Our team will respond shortly.';
        this.showForm = false;
        this.form = { subject: '', description: '', priority: 'MEDIUM', category: '', hotelId: '' };
        this.loadTickets(this.customer!.id!);
        this.cdr.markForCheck();
        setTimeout(() => (this.successMsg = ''), 5000);
      },
      error: (err: any) => {
        this.submitting = false;
        this.errorMsg = err.error?.message || 'Failed to create ticket.';
        this.cdr.markForCheck();
      },
    });
  }

  openDetail(ticket: SupportTicketResponse) {
    this.selectedTicket = ticket;
    this.showDetailModal = true;
    this.loadReplies(ticket.id);
    this.cdr.markForCheck();
  }

  closeDetailModal() {
    this.showDetailModal = false;
    this.selectedTicket = null;
    this.replies = [];
    this.replyMessage = '';
    this.cdr.markForCheck();
  }

  loadReplies(ticketId: number) {
    this.supportService.getReplies(ticketId).subscribe({
      next: (data) => {
        this.replies = data;
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  sendReply() {
    if (!this.selectedTicket || !this.replyMessage.trim()) return;
    const user = this.auth.getUser();
    if (!user) return;

    this.sendingReply = true;
    this.supportService
      .addReply({
        message: this.replyMessage,
        ticketId: this.selectedTicket.id,
        replierId: user.userId,
      })
      .subscribe({
        next: (reply) => {
          this.replies = [...this.replies, reply];
          this.replyMessage = '';
          this.sendingReply = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.sendingReply = false;
          this.errorMsg = 'Failed to send reply.';
          this.cdr.markForCheck();
        },
      });
  }

  closeTicket(id: number) {
    if (!confirm('Close this support ticket?')) return;
    this.supportService.close(id).subscribe({
      next: () => {
        this.tickets = this.tickets.map((t) => (t.id === id ? { ...t, status: 'CLOSED' } : t));
        if (this.selectedTicket?.id === id) {
          this.selectedTicket = { ...this.selectedTicket, status: 'CLOSED' };
        }
        this.applyFilters();
        this.cdr.markForCheck();
      },
    });
  }

  reopenTicket(id: number) {
    if (!confirm('Reopen this support ticket?')) return;
    this.supportService.update(id, { status: 'IN_PROGRESS' }).subscribe({
      next: (updated) => {
        this.tickets = this.tickets.map((t) => (t.id === id ? { ...t, status: 'IN_PROGRESS' } : t));
        if (this.selectedTicket?.id === id) {
          this.selectedTicket = updated;
        }
        this.successMsg = 'Ticket reopened successfully!';
        this.applyFilters();
        this.cdr.markForCheck();
        setTimeout(() => (this.successMsg = ''), 3000);
      },
      error: () => {
        this.errorMsg = 'Failed to reopen ticket.';
        this.cdr.markForCheck();
      },
    });
  }

  getCategoryLabel(cat: string): string {
    const labels: Record<string, string> = {
      BOOKING: 'Booking Issue',
      PAYMENT: 'Payment Problem',
      CANCELATION: 'Cancellation',
      REFUND: 'Refund Request',
      HOTEL_SERVICE: 'Hotel Service',
      ROOM: 'Room Issue',
      STAFF: 'Staff Behavior',
      BILLING: 'Billing Dispute',
      OTHER: 'Other',
    };
    return labels[cat] || cat || 'General';
  }

  statusClass(status: string): string {
    switch (status) {
      case 'RESOLVED':
        return 'status-resolved';
      case 'IN_PROGRESS':
        return 'status-progress';
      case 'CLOSED':
        return 'status-closed';
      case 'ESCALATED':
        return 'status-escalated';
      case 'PENDING':
        return 'status-pending';
      default:
        return '';
    }
  }

  priorityClass(priority: string): string {
    switch (priority) {
      case 'URGENT':
        return 'priority-urgent';
      case 'HIGH':
        return 'priority-high';
      case 'MEDIUM':
        return 'priority-medium';
      case 'LOW':
        return 'priority-low';
      default:
        return '';
    }
  }

  getStatusLabel(status: string): string {
    switch (status) {
      case 'PENDING':
        return 'Awaiting Response';
      case 'IN_PROGRESS':
        return 'In Progress';
      case 'ESCALATED':
        return 'Escalated';
      case 'RESOLVED':
        return 'Resolved';
      case 'CLOSED':
        return 'Closed';
      default:
        return status;
    }
  }

  getTimeAgo(dateStr: string): string {
    const date = new Date(dateStr);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    if (diffMins < 60) return `${diffMins}m ago`;
    const diffHours = Math.floor(diffMins / 60);
    if (diffHours < 24) return `${diffHours}h ago`;
    const diffDays = Math.floor(diffHours / 24);
    return `${diffDays}d ago`;
  }
}
