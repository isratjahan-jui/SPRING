import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { HotelService } from '../../../services/hotel.service';
import { Hotel } from '../../../models/hotel.model';
import { WishlistService } from '../../../services/wishlist.service';
import { AuthService } from '../../../services/auth.service';
import { CustomerService } from '../../../services/customer.service';
import { environment } from '../../../../environments/environments';

@Component({
  selector: 'app-hotel-list',
  imports: [CommonModule, RouterLink],
  templateUrl: './hotel-list.html',
  styleUrl: './hotel-list.css',
})
export class HotelList implements OnInit {
  hotels: Hotel[] = [];
  imageBaseUrl = environment.imageBaseUrl;

  private hotelService = inject(HotelService);
  private cdr = inject(ChangeDetectorRef);
  private auth = inject(AuthService);
  private customerService = inject(CustomerService);
  private wishlistService = inject(WishlistService);

  isCustomer = false;
  customerId: number | null = null;
  wishlistHotelIds = new Set<number>();
  togglingHotelId: number | null = null;

  ngOnInit(): void {
    const role = this.auth.getRole();
    this.isCustomer = role === 'CUSTOMER';

    if (this.isCustomer) {
      const userId = this.auth.getUser()?.userId;
      if (userId) {
        this.customerService.getCustomerByUserId(userId).subscribe({
          next: (c) => {
            this.customerId = c.id ?? null;
            if (this.customerId) {
              this.loadWishlist();
            }
            this.cdr.markForCheck();
          },
          error: () => {},
        });
      }
    }

    this.loadHotels();
  }

  loadHotels() {
    this.hotelService.getAllApproved().subscribe((data) => {
      this.hotels = data;
      this.cdr.markForCheck();
    });
  }

  loadWishlist() {
    if (!this.customerId) return;
    this.wishlistService.getByCustomer(this.customerId).subscribe({
      next: (items) => {
        this.wishlistHotelIds = new Set(items.map((i) => i.hotelId));
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  isInWishlist(hotelId: number): boolean {
    return this.wishlistHotelIds.has(hotelId);
  }

  toggleWishlist(hotelId: number) {
    if (!this.customerId || this.togglingHotelId !== null) return;
    const userId = this.auth.getUser()?.userId;
    if (!userId) return;

    this.togglingHotelId = hotelId;

    if (this.isInWishlist(hotelId)) {
      this.wishlistService.getByCustomer(this.customerId).subscribe({
        next: (items) => {
          const match = items.find((i) => i.hotelId === hotelId);
          if (match) {
            this.wishlistService.delete(match.id).subscribe({
              next: () => {
                this.wishlistHotelIds.delete(hotelId);
                this.togglingHotelId = null;
                this.cdr.markForCheck();
              },
              error: () => {
                this.togglingHotelId = null;
                this.cdr.markForCheck();
              },
            });
          } else {
            this.wishlistHotelIds.delete(hotelId);
            this.togglingHotelId = null;
            this.cdr.markForCheck();
          }
        },
        error: () => {
          this.togglingHotelId = null;
          this.cdr.markForCheck();
        },
      });
    } else {
      this.wishlistService.create({ userId, customerId: this.customerId, hotelId }).subscribe({
        next: () => {
          this.wishlistHotelIds.add(hotelId);
          this.togglingHotelId = null;
          this.cdr.markForCheck();
        },
        error: () => {
          this.togglingHotelId = null;
          this.cdr.markForCheck();
        },
      });
    }
  }
}
