import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CouponService } from '../../../services/coupon.service';
import { CouponResponse } from '../../../models/coupon.model';

@Component({
  selector: 'app-customer-coupons',
  imports: [CommonModule, RouterLink],
  templateUrl: './coupons.html',
  styleUrl: './coupons.css',
})
export class CustomerCoupons implements OnInit {
  private couponService = inject(CouponService);
  private cdr = inject(ChangeDetectorRef);

  coupons: CouponResponse[] = [];
  loading = true;

  ngOnInit() {
    this.couponService.getAllActive().subscribe({
      next: (data) => {
        this.coupons = data;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  isValid(coupon: CouponResponse): boolean {
    const now = new Date();
    const validFrom = new Date(coupon.validFrom);
    const validUntil = new Date(coupon.validUntil);
    return coupon.active && now >= validFrom && now <= validUntil;
  }
}
