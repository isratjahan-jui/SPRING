import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { CustomerService } from '../../../services/customer.service';
import { BookingService } from '../../../services/booking.service';
import { Customer } from '../../../models/customer.model';
import { Booking } from '../../../models/booking.model';
import { StorageService } from '../../../services/storage.service';
import { LoginResponse } from '../../../models/auth.model';

@Component({
  selector: 'app-customer-dashboard',
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class CustomerDashboard implements OnInit {

  private auth = inject(AuthService);
  private customerService = inject(CustomerService);
  private bookingService = inject(BookingService);
  private storage = inject(StorageService);
  private cdr = inject(ChangeDetectorRef);

  user: LoginResponse | null = null;
  userId!: number;

  customer?: Customer;
  bookings: Booking[] = [];
  loading = true;

  ngOnInit() {
    this.user = this.storage.getUser();

    if (this.user?.userId) {
      this.userId = this.user.userId;
    }

    if (this.userId) {
      this.customerService.getCustomerByUserId(this.userId).subscribe({
        next: (data) => {
          this.customer = data;
          if (data.id) {
            this.bookingService.getByCustomer(data.id).subscribe({
              next: (b) => {
                this.bookings = b;
                this.loading = false;
              },
              error: () => (this.loading = false),
            });
          } else {
            this.loading = false;
          }
        },
        error: () => (this.loading = false),
      });
    } else {
      this.loading = false;
    }
  }
}
