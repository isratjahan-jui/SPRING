import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Hotel } from '../../../models/hotel.model';
import { HotelDetails as HotelDetailsModel } from '../../../models/hotel-details.model';
import { Facility } from '../../../models/facility.model';
import { FoodItem } from '../../../models/food-item.model';
import { Room } from '../../../models/room.model';
import { Gallery } from '../../../models/gallery.model';
import { HotelService } from '../../../services/hotel.service';
import { HotelDetailsService } from '../../../services/hotel-details.service';
import { FacilityService } from '../../../services/facility.service';
import { FoodItemService } from '../../../services/food-item.service';
import { RoomService } from '../../../services/room.service';
import { GalleryService } from '../../../services/gallery.service';
import { ReviewService } from '../../../services/review.service';
import { AuthService } from '../../../services/auth.service';
import { CustomerService } from '../../../services/customer.service';
import { WishlistService } from '../../../services/wishlist.service';
import { DealService } from '../../../services/deal.service';
import { DealResponse } from '../../../models/deal.model';
import { ReviewResponse } from '../../../models/review.model';
import { HotelExtraService } from '../../../models/hotel-extra-service.model';
import { HotelExtraServiceService } from '../../../services/hotel-extra-service.service';
import { environment } from '../../../../environments/environments';

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
  gallery: Gallery[] = [];
  reviews: ReviewResponse[] = [];
  deals: DealResponse[] = [];
  extraServices: HotelExtraService[] = [];
  detailsError = false;
  loading = true;
  errorMsg = '';

  reviewRating = 5;
  reviewComment = '';
  submittingReview = false;
  reviewSubmitted = false;
  isCustomer = false;
  customerId: number | null = null;

  isInWishlist = false;
  wishlistId: number | null = null;
  togglingWishlist = false;
  currentHotelId = 0;

  private auth = inject(AuthService);
  private customerService = inject(CustomerService);
  private reviewService = inject(ReviewService);
  private wishlistService = inject(WishlistService);
  private dealService = inject(DealService);

  backRoute = '/hotels';
  backLabel = 'Back to Hotels';

  constructor(
    private route: ActivatedRoute,
    private hotelService: HotelService,
    private hotelDetailsService: HotelDetailsService,
    private facilityService: FacilityService,
    private foodItemService: FoodItemService,
    private roomService: RoomService,
    private galleryService: GalleryService,
    private hotelExtraServiceService: HotelExtraServiceService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.currentHotelId = id;

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
          if (this.customerId) {
            this.checkWishlist();
          }
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

    this.galleryService.getByHotel(id).subscribe({
      next: (data) => {
        this.gallery = data;
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

    this.dealService.getByHotel(id).subscribe({
      next: (data) => {
        this.deals = data;
        this.cdr.markForCheck();
      },
      error: () => {},
    });

    this.hotelExtraServiceService.getActiveByHotel(id).subscribe({
      next: (data) => {
        this.extraServices = data;
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

  checkWishlist() {
    if (!this.customerId) return;
    this.wishlistService.exists(this.customerId, this.currentHotelId).subscribe({
      next: (exists) => {
        if (exists) {
          this.wishlistService.getByCustomer(this.customerId!).subscribe({
            next: (items) => {
              const match = items.find((i) => i.hotelId === this.currentHotelId);
              this.isInWishlist = !!match;
              this.wishlistId = match?.id ?? null;
              this.cdr.markForCheck();
            },
          });
        } else {
          this.isInWishlist = false;
          this.wishlistId = null;
          this.cdr.markForCheck();
        }
      },
      error: () => {},
    });
  }

  toggleWishlist() {
    if (!this.customerId || !this.hotel || this.togglingWishlist) return;
    const userId = this.auth.getUser()?.userId;
    if (!userId) return;

    this.togglingWishlist = true;

    if (this.isInWishlist && this.wishlistId) {
      this.wishlistService.delete(this.wishlistId).subscribe({
        next: () => {
          this.isInWishlist = false;
          this.wishlistId = null;
          this.togglingWishlist = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.togglingWishlist = false;
          this.cdr.markForCheck();
        },
      });
    } else {
      this.wishlistService
        .create({
          userId,
          customerId: this.customerId,
          hotelId: this.currentHotelId,
        })
        .subscribe({
          next: (res) => {
            this.isInWishlist = true;
            this.wishlistId = res.id;
            this.togglingWishlist = false;
            this.cdr.markForCheck();
          },
          error: () => {
            this.togglingWishlist = false;
            this.cdr.markForCheck();
          },
        });
    }
  }

  getImageUrl(image: string): string {
    return image ? `${environment.imageBaseUrl}/hotel/${image}` : '';
  }

  getRoomImageUrl(image: string): string {
    return image ? `${environment.imageBaseUrl}/room/${image}` : '';
  }

  getFoodImageUrl(image: string): string {
    return image ? `${environment.imageBaseUrl}/food/${image}` : '';
  }

  getGalleryImageUrl(image: string): string {
    return image ? `${environment.imageBaseUrl}/${image}` : '';
  }
}
