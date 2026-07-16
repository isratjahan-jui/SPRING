import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { InvoiceService } from '../../../services/invoice.service';
import { AuthService } from '../../../services/auth.service';
import { CustomerService } from '../../../services/customer.service';
import { InvoiceResponse } from '../../../models/invoice.model';
import { BookingService } from '../../../services/booking.service';
import { Booking } from '../../../models/booking.model';

@Component({
  selector: 'app-customer-invoices',
  imports: [CommonModule, RouterLink],
  templateUrl: './invoices.html',
  styleUrl: './invoices.css',
})
export class CustomerInvoices implements OnInit {
  private invoiceService = inject(InvoiceService);
  private auth = inject(AuthService);
  private customerService = inject(CustomerService);
  private cdr = inject(ChangeDetectorRef);

  invoices: InvoiceResponse[] = [];
  bookingCache: Map<number, Booking> = new Map();
  loading = true;
  private bookingService = inject(BookingService);

  ngOnInit() {
    const userId = this.auth.getUser()?.userId;
    if (userId) {
      this.customerService.getCustomerByUserId(userId).subscribe({
        next: (customer) => {
          if (customer.id) {
            this.invoiceService.getByCustomer(customer.id).subscribe({
              next: (data) => {
                this.invoices = data;
                this.loading = false;
                this.cdr.markForCheck();
                data.forEach((inv) => this.loadBooking(inv.bookingId));
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

  loadBooking(bookingId: number) {
    if (this.bookingCache.has(bookingId)) return;
    this.bookingService.getById(bookingId).subscribe({
      next: (b) => {
        this.bookingCache.set(bookingId, b);
        this.cdr.markForCheck();
      },
    });
  }

  getBooking(bookingId: number): Booking | undefined {
    return this.bookingCache.get(bookingId);
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

  printInvoice(inv: InvoiceResponse) {
    const b = this.bookingCache.get(inv.bookingId);
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
        .header .inv-meta p { margin: 2px 0; font-size: 14px; }
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
        .summary table th, .summary table td { padding: 8px 12px; }
        .summary .total-row { font-size: 18px; font-weight: bold; border-top: 2px solid #333; }
        .footer { clear: both; text-align: center; margin-top: 40px; padding-top: 20px; border-top: 2px solid #eee; color: #888; font-size: 13px; }
        @media print { body { padding: 20px; } .invoice-box { border: none; } }
      </style></head>
      <body>
        <div class="invoice-box">
          <div class="header">
            <div>
              <h1>INVOICE</h1>
              <p style="color:#666; font-size:14px;">Multi Hotel Management System</p>
            </div>
            <div class="inv-meta">
              <p class="inv-num">${inv.invoiceNumber}</p>
              <p>Date: ${inv.issuedAt ? new Date(inv.issuedAt).toLocaleDateString('en-BD', { day: '2-digit', month: 'short', year: 'numeric' }) : 'N/A'}</p>
              <p>Status: <strong>${inv.status}</strong></p>
            </div>
          </div>

          ${
            b
              ? `
          <div class="section">
            <h3>Booking Details</h3>
            <div class="details-grid">
              <p><span class="label">Booking ID:</span> #${b.id}</p>
              <p><span class="label">Hotel:</span> ${b.hotelName || 'N/A'}</p>
              <p><span class="label">Room Type:</span> ${b.roomType || 'N/A'}</p>
              <p><span class="label">Rooms:</span> ${b.numberOfRooms}</p>
              <p><span class="label">Check-in:</span> ${b.checkInDate ? new Date(b.checkInDate).toLocaleDateString('en-BD', { day: '2-digit', month: 'short', year: 'numeric' }) : 'N/A'}</p>
              <p><span class="label">Check-out:</span> ${b.checkOutDate ? new Date(b.checkOutDate).toLocaleDateString('en-BD', { day: '2-digit', month: 'short', year: 'numeric' }) : 'N/A'}</p>
              <p><span class="label">Guests:</span> ${b.totalGuests}</p>
              <p><span class="label">Booking Status:</span> ${b.status}</p>
            </div>
          </div>
          `
              : ''
          }

          <div class="section">
            <h3>Payment Summary</h3>
            <table>
              <thead>
                <tr><th>Description</th><th class="text-right">Amount (BDT)</th></tr>
              </thead>
              <tbody>
                <tr><td>Room Charges</td><td class="text-right">${inv.totalAmount?.toFixed(2) || '0.00'}</td></tr>
                <tr><td>Tax (15% VAT)</td><td class="text-right">${inv.taxAmount?.toFixed(2) || '0.00'}</td></tr>
                <tr><td>Discount</td><td class="text-right">-${inv.discountAmount?.toFixed(2) || '0.00'}</td></tr>
              </tbody>
            </table>
            <div class="summary">
              <table>
                <tr><td>Subtotal</td><td class="text-right">${inv.totalAmount?.toFixed(2) || '0.00'}</td></tr>
                <tr><td>+ Tax</td><td class="text-right">${inv.taxAmount?.toFixed(2) || '0.00'}</td></tr>
                <tr><td>- Discount</td><td class="text-right">-${inv.discountAmount?.toFixed(2) || '0.00'}</td></tr>
                <tr class="total-row"><td>Net Amount</td><td class="text-right">BDT ${inv.netAmount?.toFixed(2) || '0.00'}</td></tr>
              </table>
            </div>
          </div>

          <div class="footer">
            <p>Thank you for your stay! For any queries, contact support.</p>
            <p>Multi Hotel Management System &copy; ${new Date().getFullYear()}</p>
          </div>
        </div>
        <script>window.onload = function() { window.print(); }</script>
      </body></html>
    `);
    pw.document.close();
  }
}
