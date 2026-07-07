import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { AuthService } from '../../../services/auth.service';
import { BookingService } from '../../../services/booking.service';
import { HotelService } from '../../../services/hotel.service';
import { Booking } from '../../../models/booking.model';
import { Hotel } from '../../../models/hotel.model';

@Component({
  selector: 'app-owner-bookings',
  standalone: true,
  imports: [FormsModule, DatePipe],
  templateUrl: './bookings.component.html',
  styleUrls: ['./bookings.component.css'],
})
export class OwnerBookingsComponent implements OnInit {
  private auth = inject(AuthService);
  private bookingSvc = inject(BookingService);
  private hotelSvc = inject(HotelService);

  hotels: Hotel[] = [];
  bookings: Booking[] = [];
  selectedHotelId = '';

  ngOnInit() {
    const oid = this.auth.user()?.ownerId;
    if (oid) this.hotelSvc.getByOwner(oid).subscribe(d => this.hotels = d);
  }

  loadBookings() {
    if (this.selectedHotelId) {
      this.bookingSvc.getByHotel(Number(this.selectedHotelId)).subscribe(d => this.bookings = d);
    }
  }
}
