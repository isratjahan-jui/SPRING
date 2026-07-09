import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { BookingService } from '../../../services/booking.service';
import { CustomerService } from '../../../services/customer.service';
import { AuthService } from '../../../services/auth.service';
import { Booking } from '../../../models/booking.model';

@Component({
  selector: 'app-customer-bookings',
  imports: [CommonModule, RouterLink],
  templateUrl: './bookings.html',
  styleUrl: './bookings.css',
})
export class CustomerBookings implements OnInit {
  private auth = inject(AuthService);
  private customerService = inject(CustomerService);
  private bookingService = inject(BookingService);

  bookings: Booking[] = [];
  loading = true;

  ngOnInit() {
    const userId = 1;
    if (userId) {
      this.customerService.getCustomerByUserId(userId).subscribe({
        next: (customer) => {
          if (customer.id) {
            this.bookingService.getByCustomer(customer.id).subscribe({
              next: (data) => {
                this.bookings = data;
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
