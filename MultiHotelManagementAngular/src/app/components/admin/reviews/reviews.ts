import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReviewService } from '../../../services/review.service';
import { ReviewResponse } from '../../../models/review.model';

@Component({
  selector: 'app-admin-reviews',
  imports: [CommonModule, FormsModule],
  templateUrl: './reviews.html',
  styleUrl: './reviews.css',
})
export class AdminReviews implements OnInit {
  private reviewService = inject(ReviewService);
  private cdr = inject(ChangeDetectorRef);

  reviews: ReviewResponse[] = [];
  filteredReviews: ReviewResponse[] = [];
  loading = true;
  searchTerm = '';
  filterStatus = 'ALL';

  ngOnInit() {
    this.loadReviews();
  }

  loadReviews() {
    this.loading = true;
    this.reviewService.getAll().subscribe({
      next: (data) => {
        this.reviews = data;
        this.applyFilter();
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  applyFilter() {
    let result = this.reviews;
    if (this.filterStatus !== 'ALL') {
      result = result.filter((r) => r.status === this.filterStatus);
    }
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      result = result.filter(
        (r) =>
          (r.customerName && r.customerName.toLowerCase().includes(term)) ||
          (r.hotelName && r.hotelName.toLowerCase().includes(term)) ||
          (r.comment && r.comment.toLowerCase().includes(term)),
      );
    }
    this.filteredReviews = result;
  }

  onSearch() {
    this.applyFilter();
    this.cdr.markForCheck();
  }

  approveReview(review: ReviewResponse) {
    this.reviewService.approve(review.id).subscribe({
      next: (updated) => {
        const idx = this.reviews.findIndex((r) => r.id === updated.id);
        if (idx !== -1) this.reviews[idx] = updated;
        this.applyFilter();
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  rejectReview(review: ReviewResponse) {
    this.reviewService.reject(review.id).subscribe({
      next: (updated) => {
        const idx = this.reviews.findIndex((r) => r.id === updated.id);
        if (idx !== -1) this.reviews[idx] = updated;
        this.applyFilter();
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  deleteReview(review: ReviewResponse) {
    if (!confirm(`Delete review #${review.id} by ${review.customerName}?`)) return;
    this.reviewService.delete(review.id).subscribe({
      next: () => {
        this.reviews = this.reviews.filter((r) => r.id !== review.id);
        this.applyFilter();
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  getStatusBadge(status: string): string {
    switch (status) {
      case 'APPROVED':
        return 'bg-success';
      case 'PENDING':
        return 'bg-warning text-dark';
      case 'REJECTED':
        return 'bg-danger';
      default:
        return 'bg-secondary';
    }
  }
}
