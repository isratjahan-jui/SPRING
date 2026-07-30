import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InvoiceService } from '../../../services/invoice.service';
import { AuthService } from '../../../services/auth.service';
import { InvoiceResponse } from '../../../models/invoice.model';

@Component({
  selector: 'app-owner-invoices',
  imports: [CommonModule],
  templateUrl: './invoices.html',
  styleUrl: './invoices.css',
})
export class OwnerInvoices implements OnInit {
  private invoiceService = inject(InvoiceService);
  private auth = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);

  invoices: InvoiceResponse[] = [];
  loading = true;
  downloadingId: number | null = null;

  ngOnInit() {
    const ownerId = this.auth.getUser()?.ownerId;
    if (ownerId) {
      this.invoiceService.getByOwnerId(ownerId).subscribe({
        next: (data) => {
          this.invoices = data;
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
    }
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'PAID':
        return 'bg-success';
      case 'ISSUED':
        return 'bg-warning text-dark';
      case 'CANCELLED':
        return 'bg-danger';
      default:
        return 'bg-secondary';
    }
  }

  downloadPdf(inv: InvoiceResponse) {
    this.downloadingId = inv.id;
    this.invoiceService.downloadPdf(inv.id).subscribe({
      next: (response) => {
        const blob = response.body;
        if (blob) {
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = 'Invoice_' + (inv.invoiceNumber || inv.id) + '.pdf';
          document.body.appendChild(a);
          a.click();
          document.body.removeChild(a);
          window.URL.revokeObjectURL(url);
        }
        this.downloadingId = null;
        this.cdr.markForCheck();
      },
      error: () => {
        this.downloadingId = null;
        this.cdr.markForCheck();
        alert('Failed to download PDF.');
      },
    });
  }

  printInvoice(inv: InvoiceResponse) {
    const pw = window.open('', '_blank');
    if (!pw) return;
    pw.document.write(`
      <!DOCTYPE html>
      <html><head><title>Invoice ${inv.invoiceNumber}</title>
      <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Arial, sans-serif; padding: 40px; color: #333; }
        .invoice-box { max-width: 800px; margin: 0 auto; border: 1px solid #ddd; padding: 30px; }
        .header { display: flex; justify-content: space-between; align-items: flex-start; border-bottom: 3px solid #2563eb; padding-bottom: 20px; margin-bottom: 25px; }
        .header h1 { font-size: 28px; color: #2563eb; }
        .header .inv-meta { text-align: right; }
        .header .inv-meta .inv-num { font-size: 18px; font-weight: bold; color: #2563eb; }
        .section { margin-bottom: 20px; }
        .section h3 { font-size: 14px; text-transform: uppercase; color: #666; margin-bottom: 8px; border-bottom: 1px solid #eee; padding-bottom: 5px; }
        .details-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 5px 30px; }
        .details-grid p { font-size: 14px; line-height: 1.6; }
        .details-grid .label { color: #888; }
        table { width: 100%; border-collapse: collapse; margin: 20px 0; }
        th { background: #f8f9fa; text-align: left; padding: 10px 12px; font-size: 13px; text-transform: uppercase; color: #666; border-bottom: 2px solid #ddd; }
        td { padding: 10px 12px; border-bottom: 1px solid #eee; font-size: 14px; }
        .text-right { text-align: right; }
        .summary { float: right; width: 300px; }
        .summary .total-row { font-size: 18px; font-weight: bold; border-top: 2px solid #333; }
        .footer { clear: both; text-align: center; margin-top: 40px; padding-top: 20px; border-top: 2px solid #eee; color: #888; font-size: 13px; }
        @media print { body { padding: 20px; } .invoice-box { border: none; } }
      </style></head>
      <body>
        <div class="invoice-box">
          <div class="header">
            <div><h1>INVOICE</h1><p style="color:#666; font-size:14px;">Multi Hotel Management System</p></div>
            <div class="inv-meta">
              <p class="inv-num">${inv.invoiceNumber}</p>
              <p>Date: ${inv.issuedAt ? new Date(inv.issuedAt).toLocaleDateString('en-BD', { day: '2-digit', month: 'short', year: 'numeric' }) : 'N/A'}</p>
              <p>Status: <strong>${inv.status}</strong></p>
            </div>
          </div>
          <div class="section">
            <h3>Booking Details</h3>
            <div class="details-grid">
              <p><span class="label">Booking ID:</span> #${inv.bookingId}</p>
              <p><span class="label">Hotel:</span> ${inv.hotelName || 'N/A'}</p>
              <p><span class="label">Room Type:</span> ${inv.roomType || 'N/A'}</p>
              <p><span class="label">Customer:</span> ${inv.customerName || 'N/A'}</p>
            </div>
          </div>
          <div class="section">
            <h3>Payment Summary</h3>
            <div class="summary">
              <table>
                <tr><td>Subtotal</td><td class="text-right">${inv.totalAmount?.toFixed(2) || '0.00'}</td></tr>
                <tr><td>+ Tax</td><td class="text-right">${inv.taxAmount?.toFixed(2) || '0.00'}</td></tr>
                <tr><td>- Discount</td><td class="text-right">-${inv.discountAmount?.toFixed(2) || '0.00'}</td></tr>
                <tr class="total-row"><td>Net Amount</td><td class="text-right">BDT ${inv.netAmount?.toFixed(2) || '0.00'}</td></tr>
              </table>
            </div>
          </div>
          <div class="footer"><p>Multi Hotel Management System &copy; ${new Date().getFullYear()}</p></div>
        </div>
        <script>window.onload = function() { window.print(); }</script>
      </body></html>
    `);
    pw.document.close();
  }
}
