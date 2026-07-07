import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HotelService } from '../../../services/hotel.service';
import { RoomService } from '../../../services/room.service';
import { BookingService } from '../../../services/booking.service';
import { AuthService } from '../../../services/auth.service';
import { Hotel } from '../../../models/hotel.model';
import { Room } from '../../../models/room.model';

@Component({
  selector: 'app-hotel-detail',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './hotel-detail.component.html',
  styleUrls: ['./hotel-detail.component.css'],
})
export class HotelDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private hotelService = inject(HotelService);
  private roomService = inject(RoomService);
  private bookingService = inject(BookingService);
  private router = inject(Router);
  auth = inject(AuthService);

  hotel?: Hotel;
  rooms: Room[] = [];
  selectedRoomId: number | null = null;
  selectedRoom: Room | null = null;
  loading = false;
  message = '';
  error = '';
  customerId: number | null = null;

  bookingForm = { name: '', phone: '', checkIn: '', checkOut: '', guests: 1, rooms: 1 };

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.hotelService.getById(id).subscribe(d => this.hotel = d);
    this.roomService.getAvailable(id).subscribe(d => this.rooms = d);

    if (this.auth.isLoggedIn() && this.auth.role() === 'CUSTOMER') {
      this.bookingService.getCustomerByUserId(this.auth.userId()!).subscribe(d => this.customerId = d.id);
    }
  }

  get totalPrice(): number {
    if (!this.selectedRoom || !this.bookingForm.checkIn || !this.bookingForm.checkOut) return 0;
    const days = Math.max(1, Math.ceil((new Date(this.bookingForm.checkOut).getTime() - new Date(this.bookingForm.checkIn).getTime()) / (1000 * 3600 * 24)));
    return this.selectedRoom.pricePerNight * days * this.bookingForm.rooms;
  }

  selectRoom(room: Room) {
    this.selectedRoom = room;
    this.selectedRoomId = room.id;
  }

  bookNow() {
    if (!this.customerId || !this.selectedRoomId || !this.hotel) return;
    this.loading = true;
    this.error = '';
    this.message = '';

    this.bookingService.create({
      contractPersonName: this.bookingForm.name,
      phone: this.bookingForm.phone,
      checkInDate: this.bookingForm.checkIn,
      checkOutDate: this.bookingForm.checkOut,
      totalGuests: this.bookingForm.guests,
      numberOfRooms: this.bookingForm.rooms,
      customerId: this.customerId,
      hotelId: this.hotel.id,
      roomId: this.selectedRoomId,
    }).subscribe({
      next: (res) => {
        this.message = `Booking successful! (ID: ${res.id})`;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Booking failed';
        this.loading = false;
      },
    });
  }
}
