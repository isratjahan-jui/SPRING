import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Hotel } from '../../../models/hotel.model';
import { HotelDetails as HotelDetailsModel } from '../../../models/hotel-details.model';
import { Facility } from '../../../models/facility.model';
import { FoodItem } from '../../../models/food-item.model';
import { Room } from '../../../models/room.model';
import { HotelService } from '../../../services/hotel.service';
import { HotelDetailsService } from '../../../services/hotel-details.service';
import { FacilityService } from '../../../services/facility.service';
import { FoodItemService } from '../../../services/food-item.service';
import { RoomService } from '../../../services/room.service';
import { ReviewService } from '../../../services/review.service';
import { AuthService } from '../../../services/auth.service';
import { CustomerService } from '../../../services/customer.service';
import { ReviewResponse } from '../../../models/review.model';

@Component({
  selector: 'app-hotel-details',
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './hotel-details.html',
  styleUrl: './hotel-details.css',
})
export class HotelDetails implements OnInit {
  hotel?: Hotel;
  details?: HotelDetailsModel;
  facilities: Facility[] = [];
  foodItems: FoodItem[] = [];
  rooms: Room[] = [];
  reviews: ReviewResponse[] = [];
  detailsError = false;
  loading = true;
  errorMsg = '';

  reviewRating = 5;
  reviewComment = '';
  submittingReview = false;
  reviewSubmitted = false;
  isCustomer = false;
  customerId: number | null = null;

  private auth = inject(AuthService);
  private customerService = inject(CustomerService);
  private reviewService = inject(ReviewService);

  backRoute = '/hotels';
  backLabel = 'Back to Hotels';

  constructor(
    private route: ActivatedRoute,
    private hotelService: HotelService,
    private hotelDetailsService: HotelDetailsService,
    private facilityService: FacilityService,
    private foodItemService: FoodItemService,
    private roomService: RoomService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    const role = this.auth.getRole();
    this.isCustomer = role === 'CUSTOMER';
    if (role === 'ADMIN') {
      this.backRoute = '/admin/hotels/manage';
      this.backLabel = 'Back to Manage Hotels';
    } else if (role === 'HOTEL_OWNER') {
      this.backRoute = '/owner/my-hotels';
      this.backLabel = 'Back to My Hotels';
    }
    const userId = this.auth.getUser()?.userId;
    if (userId) {
      this.customerService.getCustomerByUserId(userId).subscribe({
        next: (c) => {
          this.customerId = c.id ?? null;
          this.cdr.markForCheck();
        },
        error: () => {},
      });
    }

    this.hotelService.getById(id).subscribe({
      next: (data) => {
        this.hotel = data;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Hotel load error:', err);
        this.loading = false;
        this.errorMsg = 'Failed to load hotel details.';
        this.cdr.markForCheck();
      },
    });

    this.hotelDetailsService.getByHotelId(id).subscribe({
      next: (data) => {
        this.details = data;
        this.cdr.markForCheck();
      },
      error: () => {
        this.detailsError = true;
        this.cdr.markForCheck();
      },
    });

    this.facilityService.getByHotel(id).subscribe({
      next: (data) => {
        this.facilities = data;
        this.cdr.markForCheck();
      },
      error: () => {},
    });

    this.foodItemService.getByHotel(id).subscribe({
      next: (data) => {
        this.foodItems = data;
        this.cdr.markForCheck();
      },
      error: () => {},
    });

    this.roomService.getByHotel(id).subscribe({
      next: (data) => {
        this.rooms = data;
        this.cdr.markForCheck();
      },
      error: () => {},
    });

    this.reviewService.getByHotel(id).subscribe({
      next: (data) => {
        this.reviews = data;
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  submitReview() {
    if (!this.reviewComment.trim() || !this.customerId || !this.hotel) return;
    this.submittingReview = true;
    this.reviewService
      .create({
        rating: this.reviewRating,
        comment: this.reviewComment,
        hotelId: this.hotel.id,
        customerId: this.customerId,
      })
      .subscribe({
        next: (review) => {
          this.reviews.unshift(review);
          this.reviewComment = '';
          this.reviewRating = 5;
          this.reviewSubmitted = true;
          this.submittingReview = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.submittingReview = false;
          this.cdr.markForCheck();
        },
      });
  }

  getImageUrl(image: string): string {
    return image ? `http://localhost:8085/hotel/${image}` : '';
  }

  getRoomImageUrl(image: string): string {
    return image ? `http://localhost:8085/room/${image}` : '';
  }
}
