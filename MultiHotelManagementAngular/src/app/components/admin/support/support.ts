import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../services/auth.service';
import { environment } from '../../../../environments/environments';
import { SupportTicketResponse } from '../../../services/customer-support.service';

@Component({
  selector: 'app-admin-support',
  imports: [CommonModule, FormsModule],
  templateUrl: './support.html',
  styleUrl: './support.css',
})
export class AdminSupport implements OnInit {
  private http = inject(HttpClient);
  private auth = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);

  private apiUrl = environment.apiUrl + 'support';

  tickets: SupportTicketResponse[] = [];
  filteredTickets: SupportTicketResponse[] = [];
  loading = true;
  successMsg = '';
  errorMsg = '';

  filterStatus = '';
  filterPriority = '';
  searchTerm = '';

  showEditModal = false;
  editingTicket: SupportTicketResponse | null = null;
  editForm = { status: '', priority: '' };

  ngOnInit() {
    this.loadTickets();
  }

  loadTickets() {
    this.loading = true;
    this.http.get<SupportTicketResponse[]>(this.apiUrl).subscribe({
      next: (data) => {
        this.tickets = data;
        this.applyFilters();
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        const user = this.auth.getUser();
        if (user?.userId) {
          this.http.get<SupportTicketResponse[]>(`${this.apiUrl}/agent/${user.userId}`).subscribe({
            next: (data) => {
              this.tickets = data;
              this.applyFilters();
              this.loading = false;
              this.cdr.markForCheck();
            },
            error: () => {
              this.loading = false;
              this.errorMsg = 'Failed to load support tickets.';
              this.cdr.markForCheck();
            },
          });
        } else {
          this.loading = false;
          this.errorMsg = 'Failed to load support tickets.';
          this.cdr.markForCheck();
        }
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
        t.customerName?.toLowerCase().includes(search) ||
        t.agentName?.toLowerCase().includes(search);
      return matchesStatus && matchesPriority && matchesSearch;
    });
    this.cdr.markForCheck();
  }

  onFilterChange() {
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

    this.http
      .put<SupportTicketResponse>(`${this.apiUrl}/${this.editingTicket.id}`, this.editForm)
      .subscribe({
        next: () => {
          this.successMsg = 'Ticket updated successfully!';
          this.closeEditModal();
          this.loadTickets();
          this.cdr.markForCheck();
          setTimeout(() => (this.successMsg = ''), 3000);
        },
        error: (err) => {
          this.errorMsg = err.error?.message || 'Failed to update ticket.';
          this.cdr.markForCheck();
        },
      });
  }

  closeTicket(ticket: SupportTicketResponse) {
    if (!confirm(`Close ticket #${ticket.id}?`)) return;

    this.http.put<void>(`${this.apiUrl}/${ticket.id}/close`, {}).subscribe({
      next: () => {
        this.successMsg = 'Ticket closed successfully!';
        this.loadTickets();
        this.cdr.markForCheck();
        setTimeout(() => (this.successMsg = ''), 3000);
      },
      error: (err) => {
        this.errorMsg = err.error?.message || 'Failed to close ticket.';
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
        return 'bg-danger';
      case 'MEDIUM':
        return 'bg-warning text-dark';
      case 'LOW':
        return 'bg-secondary';
      default:
        return 'bg-secondary';
    }
  }
}
