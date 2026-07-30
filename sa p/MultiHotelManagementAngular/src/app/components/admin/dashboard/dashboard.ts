import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { HotelOwnerService } from '../../../services/hotel-owner.service';
import { CustomerService } from '../../../services/customer.service';
import { HotelService } from '../../../services/hotel.service';
import { BookingService } from '../../../services/booking.service';
import { PaymentService } from '../../../services/payment.service';
import { CommissionService } from '../../../services/commission.service';
import { InvoiceService } from '../../../services/invoice.service';
import { ReceiptService } from '../../../services/receipt.service';
import { CustomerSupportService } from '../../../services/customer-support.service';
import { LocationService } from '../../../services/location.service';
import { ReportService, PlatformSummary } from '../../../services/report.service';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-admin-dashboard',
  imports: [RouterLink, DecimalPipe],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class AdminDashboard implements OnInit {
  private auth = inject(AuthService);
  private ownerService = inject(HotelOwnerService);
  private customerService = inject(CustomerService);
  private hotelService = inject(HotelService);
  private bookingService = inject(BookingService);
  private paymentService = inject(PaymentService);
  private commissionService = inject(CommissionService);
  private invoiceService = inject(InvoiceService);
  private receiptService = inject(ReceiptService);
  private supportService = inject(CustomerSupportService);
  private locationService = inject(LocationService);
  private reportService = inject(ReportService);
  private cdr = inject(ChangeDetectorRef);

  userName = '';
  ownerCount = 0;
  customerCount = 0;
  totalLocations = 0;
  approvedHotelCount = 0;
  pendingHotelCount = 0;
  rejectedHotelCount = 0;
  totalBookings = 0;
  activeBookings = 0;
  totalPayments = 0;
  paidPayments = 0;
  totalCommission = 0;
  totalInvoices = 0;
  paidInvoices = 0;
  totalReceipts = 0;
  totalTickets = 0;
  openTickets = 0;
  platformSummary: PlatformSummary | null = null;
  loading = true;

  ngOnInit() {
    const user = this.auth.getUser();
    this.userName = user?.name || user?.email || 'Admin';

    forkJoin({
      owners: this.ownerService.getAllOwners().pipe(catchError(() => of([]))),
      customers: this.customerService.getAllCustomers().pipe(catchError(() => of([]))),
      hotels: this.hotelService.getAll().pipe(catchError(() => of([]))),
      bookings: this.bookingService.getAll().pipe(catchError(() => of([]))),
      payments: this.paymentService.getAll().pipe(catchError(() => of([]))),
      commission: this.commissionService.getAdminTotal().pipe(catchError(() => of(0))),
      invoices: this.invoiceService.getAll().pipe(catchError(() => of([]))),
      receipts: this.receiptService.getAll().pipe(catchError(() => of([]))),
      tickets: this.supportService.getAll().pipe(catchError(() => of([]))),
      locations: this.locationService.getAll().pipe(catchError(() => of([]))),
      summary: this.reportService
        .getPlatformSummary()
        .pipe(catchError(() => of(null as PlatformSummary | null))),
    }).subscribe({
      next: (r) => {
        this.ownerCount = r.owners.length;
        this.customerCount = r.customers.length;
        this.totalLocations = r.locations.length;
        this.approvedHotelCount = r.hotels.filter((h: any) => h.status === 'APPROVED').length;
        this.pendingHotelCount = r.hotels.filter(
          (h: any) => h.status === 'PENDING_APPROVAL',
        ).length;
        this.rejectedHotelCount = r.hotels.filter((h: any) => h.status === 'REJECTED').length;
        this.totalBookings = r.bookings.length;
        this.activeBookings = r.bookings.filter(
          (b: any) =>
            b.status === 'PENDING' || b.status === 'CONFIRMED' || b.status === 'CHECKED_IN',
        ).length;
        this.totalPayments = r.payments.length;
        this.paidPayments = r.payments.filter((p: any) => p.status === 'PAID').length;
        this.totalCommission = r.commission;
        this.totalInvoices = r.invoices.length;
        this.paidInvoices = r.invoices.filter((i: any) => i.status === 'PAID').length;
        this.totalReceipts = r.receipts.length;
        this.totalTickets = r.tickets.length;
        this.openTickets = r.tickets.filter(
          (t: any) =>
            t.status === 'PENDING' || t.status === 'IN_PROGRESS' || t.status === 'ESCALATED',
        ).length;
        this.platformSummary = r.summary;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }
}
