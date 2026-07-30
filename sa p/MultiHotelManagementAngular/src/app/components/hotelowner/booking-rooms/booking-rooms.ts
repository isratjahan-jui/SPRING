import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookingRoom, BookingRoomRequest } from '../../../models/booking-room.model';
import { BookingRoomService } from '../../../services/booking-room.service';
import { BookingService } from '../../../services/booking.service';
import { HotelService } from '../../../services/hotel.service';
import { AuthService } from '../../../services/auth.service';
import { Hotel } from '../../../models/hotel.model';
import { Booking } from '../../../models/booking.model';

@Component({
  selector: 'app-owner-booking-rooms',
  imports: [CommonModule, FormsModule],
  templateUrl: './booking-rooms.html',
  styleUrl: './booking-rooms.css',
})
export class OwnerBookingRooms implements OnInit {
  private bookingRoomService = inject(BookingRoomService);
  private bookingService = inject(BookingService);
  private hotelService = inject(HotelService);
  private authService = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);

  allBookingRooms: BookingRoom[] = [];
  filteredBookingRooms: BookingRoom[] = [];
  hotels: Hotel[] = [];
  loading = true;

  selectedHotelId: number | null = null;
  selectedStatus: string = '';
  searchText: string = '';

  totalRooms = 0;
  totalPrice = 0;

  editingId: number | null = null;
  editForm: { numberOfRooms: number; adults: number; children: number } = {
    numberOfRooms: 1,
    adults: 1,
    children: 0,
  };

  statusOptions = [
    'PENDING', 'CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLED', 'NO_SHOW', 'EXPIRED'
  ];

  ngOnInit() {
    const ownerId = this.authService.getUser()?.ownerId;
    if (ownerId) {
      this.loadAll(ownerId);
    } else {
      this.loading = false;
    }
  }

  private loadAll(ownerId: number) {
    this.loading = true;

    this.hotelService.getByOwner(ownerId).subscribe({
      next: (hotels) => {
        this.hotels = hotels;

        this.bookingService.getByOwner(ownerId).subscribe({
          next: (bookings) => {
            if (bookings.length === 0) {
              this.loading = false;
              this.cdr.markForCheck();
              return;
            }
            this.loadBookingRooms(bookings);
          },
          error: () => {
            this.loading = false;
            this.cdr.markForCheck();
          },
        });
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  private loadBookingRooms(bookings: Booking[]) {
    let pending = bookings.length;
    const all: BookingRoom[] = [];

    for (const booking of bookings) {
      this.bookingRoomService.getByBookingId(booking.id).subscribe({
        next: (rooms) => {
          all.push(...rooms);
          if (--pending === 0) {
            this.allBookingRooms = all;
            this.applyFilters();
            this.loading = false;
            this.cdr.markForCheck();
          }
        },
        error: () => {
          if (--pending === 0) {
            this.allBookingRooms = all;
            this.applyFilters();
            this.loading = false;
            this.cdr.markForCheck();
          }
        },
      });
    }
  }

  applyFilters() {
    let result = [...this.allBookingRooms];

    if (this.selectedHotelId !== null) {
      result = result.filter((r) => r.hotelId === this.selectedHotelId);
    }

    if (this.selectedStatus) {
      result = result.filter((r) => r.bookingStatus === this.selectedStatus);
    }

    if (this.searchText.trim()) {
      const s = this.searchText.toLowerCase();
      result = result.filter(
        (r) =>
          (r.customerName && r.customerName.toLowerCase().includes(s)) ||
          (r.roomType && r.roomType.toLowerCase().includes(s)) ||
          (r.hotelName && r.hotelName.toLowerCase().includes(s)) ||
          String(r.bookingId).includes(s)
      );
    }

    this.filteredBookingRooms = result;
    this.calculateStats();
  }

  private calculateStats() {
    this.totalRooms = this.filteredBookingRooms.reduce((s, r) => s + r.numberOfRooms, 0);
    this.totalPrice = this.filteredBookingRooms.reduce((s, r) => s + (r.price || 0), 0);
  }

  onFilterChange() {
    this.applyFilters();
  }

  clearFilters() {
    this.selectedHotelId = null;
    this.selectedStatus = '';
    this.searchText = '';
    this.applyFilters();
  }

  startEdit(br: BookingRoom) {
    this.editingId = br.id;
    this.editForm = {
      numberOfRooms: br.numberOfRooms,
      adults: br.adults,
      children: br.children,
    };
  }

  cancelEdit() {
    this.editingId = null;
  }

  saveEdit(id: number) {
    const br = this.allBookingRooms.find((r) => r.id === id);
    if (!br) return;

    const request: BookingRoomRequest = {
      bookingId: br.bookingId,
      roomId: br.roomId,
      numberOfRooms: this.editForm.numberOfRooms,
      adults: this.editForm.adults,
      children: this.editForm.children,
    };

    this.bookingRoomService.update(id, request).subscribe({
      next: (updated) => {
        const idx = this.allBookingRooms.findIndex((r) => r.id === id);
        if (idx >= 0) this.allBookingRooms[idx] = updated;
        this.editingId = null;
        this.applyFilters();
        this.cdr.markForCheck();
      },
      error: (err) => {
        alert(err.error?.message || 'Update failed');
      },
    });
  }

  deleteBookingRoom(id: number) {
    if (!confirm('Are you sure you want to delete this booking room entry?')) return;

    this.bookingRoomService.delete(id).subscribe({
      next: () => {
        this.allBookingRooms = this.allBookingRooms.filter((r) => r.id !== id);
        this.applyFilters();
        this.cdr.markForCheck();
      },
      error: (err) => {
        alert(err.error?.message || 'Delete failed');
      },
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'CONFIRMED':
        return 'bg-success';
      case 'PENDING':
        return 'bg-warning text-dark';
      case 'CHECKED_IN':
        return 'bg-info text-dark';
      case 'CHECKED_OUT':
        return 'bg-secondary';
      case 'CANCELLED':
        return 'bg-danger';
      case 'NO_SHOW':
        return 'bg-dark';
      case 'EXPIRED':
        return 'bg-orange text-white';
      default:
        return 'bg-light text-dark';
    }
  }

  formatDate(date: string): string {
    if (!date) return '—';
    const d = new Date(date);
    return d.toLocaleDateString('en-BD', { day: '2-digit', month: 'short', year: 'numeric' });
  }
}
