import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Payment } from '../../models';

@Component({
  selector: 'app-admin-payments',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page">
      <div class="page-header">
        <div>
          <h1>Payments</h1>
          <p class="subtitle">All payment transactions</p>
        </div>
        <div class="stats">
          <div class="stat-card">
            <span class="stat-value">{{ payments.length }}</span>
            <span class="stat-label">Total</span>
          </div>
          <div class="stat-card paid">
            <span class="stat-value">{{ totalPaid | number:'1.0-0' }}</span>
            <span class="stat-label">Total Paid</span>
          </div>
        </div>
      </div>

      <div class="table-container">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Booking</th>
              <th>Customer</th>
              <th>Method</th>
              <th>Amount</th>
              <th>Status</th>
              <th>Date</th>
            </tr>
          </thead>
          <tbody>
            @for (p of payments; track p.id) {
              <tr>
                <td class="mono">#{{ p.id }}</td>
                <td>{{ p.bookingReference }}</td>
                <td>{{ p.customerName || '-' }}</td>
                <td>{{ p.method }}</td>
                <td class="amount bold">{{ p.amount | number:'1.2-2' }}</td>
                <td>
                  <span class="badge" [class]="p.status.toLowerCase()">{{ p.status }}</span>
                </td>
                <td class="date">{{ p.transactionDate | date:'medium' }}</td>
              </tr>
            } @empty {
              <tr><td colspan="7" class="empty">No payments found</td></tr>
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
    .date { color: #666; font-size: 12px; }
    .badge {
      display: inline-block; padding: 4px 10px; border-radius: 20px;
      font-size: 11px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px;
    }
    .badge.paid, .badge.success { background: rgba(46,204,113,0.12); color: #2ecc71; }
    .badge.pending { background: rgba(201,169,110,0.12); color: #c9a96e; }
    .badge.failed { background: rgba(231,76,60,0.12); color: #e74c3c; }
    .badge.refunded { background: rgba(52,152,219,0.12); color: #3498db; }
    .empty { text-align: center; color: #666; padding: 40px !important; }
  `]
})
export class AdminPaymentsComponent implements OnInit {
  payments: Payment[] = [];

  get totalPaid() {
    return this.payments.filter(p => p.status === 'PAID' || p.status === 'SUCCESS')
      .reduce((s, p) => s + (p.amount || 0), 0);
  }

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.http.get<Payment[]>('http://localhost:8080/api/payments').subscribe(data => this.payments = data);
  }
}
