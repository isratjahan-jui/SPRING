import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { HotelService } from '../../../services/hotel.service';
import { RoomService, RoomAvailabilityResponse } from '../../../services/room.service';
import { BookingService } from '../../../services/booking.service';
import { AuthService } from '../../../services/auth.service';
import { CustomerService } from '../../../services/customer.service';
import { CouponService } from '../../../services/coupon.service';
import { DealService } from '../../../services/deal.service';
import { HotelExtraServiceService } from '../../../services/hotel-extra-service.service';
import { HotelDetailsService } from '../../../services/hotel-details.service';
import { Hotel } from '../../../models/hotel.model';
import { Room } from '../../../models/room.model';
import { DealResponse } from '../../../models/deal.model';
import { HotelExtraService } from '../../../models/hotel-extra-service.model';
import { HotelDetails } from '../../../models/hotel-details.model';
import { environment } from '../../../../environments/environments';

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
  private couponService = inject(CouponService);
  private dealService = inject(DealService);
  private extraServiceService = inject(HotelExtraServiceService);
  private hotelDetailsService = inject(HotelDetailsService);
  private cdr = inject(ChangeDetectorRef);

  hotel?: Hotel;
  hotelDetails?: HotelDetails;
  room?: Room;
  customerId: number | null = null;
  imageBaseUrl = environment.imageBaseUrl;

  checkInDate = '';
  checkOutDate = '';
  numberOfRooms = 1;
  totalGuests = 1;
  discountRate = 0;
  advanceAmount = 0;
  loading = true;
  submitting = false;
  successMessage = '';
  errorMessage = '';

  hotelPaymentOption = 'ADVANCE';
  depositPercentage = 20;
  showDepositInfo = false;

  deals: DealResponse[] = [];
  appliedDeal: DealResponse | null = null;
  couponCode = '';
  couponApplied = false;
  couponDiscount = 0;
  couponError = '';
  applyingCoupon = false;

  extraServices: HotelExtraService[] = [];
  selectedExtraServiceIds: Set<number> = new Set();

  availability: RoomAvailabilityResponse | null = null;
  checkingAvailability = false;

  selectedPaymentMethod = 'pay_at_hotel';

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

    this.dealService.getByHotel(hotelId).subscribe({
      next: (data) => {
        this.deals = data;
        this.autoApplyBestDeal();
        this.cdr.markForCheck();
      },
    });

    this.extraServiceService.getActiveByHotel(hotelId).subscribe({
      next: (data) => {
        this.extraServices = data;
        this.cdr.markForCheck();
      },
    });

    this.hotelDetailsService.getByHotelId(hotelId).subscribe({
      next: (data) => {
        this.hotelDetails = data;
        this.initPaymentOption();
        this.cdr.markForCheck();
      },
    });
  }

  get today(): string {
    const d = new Date();
    return d.toISOString().split('T')[0];
  }

  private initPaymentOption() {
    if (!this.hotelDetails) return;
    this.hotelPaymentOption = this.hotelDetails.paymentOption || 'ADVANCE';
    this.depositPercentage = this.hotelDetails.depositPercentage || 20;
    this.showDepositInfo = this.hotelPaymentOption !== 'ADVANCE';
    if (this.hotelPaymentOption === 'ADVANCE') {
      this.advanceAmount = this.estimatedTotal;
    }
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

  get roomSubtotal(): number {
    if (!this.checkInDate || !this.checkOutDate || !this.room) return 0;
    return this.calculateNights() * this.room.price * this.numberOfRooms;
  }

  get effectiveDiscountRate(): number {
    let rate = this.discountRate;
    if (this.couponApplied) {
      rate += this.couponDiscount;
    }
    return rate;
  }

  get discountAmount(): number {
    return (this.roomSubtotal * this.effectiveDiscountRate) / 100;
  }

  get extraServicesTotal(): number {
    let total = 0;
    this.extraServices.forEach((s) => {
      if (this.selectedExtraServiceIds.has(s.id)) {
        total += s.price;
      }
    });
    return total;
  }

  get estimatedTotal(): number {
    return this.roomSubtotal - this.discountAmount + this.extraServicesTotal;
  }

  get dueAfterAdvance(): number {
    return Math.max(0, this.estimatedTotal - this.advanceAmount);
  }

  get depositAmount(): number {
    if (this.hotelPaymentOption === 'ADVANCE') return this.estimatedTotal;
    return Math.round((this.estimatedTotal * this.depositPercentage) / 100);
  }

  private autoApplyBestDeal() {
    if (!this.deals.length || !this.room) return;
    const now = new Date();
    const applicable = this.deals.filter((d) => {
      const start = new Date(d.startDate);
      const end = new Date(d.endDate);
      if (d.roomType && d.roomType !== this.room!.roomType) return false;
      return d.isActive && now >= start && now <= end;
    });
    if (!applicable.length) return;
    const best = applicable.reduce((a, b) => {
      const aVal = a.discountPercent || (a.discountAmount ? 1 : 0);
      const bVal = b.discountPercent || (b.discountAmount ? 1 : 0);
      return bVal > aVal ? b : a;
    });
    this.appliedDeal = best;
    if (best.discountPercent) {
      this.discountRate = best.discountPercent;
    }
  }

  submit() {
    if (!this.customerId || !this.hotel || !this.room) return;
    if (!this.checkInDate || !this.checkOutDate) {
      this.errorMessage = 'Please select check-in and check-out dates.';
      return;
    }
    if (new Date(this.checkOutDate) <= new Date(this.checkInDate)) {
      this.errorMessage = 'Check-out date must be after check-in date.';
      return;
    }
    if (this.numberOfRooms < 1 || !Number.isInteger(this.numberOfRooms)) {
      this.errorMessage = 'Number of rooms must be a positive integer.';
      return;
    }
    if (this.availability && this.numberOfRooms > this.availability.availableForDates) {
      this.errorMessage = `Only ${this.availability.availableForDates} room(s) available for the selected dates.`;
      return;
    }
    if (!this.availability && this.numberOfRooms > (this.room.availableRooms || 1)) {
      this.errorMessage = `Only ${this.room.availableRooms} room(s) available.`;
      return;
    }
    if (this.totalGuests < 1) {
      this.errorMessage = 'At least 1 guest is required.';
      return;
    }

    this.submitting = true;
    this.errorMessage = '';

    const finalDiscountRate = this.effectiveDiscountRate;

    const request: any = {
      customerId: this.customerId,
      hotelId: this.hotel.id,
      roomId: this.room.id,
      checkInDate: this.checkInDate,
      checkOutDate: this.checkOutDate,
      numberOfRooms: this.numberOfRooms,
      totalGuests: this.totalGuests,
      discountRate: finalDiscountRate,
      advanceAmount:
        this.hotelPaymentOption === 'ADVANCE' ? this.estimatedTotal : this.depositAmount,
    };
    if (this.selectedExtraServiceIds.size > 0) {
      request.extraServiceIds = Array.from(this.selectedExtraServiceIds);
    }

    this.bookingService.create(request).subscribe({
      next: (createdBooking) => {
        this.submitting = false;
        this.successMessage = 'Booking confirmed successfully!';
        this.cdr.markForCheck();

        if (this.selectedPaymentMethod === 'pay_at_hotel') {
          setTimeout(() => this.router.navigate(['/customer/bookings']), 1500);
        } else {
          setTimeout(() => this.router.navigate(['/customer/pay', createdBooking.id]), 1000);
        }
      },
      error: (err) => {
        this.submitting = false;
        this.errorMessage = err.error?.message || 'Booking failed. Please try again.';
        this.cdr.markForCheck();
      },
    });
  }

  applyCouponCode() {
    if (!this.couponCode.trim()) return;
    this.applyingCoupon = true;
    this.couponError = '';
    this.couponService.getByCode(this.couponCode.trim()).subscribe({
      next: (coupon) => {
        const now = new Date();
        const validFrom = new Date(coupon.validFrom);
        const validUntil = new Date(coupon.validUntil);
        if (now < validFrom || now > validUntil) {
          this.couponError = 'This coupon has expired or is not yet valid.';
          this.applyingCoupon = false;
          return;
        }
        if (!coupon.active) {
          this.couponError = 'This coupon is no longer active.';
          this.applyingCoupon = false;
          return;
        }
        this.couponApplied = true;
        this.couponDiscount = coupon.discountPercent || 0;
        this.applyingCoupon = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.couponError = 'Invalid coupon code.';
        this.applyingCoupon = false;
        this.cdr.markForCheck();
      },
    });
  }

  removeCoupon() {
    this.couponApplied = false;
    this.couponCode = '';
    this.couponDiscount = 0;
    this.couponError = '';
  }

  toggleExtraService(id: number) {
    if (this.selectedExtraServiceIds.has(id)) {
      this.selectedExtraServiceIds.delete(id);
    } else {
      this.selectedExtraServiceIds.add(id);
    }
  }

  onDateChange() {
    this.availability = null;
    this.numberOfRooms = 1;
    if (this.checkInDate && this.checkOutDate && this.room) {
      this.checkingAvailability = true;
      this.roomService
        .getAvailabilityForDates(this.room.id, this.checkInDate, this.checkOutDate)
        .subscribe({
          next: (res) => {
            this.availability = res;
            this.checkingAvailability = false;
            this.cdr.markForCheck();
          },
          error: () => {
            this.checkingAvailability = false;
            this.cdr.markForCheck();
          },
        });
    }
  }

  get maxRooms(): number {
    if (this.availability) {
      return this.availability.availableForDates;
    }
    return this.room?.totalRooms || 1;
  }
}
