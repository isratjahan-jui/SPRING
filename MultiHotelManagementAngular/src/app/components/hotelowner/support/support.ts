import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { HotelService } from '../../../services/hotel.service';
import {
  CustomerSupportService,
  SupportTicketResponse,
  SupportReplyResponse,
  SupportStats,
} from '../../../services/customer-support.service';
import { HotelOwnerService } from '../../../services/hotel-owner.service';

@Component({
  selector: 'app-owner-support',
  imports: [CommonModule, FormsModule],
  templateUrl: './support.html',
  styleUrl: './support.css',
})
export class OwnerSupport implements OnInit {
  private auth = inject(AuthService);
  private ownerService = inject(HotelOwnerService);
  private hotelService = inject(HotelService);
  private supportService = inject(CustomerSupportService);
  private cdr = inject(ChangeDetectorRef);

  tickets: SupportTicketResponse[] = [];
  filteredTickets: SupportTicketResponse[] = [];
  stats: SupportStats | null = null;
  hotelStats: { hotelName: string; hotelId: number; stats: SupportStats }[] = [];
  loading = true;
  successMsg = '';
  errorMsg = '';

  filterStatus = '';
  filterPriority = '';
  searchTerm = '';
  ownerId: number | null = null;
  ownerUserId: number | null = null;

  showDetailModal = false;
  selectedTicket: SupportTicketResponse | null = null;
  replies: SupportReplyResponse[] = [];
  replyMessage = '';
  sendingReply = false;

  showEscalateModal = false;
  escalateTicket: SupportTicketResponse | null = null;
  escalateReason = '';

  ngOnInit() {
    const user = this.auth.getUser();
    if (user?.userId) {
      this.ownerUserId = user.userId;
      this.ownerService.getOwnerByUserId(user.userId).subscribe({
        next: (owner) => {
          this.ownerId = owner.id!;
          this.loadTickets();
        },
        error: () => {
          this.loading = false;
          this.cdr.markForCheck();
        },
      });
    }
  }

  loadTickets() {
    if (!this.ownerId) return;
    this.loading = true;

    this.hotelService.getByOwner(this.ownerId).subscribe({
      next: (hotels: any[]) => {
        const allTickets: SupportTicketResponse[] = [];
        this.hotelStats = [];
        let loaded = 0;

        if (hotels.length === 0) {
          this.tickets = [];
          this.computeOverallStats();
          this.applyFilters();
          this.loading = false;
          this.cdr.markForCheck();
          return;
        }

        hotels.forEach((hotel: any) => {
          this.supportService.getByHotel(hotel.id).subscribe({
            next: (tickets) => {
              allTickets.push(...tickets);
              this.supportService.getStatsByHotel(hotel.id).subscribe({
                next: (stats) => {
                  this.hotelStats.push({ hotelName: hotel.hotelName, hotelId: hotel.id, stats });
                  loaded++;
                  if (loaded === hotels.length) {
                    this.tickets = allTickets;
                    this.computeOverallStats();
                    this.applyFilters();
                    this.loading = false;
                    this.cdr.markForCheck();
                  }
                },
                error: () => {
                  loaded++;
                  if (loaded === hotels.length) {
                    this.tickets = allTickets;
                    this.computeOverallStats();
                    this.applyFilters();
                    this.loading = false;
                    this.cdr.markForCheck();
                  }
                },
              });
            },
            error: () => {
              loaded++;
              if (loaded === hotels.length) {
                this.tickets = allTickets;
                this.computeOverallStats();
                this.applyFilters();
                this.loading = false;
                this.cdr.markForCheck();
              }
            },
          });
        });
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  computeOverallStats() {
    this.stats = {
      totalTickets: this.tickets.length,
      pendingCount: this.tickets.filter((t) => t.status === 'PENDING').length,
      inProgressCount: this.tickets.filter((t) => t.status === 'IN_PROGRESS').length,
      escalatedCount: this.tickets.filter((t) => t.status === 'ESCALATED').length,
      resolvedCount: this.tickets.filter((t) => t.status === 'RESOLVED').length,
      closedCount: this.tickets.filter((t) => t.status === 'CLOSED').length,
      unassignedCount: this.tickets.filter((t) => !t.agentName).length,
      avgResponseTimeHours: 0,
      avgResolutionTimeHours: 0,
    };
  }

  applyFilters() {
    this.filteredTickets = this.tickets.filter((t) => {
      const matchesStatus = !this.filterStatus || t.status === this.filterStatus;
      const matchesPriority = !this.filterPriority || t.priority === this.filterPriority;
      const search = this.searchTerm.toLowerCase();
      const matchesSearch =
        !search ||
        t.subject.toLowerCase().includes(search) ||
        t.customerName?.toLowerCase().includes(search) ||
        t.hotelName?.toLowerCase().includes(search) ||
        t.category?.toLowerCase().includes(search);
      return matchesStatus && matchesPriority && matchesSearch;
    });
    this.cdr.markForCheck();
  }

  onFilterChange() {
    this.applyFilters();
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

  openEscalate(ticket: SupportTicketResponse) {
    this.escalateTicket = ticket;
    this.escalateReason = '';
    this.showEscalateModal = true;
    this.cdr.markForCheck();
  }

  closeEscalateModal() {
    this.showEscalateModal = false;
    this.escalateTicket = null;
    this.escalateReason = '';
    this.cdr.markForCheck();
  }

  confirmEscalate() {
    if (!this.escalateTicket) return;
    this.supportService
      .escalate(this.escalateTicket.id, {
        reason: this.escalateReason,
        escalatedByUserId: this.ownerUserId || undefined,
      })
      .subscribe({
        next: () => {
          this.successMsg = 'Ticket escalated to Admin successfully!';
          this.closeEscalateModal();
          this.loadTickets();
          this.cdr.markForCheck();
          setTimeout(() => (this.successMsg = ''), 3000);
        },
        error: () => {
          this.errorMsg = 'Failed to escalate ticket.';
          this.cdr.markForCheck();
        },
      });
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
}
