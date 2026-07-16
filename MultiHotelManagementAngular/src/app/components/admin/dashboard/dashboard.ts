import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { AuthService } from '../../../services/auth.service';
import { HotelOwnerService } from '../../../services/hotel-owner.service';
import { CustomerService } from '../../../services/customer.service';
import { HotelService } from '../../../services/hotel.service';
import { BookingService } from '../../../services/booking.service';
import { CommissionService } from '../../../services/commission.service';
import { RouterLink } from '@angular/router';
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
  private commissionService = inject(CommissionService);
  private cdr = inject(ChangeDetectorRef);

  userName = '';
  ownerCount = 0;
  customerCount = 0;
  approvedHotelCount = 0;
  pendingHotelCount = 0;
  rejectedHotelCount = 0;
  totalBookings = 0;
  totalCommission = 0;
  loading = true;

  ngOnInit() {
    const user = this.auth.getUser();
    this.userName = user?.name || user?.email || 'Admin';

    forkJoin({
      owners: this.ownerService.getAllOwners().pipe(catchError(() => of([]))),
      customers: this.customerService.getAllCustomers().pipe(catchError(() => of([]))),
      hotels: this.hotelService.getAll().pipe(catchError(() => of([]))),
      bookings: this.bookingService.getAll().pipe(catchError(() => of([]))),
      commission: this.commissionService.getAdminTotal().pipe(catchError(() => of(0))),
    }).subscribe({
      next: (result) => {
        this.ownerCount = result.owners.length;
        this.customerCount = result.customers.length;
        this.approvedHotelCount = result.hotels.filter((h) => h.status === 'APPROVED').length;
        this.pendingHotelCount = result.hotels.filter(
          (h) => h.status === 'PENDING_APPROVAL',
        ).length;
        this.rejectedHotelCount = result.hotels.filter((h) => h.status === 'REJECTED').length;
        this.totalBookings = result.bookings.length;
        this.totalCommission = result.commission;
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
