import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { LoginResponse } from '../../../models/auth.model';
import { HotelOwner } from '../../../models/hotel-owner.model';
import { KEYS, StorageService } from '../../../services/storage.service';
import { HotelOwnerService } from '../../../services/hotel-owner.service';
import { OwnerDashboardService } from '../../../services/owner-dashboard.service';
import { BookingService } from '../../../services/booking.service';
import { OwnerDashboardStats } from '../../../models/owner-dashboard.model';
import { Booking } from '../../../models/booking.model';
import { CommonModule } from '@angular/common';
import { environment } from '../../../../environments/environments';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-owner-dashboard',
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class OwnerDashboard implements OnInit {
  imageUrl = environment.imageBaseUrl + '/owners/';

  user: LoginResponse | null = null;
  userId!: number;
  ownerId = 0;
  owner: HotelOwner | null = null;
  stats: OwnerDashboardStats | null = null;
  todaysArrivals: Booking[] = [];

  private auth = inject(AuthService);
  private ownerService = inject(HotelOwnerService);
  private dashboardService = inject(OwnerDashboardService);
  private bookingService = inject(BookingService);
  private storage = inject(StorageService);
  private cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.user = this.storage.getUser();
    if (this.user?.userId) {
      this.userId = this.user.userId;
    }
    this.loadOwner();
  }

  loadOwner(): void {
    this.ownerService.getOwnerByUserId(this.userId).subscribe({
      next: (res) => {
        this.owner = res;
        this.ownerId = res.id ?? 0;
        this.storage.saveData(KEYS.HOTEL_OWNER, res);
        this.cdr.markForCheck();
        if (this.ownerId) {
          this.loadStats();
          this.loadTodaysArrivals();
        }
      },
      error: (err) => console.error(err),
    });
  }

  loadStats(): void {
    this.dashboardService.getStats(this.ownerId).subscribe({
      next: (res) => {
        this.stats = res;
        this.cdr.markForCheck();
      },
      error: (err) => console.error('Failed to load dashboard stats', err),
    });
  }

  loadTodaysArrivals(): void {
    const today = new Date();
    const todayStr = today.toISOString().split('T')[0];

    this.bookingService.getByOwner(this.ownerId).subscribe({
      next: (bookings) => {
        this.todaysArrivals = bookings.filter((b) => {
          const checkIn = new Date(b.checkInDate);
          const checkInStr = checkIn.toISOString().split('T')[0];
          return checkInStr === todayStr && (b.status === 'CONFIRMED' || b.status === 'PENDING');
        });
        this.cdr.markForCheck();
      },
      error: () => {
        this.todaysArrivals = [];
        this.cdr.markForCheck();
      },
    });
  }
}
