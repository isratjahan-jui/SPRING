import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReceiptService } from '../../services/receipt.service';
import { AuthService } from '../../services/auth.service';
import { Receipt } from '../../models';

@Component({
  selector: 'app-customer-receipts',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page">
      <div class="page-header">
        <h1>My Receipts</h1>
        <p class="subtitle">Payment receipts for your bookings</p>
      </div>

      <div class="receipts-grid">
        @for (r of receipts; track r.id) {
          <div class="receipt-card">
            <div class="receipt-header">
              <span class="receipt-number">{{ r.receiptNumber }}</span>
              <span class="receipt-date">{{ r.issuedAt | date:'mediumDate' }}</span>
            </div>
            <div class="receipt-body">
              <div class="receipt-row">
                <span class="label">Booking</span>
                <span class="value">{{ r.bookingReference }}</span>
              </div>
              @if (r.invoiceNumber) {
                <div class="receipt-row">
                  <span class="label">Invoice</span>
                  <span class="value mono">{{ r.invoiceNumber }}</span>
                </div>
              }
              <div class="receipt-row">
                <span class="label">Method</span>
                <span class="value">{{ r.paymentMethod }}</span>
              </div>
              <div class="receipt-row">
                <span class="label">Transaction</span>
                <span class="value mono small">{{ r.transactionId || '-' }}</span>
              </div>
            </div>
            <div class="receipt-footer">
              <span class="total-label">Total Paid</span>
              <span class="total-amount">{{ r.totalAmount | number:'1.2-2' }}</span>
            </div>
          </div>
        } @empty {
          <div class="empty-state">
            <p>No receipts yet. Complete a payment to receive a receipt.</p>
          </div>
        }
      </div>
    </div>
  `,
  styles: [`
    .page { max-width: 1200px; }
    .page-header { margin-bottom: 32px; }
    h1 { font-size: 28px; font-weight: 600; margin: 0; }
    .subtitle { color: #666; font-size: 14px; margin: 4px 0 0; }

    .receipts-grid {
      display: grid; grid-template-columns: repeat(auto-fill, minmax(340px, 1fr)); gap: 20px;
    }

    .receipt-card {
      background: rgba(255,255,255,0.03); border: 1px solid rgba(255,255,255,0.06);
      border-radius: 12px; overflow: hidden; transition: border-color 0.2s;
    }

    .receipt-card:hover { border-color: rgba(201,169,110,0.3); }

    .receipt-header {
      display: flex; justify-content: space-between; align-items: center;
      padding: 16px 20px; border-bottom: 1px solid rgba(255,255,255,0.04);
      background: rgba(201,169,110,0.04);
    }

    .receipt-number {
      font-family: 'JetBrains Mono', monospace; font-size: 13px; font-weight: 600;
      color: #c9a96e;
    }

    .receipt-date { font-size: 12px; color: #666; }

    .receipt-body { padding: 16px 20px; }

    .receipt-row {
      display: flex; justify-content: space-between; padding: 6px 0;
      border-bottom: 1px solid rgba(255,255,255,0.03);
    }

    .receipt-row:last-child { border-bottom: none; }

    .label { font-size: 12px; color: #666; }
    .value { font-size: 13px; }
    .mono { font-family: 'JetBrains Mono', monospace; color: #c9a96e; font-size: 12px; }
    .small { font-size: 11px; }

    .receipt-footer {
      display: flex; justify-content: space-between; align-items: center;
      padding: 16px 20px; border-top: 1px solid rgba(255,255,255,0.06);
      background: rgba(255,255,255,0.02);
    }

    .total-label { font-size: 12px; color: #888; text-transform: uppercase; letter-spacing: 1px; }

    .total-amount {
      font-size: 20px; font-weight: 700; color: #2ecc71;
      font-variant-numeric: tabular-nums;
    }

    .empty-state {
      text-align: center; color: #666; padding: 60px 20px;
      background: rgba(255,255,255,0.02); border: 1px dashed rgba(255,255,255,0.08);
      border-radius: 12px; grid-column: 1 / -1;
    }
  `]
})
export class CustomerReceiptsComponent implements OnInit {
  receipts: Receipt[] = [];

  constructor(
    private receiptService: ReceiptService,
    private auth: AuthService
  ) {}

  ngOnInit() {
    const userId = this.auth.currentUser?.id;
    if (userId) {
      this.receiptService.getByCustomerId(userId).subscribe({
        next: (data) => this.receipts = data,
        error: () => {}
      });
    }
  }
}
