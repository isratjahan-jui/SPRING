import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InvoiceService } from '../../services/invoice.service';
import { AuthService } from '../../services/auth.service';
import { Invoice } from '../../models';

@Component({
  selector: 'app-customer-invoices',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page">
      <div class="page-header">
        <div>
          <h1>My Invoices</h1>
          <p class="subtitle">Your booking invoices</p>
        </div>
        <div class="stats">
          <div class="stat-card">
            <span class="stat-value">{{ invoices.length }}</span>
            <span class="stat-label">Total</span>
          </div>
          <div class="stat-card paid">
            <span class="stat-value">{{ totalPaid | number:'1.2-2' }}</span>
            <span class="stat-label">Paid</span>
          </div>
          <div class="stat-card issued">
            <span class="stat-value">{{ totalPending | number:'1.2-2' }}</span>
            <span class="stat-label">Pending</span>
          </div>
        </div>
      </div>

      <div class="table-container">
        <table>
          <thead>
            <tr>
              <th>Invoice #</th>
              <th>Hotel</th>
              <th>Room</th>
              <th>Total</th>
              <th>Discount</th>
              <th>Tax</th>
              <th>Net Amount</th>
              <th>Status</th>
              <th>Date</th>
            </tr>
          </thead>
          <tbody>
            @for (inv of invoices; track inv.id) {
              <tr>
                <td class="mono">{{ inv.invoiceNumber }}</td>
                <td>{{ inv.hotelName || '-' }}</td>
                <td>{{ inv.roomType || '-' }}</td>
                <td class="amount">{{ inv.totalAmount | number:'1.2-2' }}</td>
                <td class="amount dim">{{ inv.discountAmount | number:'1.2-2' }}</td>
                <td class="amount dim">{{ inv.taxAmount | number:'1.2-2' }}</td>
                <td class="amount bold">{{ inv.netAmount | number:'1.2-2' }}</td>
                <td>
                  <span class="badge" [class]="inv.status.toLowerCase()">{{ inv.status }}</span>
                </td>
                <td class="date">{{ inv.issuedAt | date:'medium' }}</td>
              </tr>
            } @empty {
              <tr><td colspan="9" class="empty">No invoices yet. Book a hotel to get started!</td></tr>
            }
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: [`
    .page { max-width: 1200px; }
    .page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 32px; }
    h1 { font-size: 28px; font-weight: 600; margin: 0; }
    .subtitle { color: #666; font-size: 14px; margin: 4px 0 0; }
    .stats { display: flex; gap: 12px; }
    .stat-card {
      background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.06);
      border-radius: 10px; padding: 12px 20px; text-align: center; min-width: 80px;
    }
    .stat-value { display: block; font-size: 22px; font-weight: 700; color: #e8e6e3; }
    .stat-card.paid .stat-value { color: #2ecc71; }
    .stat-card.issued .stat-value { color: #c9a96e; }
    .stat-label {
      display: block; font-size: 11px; color: #666; text-transform: uppercase;
      letter-spacing: 1px; margin-top: 2px;
    }
    .table-container {
      background: rgba(255,255,255,0.02); border: 1px solid rgba(255,255,255,0.06);
      border-radius: 12px; overflow: hidden;
    }
    table { width: 100%; border-collapse: collapse; }
    th {
      padding: 14px 16px; text-align: left; font-size: 11px; font-weight: 600;
      color: #666; text-transform: uppercase; letter-spacing: 1px;
      border-bottom: 1px solid rgba(255,255,255,0.06); background: rgba(255,255,255,0.02);
    }
    td { padding: 14px 16px; font-size: 13px; border-bottom: 1px solid rgba(255,255,255,0.04); }
    tr:hover { background: rgba(201,169,110,0.03); }
    .mono { font-family: 'JetBrains Mono', monospace; font-size: 12px; color: #c9a96e; }
    .amount { text-align: right; font-variant-numeric: tabular-nums; }
    .bold { font-weight: 600; }
    .dim { color: #666; }
    .date { color: #666; font-size: 12px; }
    .badge {
      display: inline-block; padding: 4px 10px; border-radius: 20px;
      font-size: 11px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px;
    }
    .badge.paid { background: rgba(46,204,113,0.12); color: #2ecc71; }
    .badge.issued { background: rgba(201,169,110,0.12); color: #c9a96e; }
    .badge.cancelled { background: rgba(231,76,60,0.12); color: #e74c3c; }
    .empty { text-align: center; color: #666; padding: 40px !important; }
  `]
})
export class CustomerInvoicesComponent implements OnInit {
  invoices: Invoice[] = [];

  get totalPaid() {
    return this.invoices.filter(i => i.status === 'PAID').reduce((s, i) => s + (i.netAmount || 0), 0);
  }
  get totalPending() {
    return this.invoices.filter(i => i.status === 'ISSUED').reduce((s, i) => s + (i.netAmount || 0), 0);
  }

  constructor(
    private invoiceService: InvoiceService,
    private auth: AuthService
  ) {}

  ngOnInit() {
    const userId = this.auth.currentUser?.id;
    if (userId) {
      this.invoiceService.getByCustomer(userId).subscribe(data => this.invoices = data);
    }
  }
}
