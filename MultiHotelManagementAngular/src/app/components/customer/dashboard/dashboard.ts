import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { CustomerService } from '../../../services/customer.service';
import { BookingService } from '../../../services/booking.service';
import { Customer } from '../../../models/customer.model';
import { Booking } from '../../../models/booking.model';

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

  userName = this.auth.userName;
  customer?: Customer;
  bookings: Booking[] = [];
  loading = true;

  ngOnInit() {
    const userId = this.auth.userId();
    if (userId) {
      this.customerService.getCustomerByUserId(userId).subscribe({
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
