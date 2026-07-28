import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PaymentService } from '../../../services/payment.service';
import { ReceiptService } from '../../../services/receipt.service';
import { AuthService } from '../../../services/auth.service';
import { CustomerService } from '../../../services/customer.service';
import { PaymentResponse } from '../../../models/payment.model';
import { ReceiptResponse } from '../../../models/receipt.model';

@Component({
  selector: 'app-customer-payments',
  imports: [CommonModule, RouterLink],
  templateUrl: './payments.html',
  styleUrl: './payments.css',
})
export class CustomerPayments implements OnInit {
  private paymentService = inject(PaymentService);
  private receiptService = inject(ReceiptService);
  private auth = inject(AuthService);
  private customerService = inject(CustomerService);
  private cdr = inject(ChangeDetectorRef);

  payments: PaymentResponse[] = [];
  receipts: Map<number, ReceiptResponse> = new Map();
  loading = true;

  ngOnInit() {
    const userId = this.auth.getUser()?.userId;
    if (userId) {
      this.customerService.getCustomerByUserId(userId).subscribe({
        next: (customer) => {
          if (customer.id) {
            this.paymentService.getByCustomer(customer.id).subscribe({
              next: (data) => {
                this.payments = data;
                this.loadReceiptsForPayments(data);
                this.loading = false;
                this.cdr.markForCheck();
              },
              error: () => {
                this.loading = false;
                this.cdr.markForCheck();
              },
            });
          } else {
            this.loading = false;
            this.cdr.markForCheck();
          }
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

  getStatusClass(status: string): string {
    switch (status) {
      case 'PAID':
        return 'bg-success';
      case 'PENDING':
        return 'bg-warning text-dark';
      case 'FAILED':
        return 'bg-danger';
      case 'REFUNDED':
        return 'bg-info text-dark';
      case 'UNPAID':
        return 'bg-secondary';
      default:
        return 'bg-secondary';
    }
  }

  private loadReceiptsForPayments(payments: PaymentResponse[]) {
    for (const p of payments) {
      if (p.status === 'PAID') {
        this.receiptService.getByPayment(p.id).subscribe({
          next: (receipt) => {
            this.receipts.set(p.id, receipt);
            this.cdr.markForCheck();
          },
          error: () => {},
        });
      }
    }
  }

  getReceipt(paymentId: number): ReceiptResponse | undefined {
    return this.receipts.get(paymentId);
  }

  printReceipt(r: ReceiptResponse) {
    const pw = window.open('', '_blank');
    if (!pw) return;
    pw.document.write(`
      <!DOCTYPE html>
      <html><head><title>Receipt ${r.receiptNumber}</title>
      <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Arial, sans-serif; padding: 40px; color: #333; }
        .receipt-box { max-width: 700px; margin: 0 auto; border: 1px solid #ddd; padding: 30px; }
        .header { display: flex; justify-content: space-between; align-items: flex-start; border-bottom: 3px solid #16a34a; padding-bottom: 20px; margin-bottom: 25px; }
        .header h1 { font-size: 28px; color: #16a34a; }
        .header .rcp-meta { text-align: right; }
        .header .rcp-meta .rcp-num { font-size: 18px; font-weight: bold; color: #16a34a; }
        .section { margin-bottom: 20px; }
        .section h3 { font-size: 14px; text-transform: uppercase; color: #666; margin-bottom: 8px; border-bottom: 1px solid #eee; padding-bottom: 5px; }
        .details-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 5px 30px; }
        .details-grid p { font-size: 14px; line-height: 1.6; }
        .details-grid .label { color: #888; }
        table { width: 100%; border-collapse: collapse; margin: 20px 0; }
        th { background: #f8f9fa; text-align: left; padding: 10px 12px; font-size: 13px; text-transform: uppercase; color: #666; border-bottom: 2px solid #ddd; }
        td { padding: 10px 12px; border-bottom: 1px solid #eee; font-size: 14px; }
        .text-right { text-align: right; }
        .total-row { font-size: 18px; font-weight: bold; border-top: 2px solid #333; }
        .footer { clear: both; text-align: center; margin-top: 40px; padding-top: 20px; border-top: 2px solid #eee; color: #888; font-size: 13px; }
        @media print { body { padding: 20px; } .receipt-box { border: none; } }
      </style></head>
      <body>
        <div class="receipt-box">
          <div class="header">
            <div><h1>RECEIPT</h1><p style="color:#666; font-size:14px;">Multi Hotel Management System</p></div>
            <div class="rcp-meta">
              <p class="rcp-num">${r.receiptNumber}</p>
              <p>Date: ${r.issuedAt ? new Date(r.issuedAt).toLocaleDateString('en-BD', { day: '2-digit', month: 'short', year: 'numeric' }) : 'N/A'}</p>
            </div>
          </div>
          <div class="section">
            <h3>Payment Details</h3>
            <div class="details-grid">
              <p><span class="label">Transaction ID:</span> ${r.transactionId || 'N/A'}</p>
              <p><span class="label">Payment Method:</span> ${r.paymentMethod || 'N/A'}</p>
              <p><span class="label">Booking ID:</span> #${r.bookingId}</p>
              <p><span class="label">Customer:</span> ${r.customerName || 'N/A'}</p>
            </div>
          </div>
          <div class="section">
            <table>
              <thead><tr><th>Description</th><th class="text-right">Amount (BDT)</th></tr></thead>
              <tbody>
                <tr><td>Payment Amount</td><td class="text-right">${r.amount?.toFixed(2) || '0.00'}</td></tr>
                <tr><td>Tax</td><td class="text-right">${r.taxAmount?.toFixed(2) || '0.00'}</td></tr>
                <tr class="total-row"><td>Total</td><td class="text-right">BDT ${r.totalAmount?.toFixed(2) || '0.00'}</td></tr>
              </tbody>
            </table>
          </div>
          <div class="footer"><p>Thank you for your payment!</p></div>
        </div>
        <script>window.onload = function() { window.print(); }</script>
      </body></html>
    `);
    pw.document.close();
  }
}
