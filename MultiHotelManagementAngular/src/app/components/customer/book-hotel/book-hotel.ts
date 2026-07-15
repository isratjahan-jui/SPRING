import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { HotelService } from '../../../services/hotel.service';
import { RoomService } from '../../../services/room.service';
import { BookingService } from '../../../services/booking.service';
import { AuthService } from '../../../services/auth.service';
import { CustomerService } from '../../../services/customer.service';
import { Hotel } from '../../../models/hotel.model';
import { Room } from '../../../models/room.model';

@Component({
  selector: 'app-book-hotel',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './book-hotel.html',
  styleUrl: './book-hotel.css',
})
export class BookHotel implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private hotelService = inject(HotelService);
  private roomService = inject(RoomService);
  private bookingService = inject(BookingService);
  private auth = inject(AuthService);
  private customerService = inject(CustomerService);
  private cdr = inject(ChangeDetectorRef);

  hotel?: Hotel;
  room?: Room;
  customerId: number | null = null;

  checkInDate = '';
  checkOutDate = '';
  numberOfRooms = 1;
  totalGuests = 1;
  loading = true;
  submitting = false;
  successMessage = '';
  errorMessage = '';

  ngOnInit() {
    const hotelId = Number(this.route.snapshot.paramMap.get('hotelId'));
    const roomId = Number(this.route.snapshot.paramMap.get('roomId'));

    const userId = this.auth.getUser()?.userId;
    if (userId) {
      this.customerService.getCustomerByUserId(userId).subscribe({
        next: (c) => {
          this.customerId = c.id ?? null;
          this.cdr.markForCheck();
        },
      });
    }

    this.hotelService.getById(hotelId).subscribe({
      next: (data) => {
        this.hotel = data;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Failed to load hotel.';
        this.cdr.markForCheck();
      },
    });

    this.roomService.getById(roomId).subscribe({
      next: (data) => {
        this.room = data;
        this.totalGuests = data.adults + data.children;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Failed to load room details.';
        this.cdr.markForCheck();
      },
    });
  }

  get today(): string {
    const d = new Date();
    return d.toISOString().split('T')[0];
  }

  get minCheckOut(): string {
    if (!this.checkInDate) return this.today;
    const d = new Date(this.checkInDate);
    d.setDate(d.getDate() + 1);
    return d.toISOString().split('T')[0];
  }

  calculateNights(): number {
    if (!this.checkInDate || !this.checkOutDate) return 0;
    const diff = new Date(this.checkOutDate).getTime() - new Date(this.checkInDate).getTime();
    return Math.max(1, Math.ceil(diff / (1000 * 60 * 60 * 24)));
  }

  submit() {
    if (!this.customerId || !this.hotel || !this.room) return;
    if (!this.checkInDate || !this.checkOutDate) {
      this.errorMessage = 'Please select check-in and check-out dates.';
      return;
    }
    if (this.numberOfRooms < 1 || !Number.isInteger(this.numberOfRooms)) {
      this.errorMessage = 'Number of rooms must be a positive integer.';
      return;
    }
    if (this.numberOfRooms > (this.room.availableRooms || 1)) {
      this.errorMessage = `Only ${this.room.availableRooms} room(s) available.`;
      return;
    }

    this.submitting = true;
    this.errorMessage = '';

    this.bookingService
      .create({
        customerId: this.customerId,
        hotelId: this.hotel.id,
        roomId: this.room.id,
        checkInDate: this.checkInDate,
        checkOutDate: this.checkOutDate,
        numberOfRooms: this.numberOfRooms,
        totalGuests: this.totalGuests,
        discountRate: 0,
        advanceAmount: 0,
      })
      .subscribe({
        next: () => {
          this.successMessage = 'Booking confirmed successfully!';
          this.submitting = false;
          this.cdr.markForCheck();
          setTimeout(() => this.router.navigate(['/customer/bookings']), 2000);
        },
        error: (err) => {
          this.submitting = false;
          this.errorMessage = err.error?.message || 'Booking failed. Please try again.';
          this.cdr.markForCheck();
        },
      });
  }
}
