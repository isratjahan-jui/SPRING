import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { AuthService } from '../../../services/auth.service';
import { HotelOwnerService } from '../../../services/hotel-owner.service';
import { CustomerService } from '../../../services/customer.service';
import { HotelService } from '../../../services/hotel.service';
import { BookingService } from '../../../services/booking.service';
import { CommissionService } from '../../../services/commission.service';
import { RouterLink } from '@angular/router';

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

    this.ownerService.getAllOwners().subscribe({
      next: (data) => {
        this.ownerCount = data.length;
        this.cdr.markForCheck();
      },
    });
    this.customerService.getAllCustomers().subscribe({
      next: (data) => {
        this.customerCount = data.length;
        this.cdr.markForCheck();
      },
    });
    this.hotelService.getAll().subscribe({
      next: (data) => {
        this.approvedHotelCount = data.filter((h) => h.status === 'APPROVED').length;
        this.pendingHotelCount = data.filter((h) => h.status === 'PENDING_APPROVAL').length;
        this.rejectedHotelCount = data.filter((h) => h.status === 'REJECTED').length;
        this.cdr.markForCheck();
      },
    });
    this.bookingService.getAll().subscribe({
      next: (data) => {
        this.totalBookings = data.length;
        this.cdr.markForCheck();
      },
      error: () => {},
    });
    this.commissionService.getAdminTotal().subscribe({
      next: (data) => {
        this.totalCommission = data;
        this.cdr.markForCheck();
      },
      error: () => {},
      complete: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }
}
