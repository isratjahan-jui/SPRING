import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { BookingRoom } from '../../../models/booking-room.model';
import { Hotel } from '../../../models/hotel.model';
import { Booking } from '../../../models/booking.model';
import { HotelService } from '../../../services/hotel.service';
import { BookingService } from '../../../services/booking.service';
import { AuthService } from '../../../services/auth.service';
import { environment } from '../../../../environments/environments';

interface HotelBookingRooms {
  hotel: Hotel;
  bookingRooms: (BookingRoom & { bookingId: number; roomType: string; bookingStatus: string })[];
}

@Component({
  selector: 'app-owner-booking-rooms',
  imports: [CommonModule],
  templateUrl: './booking-rooms.html',
  styleUrl: './booking-rooms.css',
})
export class OwnerBookingRooms implements OnInit {
  hotelBookingRooms: HotelBookingRooms[] = [];
  totalRooms = 0;
  totalPrice = 0;
  totalBookingRoomEntries = 0;
  loading = false;

  private authService = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);
  private apiUrl = environment.apiUrl;

  constructor(
    private hotelService: HotelService,
    private bookingService: BookingService,
    private http: HttpClient,
  ) {}

  ngOnInit() {
    const ownerId = this.authService.getUser()?.ownerId;
    if (ownerId) {
      this.loading = true;
      this.hotelService.getByOwner(ownerId).subscribe({
        next: (hotels) => {
          this.loadBookingRooms(hotels);
        },
        error: () => {
          this.loading = false;
          alert('Failed to load hotels');
        },
      });
    }
  }

  private loadBookingRooms(hotels: Hotel[]) {
    if (hotels.length === 0) {
      this.loading = false;
      this.cdr.markForCheck();
      return;
    }

    let pending = hotels.length;

    for (const hotel of hotels) {
      this.bookingService.getByHotel(hotel.id).subscribe({
        next: (bookings) => {
          if (bookings.length === 0) {
            if (--pending === 0) this.finishLoading();
            return;
          }
          this.fetchBookingRoomsForHotel(hotel, bookings, () => {
            if (--pending === 0) this.finishLoading();
          });
        },
        error: () => {
          if (--pending === 0) this.finishLoading();
        },
      });
    }
  }

  private fetchBookingRoomsForHotel(hotel: Hotel, bookings: Booking[], done: () => void) {
    let bookingPending = bookings.length;
    const allRooms: HotelBookingRooms['bookingRooms'] = [];

    for (const booking of bookings) {
      this.http.get<BookingRoom[]>(`${this.apiUrl}booking-rooms/booking/${booking.id}`).subscribe({
        next: (rooms) => {
          for (const room of rooms) {
            allRooms.push({
              ...room,
              bookingId: booking.id,
              roomType: booking.roomType,
              bookingStatus: booking.status,
            });
          }
          if (--bookingPending === 0) {
            if (allRooms.length > 0) {
              this.hotelBookingRooms.push({ hotel, bookingRooms: allRooms });
            }
            done();
          }
        },
        error: () => {
          if (--bookingPending === 0) {
            if (allRooms.length > 0) {
              this.hotelBookingRooms.push({ hotel, bookingRooms: allRooms });
            }
            done();
          }
        },
      });
    }
  }

  private finishLoading() {
    this.totalBookingRoomEntries = this.hotelBookingRooms.reduce(
      (sum, h) => sum + h.bookingRooms.length,
      0,
    );
    this.totalRooms = this.hotelBookingRooms.reduce(
      (sum, h) => sum + h.bookingRooms.reduce((s, r) => s + r.numberOfRooms, 0),
      0,
    );
    this.totalPrice = this.hotelBookingRooms.reduce(
      (sum, h) => sum + h.bookingRooms.reduce((s, r) => s + r.price, 0),
      0,
    );
    this.loading = false;
    this.cdr.markForCheck();
  }

  getTotalRoomsForHotel(group: HotelBookingRooms): number {
    return group.bookingRooms.reduce((s, r) => s + r.numberOfRooms, 0);
  }
}
