import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Booking } from '../../../models/booking.model';
import { Hotel } from '../../../models/hotel.model';
import { BookingService } from '../../../services/booking.service';
import { HotelService } from '../../../services/hotel.service';

@Component({
  selector: 'app-owner-bookings',
  imports: [CommonModule, FormsModule],
  templateUrl: './bookings.html',
  styleUrl: './bookings.css',
})
export class OwnerBookings implements OnInit {
  hotels: Hotel[] = [];
  bookings: Booking[] = [];
  selectedHotelId = 0;

  constructor(
    private bookingService: BookingService,
    private hotelService: HotelService,
  ) {}

  ngOnInit() {
    const ownerId = localStorage.getItem('ownerId');
    if (ownerId) {
      this.hotelService.getByOwner(Number(ownerId)).subscribe({
        next: (data) => (this.hotels = data),
        error: () => alert('Failed to load hotels'),
      });
    }
  }

  onHotelChange() {
    this.bookings = [];
    if (this.selectedHotelId) {
      this.bookingService.getByHotel(this.selectedHotelId).subscribe({
        next: (data) => (this.bookings = data),
        error: () => alert('Failed to load bookings'),
      });
    }
  }
}
