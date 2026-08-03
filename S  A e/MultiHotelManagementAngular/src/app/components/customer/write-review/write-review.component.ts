import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ReviewService } from '../../../services/review.service';
import { AuthService } from '../../../services/auth.service';
import { CustomerService } from '../../../services/customer.service';
import { BookingService } from '../../../services/booking.service';
import { ReviewRequest } from '../../../models/review.model';

@Component({
  selector: 'app-write-review',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './write-review.component.html',
})
export class WriteReview implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private reviewService = inject(ReviewService);
  private auth = inject(AuthService);
  private customerService = inject(CustomerService);
  private bookingService = inject(BookingService);
  private cdr = inject(ChangeDetectorRef);

  bookingId: number = 0;
  customerId: number | null = null;

  hotelName = '';
  hotelId = 0;
  roomType = '';
  checkInDate = '';
  checkOutDate = '';

  rating = 5;
  comment = '';
  hoverRating = 0;

  loading = true;
  submitting = false;
  alreadyReviewed = false;
  error = '';
  success = false;

  ngOnInit() {
    this.bookingId = Number(this.route.snapshot.paramMap.get('bookingId'));

    if (!this.bookingId) {
      this.error = 'Invalid booking ID';
      this.loading = false;
      return;
    }

    const userId = this.auth.getUser()?.userId;
    if (userId) {
      this.customerService.getCustomerByUserId(userId).subscribe({
        next: (customer) => {
          this.customerId = customer.id ?? null;
          this.loadBooking();
          this.checkExistingReview();
        },
        error: () => {
          this.error = 'Could not load customer info';
          this.loading = false;
          this.cdr.markForCheck();
        },
      });
    } else {
      this.error = 'Not logged in';
      this.loading = false;
    }
  }

  private loadBooking() {
    this.bookingService.getById(this.bookingId).subscribe({
      next: (booking: any) => {
        this.hotelId = booking.hotelId ?? 0;
        this.hotelName = booking.hotelName ?? '';
        this.roomType = booking.roomType ?? '';
        this.checkInDate = booking.checkInDate ?? '';
        this.checkOutDate = booking.checkOutDate ?? '';
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.error = 'Booking not found';
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  private checkExistingReview() {
    if (!this.customerId) return;
    this.reviewService.checkReview(this.customerId, this.bookingId).subscribe({
      next: (result) => {
        this.alreadyReviewed = result.reviewed;
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  setRating(star: number) {
    this.rating = star;
  }

  setHoverRating(star: number) {
    this.hoverRating = star;
  }

  clearHover() {
    this.hoverRating = 0;
  }

  submitReview() {
    if (!this.customerId || !this.comment.trim() || this.rating < 1 || this.rating > 5) {
      this.error = 'Please fill all required fields.';
      return;
    }

    this.submitting = true;
    this.error = '';

    const reviewRequest: ReviewRequest = {
      rating: this.rating,
      comment: this.comment.trim(),
      hotelId: this.hotelId,
      customerId: this.customerId,
      bookingId: this.bookingId,
    };

    this.reviewService.create(reviewRequest).subscribe({
      next: () => {
        this.success = true;
        this.submitting = false;
        this.cdr.markForCheck();
        setTimeout(() => this.router.navigate(['/customer/my-reviews']), 2000);
      },
      error: (err) => {
        this.error = err.error?.message || err.error || 'Failed to submit review. Please try again.';
        this.submitting = false;
        this.cdr.markForCheck();
      },
    });
  }

  getStarArray(count: number): number[] {
    return Array.from({ length: 5 }, (_, i) => i + 1);
  }
}
