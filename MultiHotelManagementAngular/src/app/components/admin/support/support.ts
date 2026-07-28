import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import {
  CustomerSupportService,
  SupportTicketResponse,
  SupportReplyResponse,
  SupportStats,
} from '../../../services/customer-support.service';
import { environment } from '../../../../environments/environments';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-admin-support',
  imports: [CommonModule, FormsModule],
  templateUrl: './support.html',
  styleUrl: './support.css',
})
export class AdminSupport implements OnInit {
  private supportService = inject(CustomerSupportService);
  private auth = inject(AuthService);
  private http = inject(HttpClient);
  private cdr = inject(ChangeDetectorRef);

  tickets: SupportTicketResponse[] = [];
  filteredTickets: SupportTicketResponse[] = [];
  stats: SupportStats | null = null;
  loading = true;
  successMsg = '';
  errorMsg = '';

  filterStatus = '';
  filterPriority = '';
  filterCategory = '';
  filterAgent = '';
  searchTerm = '';

  showEditModal = false;
  editingTicket: SupportTicketResponse | null = null;
  editForm = { status: '', priority: '' };

  showDetailModal = false;
  selectedTicket: SupportTicketResponse | null = null;
  replies: SupportReplyResponse[] = [];
  replyMessage = '';
  sendingReply = false;

  showAssignModal = false;
  assignTicket: SupportTicketResponse | null = null;
  agentIdInput: number | null = null;

  agents: { id: number; name: string }[] = [];

  ngOnInit() {
    this.loadTickets();
    this.loadAgents();
    this.loadStats();
  }

  loadAgents() {
    this.http.get<any[]>(environment.apiUrl + 'admin/agents').subscribe({
      next: (data) => {
        this.agents = data;
        this.cdr.markForCheck();
      },
      error: () => {
        this.http.get<any[]>(environment.apiUrl + 'users').subscribe({
          next: (data) => {
            this.agents = data.map((u: any) => ({ id: u.id, name: u.name }));
            this.cdr.markForCheck();
          },
          error: () => {},
        });
      },
    });
  }

  loadStats() {
    this.supportService.getStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  loadTickets() {
    this.loading = true;
    this.supportService.getAll().subscribe({
      next: (data) => {
        this.tickets = data;
        this.applyFilters();
        this.loading = false;
        this.loadStats();
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.errorMsg = 'Failed to load support tickets.';
        this.cdr.markForCheck();
      },
    });
  }

  applyFilters() {
    this.filteredTickets = this.tickets.filter((t) => {
      const matchesStatus = !this.filterStatus || t.status === this.filterStatus;
      const matchesPriority = !this.filterPriority || t.priority === this.filterPriority;
      const matchesCategory = !this.filterCategory || t.category === this.filterCategory;
      const matchesAgent =
        !this.filterAgent ||
        (this.filterAgent === 'unassigned' ? !t.agentId : t.agentId === Number(this.filterAgent));
      const search = this.searchTerm.toLowerCase();
      const matchesSearch =
        !search ||
        t.subject.toLowerCase().includes(search) ||
        t.customerName?.toLowerCase().includes(search) ||
        t.agentName?.toLowerCase().includes(search) ||
        t.hotelName?.toLowerCase().includes(search);
      return matchesStatus && matchesPriority && matchesCategory && matchesAgent && matchesSearch;
    });
    this.cdr.markForCheck();
  }

  onFilterChange() {
    this.applyFilters();
  }

  quickFilter(status: string) {
    this.filterStatus = this.filterStatus === status ? '' : status;
    this.applyFilters();
  }

  openEdit(ticket: SupportTicketResponse) {
    this.editingTicket = ticket;
    this.editForm = { status: ticket.status, priority: ticket.priority };
    this.showEditModal = true;
    this.cdr.markForCheck();
  }

  closeEditModal() {
    this.showEditModal = false;
    this.editingTicket = null;
    this.cdr.markForCheck();
  }

  saveTicket() {
    if (!this.editingTicket) return;
    this.supportService.update(this.editingTicket.id, this.editForm).subscribe({
      next: () => {
        this.successMsg = 'Ticket updated successfully!';
        this.closeEditModal();
        this.loadTickets();
        this.cdr.markForCheck();
        setTimeout(() => (this.successMsg = ''), 3000);
      },
      error: (err: any) => {
        this.errorMsg = err.error?.message || 'Failed to update ticket.';
        this.cdr.markForCheck();
      },
    });
  }

  closeTicket(ticket: SupportTicketResponse) {
    if (!confirm(`Close ticket #${ticket.id}?`)) return;
    this.supportService.close(ticket.id).subscribe({
      next: () => {
        this.successMsg = 'Ticket closed successfully!';
        this.loadTickets();
        this.cdr.markForCheck();
        setTimeout(() => (this.successMsg = ''), 3000);
      },
      error: (err: any) => {
        this.errorMsg = err.error?.message || 'Failed to close ticket.';
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
          this.successMsg = 'Reply sent!';
          this.loadTickets();
          this.cdr.markForCheck();
          setTimeout(() => (this.successMsg = ''), 3000);
        },
        error: () => {
          this.sendingReply = false;
          this.errorMsg = 'Failed to send reply.';
          this.cdr.markForCheck();
        },
      });
  }

  openAssign(ticket: SupportTicketResponse) {
    this.assignTicket = ticket;
    this.agentIdInput = ticket.agentId || null;
    this.showAssignModal = true;
    this.cdr.markForCheck();
  }

  closeAssignModal() {
    this.showAssignModal = false;
    this.assignTicket = null;
    this.cdr.markForCheck();
  }

  saveAssign() {
    if (!this.assignTicket || !this.agentIdInput) return;
    this.supportService.assignAgent(this.assignTicket.id, this.agentIdInput).subscribe({
      next: () => {
        this.successMsg = 'Agent assigned successfully!';
        this.closeAssignModal();
        this.loadTickets();
        this.cdr.markForCheck();
        setTimeout(() => (this.successMsg = ''), 3000);
      },
      error: () => {
        this.errorMsg = 'Failed to assign agent.';
        this.cdr.markForCheck();
      },
    });
  }

  getStatusLabel(status: string): string {
    switch (status) {
      case 'PENDING':
        return 'Awaiting';
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

  getCategoryLabel(cat: string): string {
    const labels: Record<string, string> = {
      BOOKING: 'Booking',
      PAYMENT: 'Payment',
      CANCELATION: 'Cancel',
      REFUND: 'Refund',
      HOTEL_SERVICE: 'Hotel Svc',
      ROOM: 'Room',
      STAFF: 'Staff',
      BILLING: 'Billing',
      OTHER: 'Other',
    };
    return labels[cat] || cat || 'General';
  }

  formatMinutes(mins: number | undefined): string {
    if (!mins || mins <= 0) return '-';
    if (mins < 60) return `${mins}m`;
    const hours = Math.floor(mins / 60);
    const m = mins % 60;
    return `${hours}h ${m}m`;
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
