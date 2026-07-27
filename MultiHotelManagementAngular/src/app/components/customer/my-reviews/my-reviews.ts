import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ReviewService } from '../../../services/review.service';
import { AuthService } from '../../../services/auth.service';
import { CustomerService } from '../../../services/customer.service';
import { ReviewResponse } from '../../../models/review.model';

@Component({
  selector: 'app-my-reviews',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './my-reviews.html',
  styleUrl: './my-reviews.css',
})
export class MyReviews implements OnInit {
  private reviewService = inject(ReviewService);
  private auth = inject(AuthService);
  private customerService = inject(CustomerService);
  private cdr = inject(ChangeDetectorRef);

  reviews: ReviewResponse[] = [];
  loading = true;
  customerId: number | null = null;

  editingReview: ReviewResponse | null = null;
  editRating = 5;
  editComment = '';
  saving = false;

  showDeleteDialog = false;
  reviewToDelete: ReviewResponse | null = null;
  deleting = false;

  ngOnInit() {
    const userId = this.auth.getUser()?.userId;
    if (userId) {
      this.customerService.getCustomerByUserId(userId).subscribe({
        next: (customer) => {
          this.customerId = customer.id ?? null;
          if (this.customerId) {
            this.loadReviews();
          } else {
            this.loading = false;
            this.cdr.markForCheck();
          }
        },
        error: () => {
          this.loading = false;
          this.cdr.markForCheck();
        },
      });
    } else {
      this.loading = false;
    }
  }

  private loadReviews() {
    if (!this.customerId) return;
    this.reviewService.getByCustomer(this.customerId).subscribe({
      next: (data) => {
        this.reviews = data;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.reviews = [];
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  startEdit(review: ReviewResponse) {
    this.editingReview = review;
    this.editRating = review.rating;
    this.editComment = review.comment;
    this.cdr.markForCheck();
  }

  cancelEdit() {
    this.editingReview = null;
    this.cdr.markForCheck();
  }

  saveEdit() {
    if (!this.editingReview) return;
    this.saving = true;
    this.reviewService
      .update(this.editingReview.id, {
        rating: this.editRating,
        comment: this.editComment,
        hotelId: this.editingReview.hotelId,
        customerId: this.customerId!,
        bookingId: this.editingReview.bookingId,
      })
      .subscribe({
        next: (updated) => {
          const idx = this.reviews.findIndex((r) => r.id === updated.id);
          if (idx !== -1) this.reviews[idx] = updated;
          this.editingReview = null;
          this.saving = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.saving = false;
          this.cdr.markForCheck();
        },
      });
  }

  confirmDelete(review: ReviewResponse) {
    this.reviewToDelete = review;
    this.showDeleteDialog = true;
    this.cdr.markForCheck();
  }

  cancelDelete() {
    this.reviewToDelete = null;
    this.showDeleteDialog = false;
    this.cdr.markForCheck();
  }

  deleteConfirmed() {
    if (!this.reviewToDelete) return;
    this.deleting = true;
    this.reviewService.delete(this.reviewToDelete.id).subscribe({
      next: () => {
        this.reviews = this.reviews.filter((r) => r.id !== this.reviewToDelete!.id);
        this.reviewToDelete = null;
        this.showDeleteDialog = false;
        this.deleting = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.deleting = false;
        this.cdr.markForCheck();
      },
    });
  }

  getStarArray(rating: number): number[] {
    return Array.from({ length: 5 }, (_, i) => i + 1);
  }
}
