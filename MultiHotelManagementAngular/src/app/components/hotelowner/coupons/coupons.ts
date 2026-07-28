import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CouponRequest, CouponResponse } from '../../../models/coupon.model';
import { CouponService } from '../../../services/coupon.service';
import { AuthService } from '../../../services/auth.service';
import { HotelService } from '../../../services/hotel.service';
import { Hotel } from '../../../models/hotel.model';

@Component({
  selector: 'app-owner-coupons',
  imports: [CommonModule, FormsModule],
  templateUrl: './coupons.html',
  styleUrl: './coupons.css',
})
export class OwnerCoupons implements OnInit {
  private couponService = inject(CouponService);
  private auth = inject(AuthService);
  private hotelService = inject(HotelService);
  private cdr = inject(ChangeDetectorRef);

  hotels: Hotel[] = [];
  coupons: CouponResponse[] = [];
  selectedHotelId = 0;
  loading = false;

  showForm = false;
  form: CouponRequest = {
    code: '',
    discountPercent: 0,
    discountAmount: 0,
    validFrom: '',
    validUntil: '',
    hotelId: 0,
  };
  submitting = false;
  formError = '';

  ngOnInit() {
    const ownerId = this.auth.getUser()?.ownerId;
    if (ownerId) {
      this.hotelService.getByOwner(ownerId).subscribe({
        next: (data) => {
          this.hotels = data;
          this.cdr.markForCheck();
        },
      });
    }
  }

  onHotelChange() {
    this.coupons = [];
    this.showForm = false;
    if (this.selectedHotelId) {
      this.loading = true;
      this.couponService.getByHotel(this.selectedHotelId).subscribe({
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
  }

  openCreate() {
    this.form = {
      code: '',
      discountPercent: 0,
      discountAmount: 0,
      validFrom: '',
      validUntil: '',
      hotelId: this.selectedHotelId || this.hotels[0]?.id || 0,
    };
    this.formError = '';
    this.showForm = true;
  }

  cancelForm() {
    this.showForm = false;
  }

  submitForm() {
    if (!this.form.code || !this.form.validFrom || !this.form.validUntil) {
      this.formError = 'Code, Valid From, and Valid Until are required.';
      return;
    }
    this.submitting = true;
    this.formError = '';

    this.couponService.create(this.form).subscribe({
      next: (created) => {
        this.coupons.unshift(created);
        this.showForm = false;
        this.submitting = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.formError = err.error?.message || 'Failed to create coupon.';
        this.submitting = false;
        this.cdr.markForCheck();
      },
    });
  }

  deactivate(id: number) {
    if (!confirm('Deactivate this coupon? It will no longer be usable.')) return;
    this.couponService.deactivate(id).subscribe({
      next: () => {
        const c = this.coupons.find((x) => x.id === id);
        if (c) c.active = false;
        this.cdr.markForCheck();
      },
      error: () => alert('Failed to deactivate coupon'),
    });
  }
}
