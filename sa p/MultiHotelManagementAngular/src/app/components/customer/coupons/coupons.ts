import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CouponService } from '../../../services/coupon.service';
import { HotelService } from '../../../services/hotel.service';
import { CouponResponse } from '../../../models/coupon.model';
import { Hotel } from '../../../models/hotel.model';

@Component({
  selector: 'app-customer-coupons',
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './coupons.html',
  styleUrl: './coupons.css',
})
export class CustomerCoupons implements OnInit {
  private couponService = inject(CouponService);
  private hotelService = inject(HotelService);
  private cdr = inject(ChangeDetectorRef);

  coupons: CouponResponse[] = [];
  filteredCoupons: CouponResponse[] = [];
  hotels: Hotel[] = [];
  loading = true;

  selectedHotelId: number | null = null;
  searchText: string = '';

  ngOnInit() {
    this.hotelService.getAllApproved().subscribe({
      next: (hotels) => {
        this.hotels = hotels;
        this.cdr.markForCheck();
      },
      error: () => {},
    });

    this.couponService.getAllActive().subscribe({
      next: (data) => {
        this.coupons = data;
        this.filteredCoupons = data;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  applyFilters() {
    let result = [...this.coupons];

    if (this.selectedHotelId !== null) {
      result = result.filter((c) => c.hotelId === this.selectedHotelId);
    }

    if (this.searchText.trim()) {
      const s = this.searchText.toLowerCase();
      result = result.filter(
        (c) =>
          (c.code && c.code.toLowerCase().includes(s)) ||
          (c.hotelName && c.hotelName.toLowerCase().includes(s))
      );
    }

    this.filteredCoupons = result;
  }

  clearFilters() {
    this.selectedHotelId = null;
    this.searchText = '';
    this.filteredCoupons = [...this.coupons];
  }

  isValid(coupon: CouponResponse): boolean {
    const now = new Date();
    const validFrom = new Date(coupon.validFrom);
    const validUntil = new Date(coupon.validUntil);
    return coupon.active && now >= validFrom && now <= validUntil;
  }
}
