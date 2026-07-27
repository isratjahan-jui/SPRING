import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReceiptService } from '../../services/receipt.service';
import { AuthService } from '../../services/auth.service';
import { Receipt } from '../../models';

@Component({
  selector: 'app-admin-receipts',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page">
      <div class="page-header">
        <h1>Receipts</h1>
        <p class="subtitle">All payment receipts across the platform</p>
      </div>

      <div class="table-container">
        <table>
          <thead>
            <tr>
              <th>Receipt #</th>
              <th>Invoice #</th>
              <th>Booking</th>
              <th>Customer</th>
              <th>Method</th>
              <th>Amount</th>
              <th>Date</th>
            </tr>
          </thead>
          <tbody>
            @for (r of receipts; track r.id) {
              <tr>
                <td class="mono">{{ r.receiptNumber }}</td>
                <td class="mono">{{ r.invoiceNumber || '-' }}</td>
                <td>{{ r.bookingReference }}</td>
                <td>{{ r.customerName }}</td>
                <td>{{ r.paymentMethod }}</td>
                <td class="amount bold">{{ r.totalAmount | number:'1.2-2' }}</td>
                <td class="date">{{ r.issuedAt | date:'medium' }}</td>
              </tr>
            } @empty {
              <tr><td colspan="7" class="empty">No receipts found</td></tr>
            }
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: [`
    .page { max-width: 1200px; }
    .page-header { margin-bottom: 32px; }
    h1 { font-size: 28px; font-weight: 600; margin: 0; }
    .subtitle { color: #666; font-size: 14px; margin: 4px 0 0; }
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
    .empty { text-align: center; color: #666; padding: 40px !important; }
  `]
})
export class AdminReceiptsComponent implements OnInit {
  receipts: Receipt[] = [];

  constructor(private receiptService: ReceiptService) {}

  ngOnInit() {
    this.receiptService.getByCustomerId(1).subscribe({
      next: (data) => this.receipts = data,
      error: () => {}
    });
    this.receiptService.getByOwnerId(1).subscribe({
      next: (data) => this.receipts = data,
      error: () => {}
    });
  }
}
