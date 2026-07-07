import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { BookingService } from '../../../services/booking.service';
import { WishlistService } from '../../../services/wishlist.service';
import { Booking } from '../../../models/booking.model';

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class CustomerDashboardComponent implements OnInit {
  auth = inject(AuthService);
  private bookingSvc = inject(BookingService);
  private wishlistSvc = inject(WishlistService);

  bookings: Booking[] = [];
  wishlistCount = 0;

  ngOnInit() {
    if (this.auth.isLoggedIn()) {
      this.bookingSvc.getCustomerByUserId(this.auth.userId()!).subscribe(cust => {
        this.bookingSvc.getByCustomer(cust.id).subscribe(b => this.bookings = b);
        this.wishlistSvc.getByCustomerId(cust.id).subscribe(w => this.wishlistCount = w.length);
      });
    }
  }
}
