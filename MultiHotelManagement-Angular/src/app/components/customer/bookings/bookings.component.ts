import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { AuthService } from '../../../services/auth.service';
import { BookingService } from '../../../services/booking.service';
import { Booking } from '../../../models/booking.model';

@Component({
  selector: 'app-customer-bookings',
  standalone: true,
  imports: [RouterLink, DatePipe],
  templateUrl: './bookings.component.html',
  styleUrls: ['./bookings.component.css'],
})
export class CustomerBookingsComponent implements OnInit {
  private auth = inject(AuthService);
  private bookingSvc = inject(BookingService);
  bookings: Booking[] = [];

  ngOnInit() {
    this.bookingSvc.getCustomerByUserId(this.auth.userId()!).subscribe(cust => {
      this.bookingSvc.getByCustomer(cust.id).subscribe(b => this.bookings = b);
    });
  }
}
