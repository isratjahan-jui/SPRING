import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReviewService } from '../../../services/review.service';
import { HotelService } from '../../../services/hotel.service';
import { AuthService } from '../../../services/auth.service';
import { ReviewResponse } from '../../../models/review.model';
import { Hotel } from '../../../models/hotel.model';

@Component({
  selector: 'app-owner-reviews',
  imports: [CommonModule, FormsModule],
  templateUrl: './reviews.html',
  styleUrl: './reviews.css',
})
export class OwnerReviews implements OnInit {
  private reviewService = inject(ReviewService);
  private hotelService = inject(HotelService);
  private auth = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);

  hotels: Hotel[] = [];
  selectedHotelId = 0;
  reviews: ReviewResponse[] = [];
  filteredReviews: ReviewResponse[] = [];
  loading = false;
  searchTerm = '';
  filterStatus = 'ALL';

  replyText: { [reviewId: number]: string } = {};
  submittingReply: { [reviewId: number]: boolean } = {};

  ngOnInit() {
    const ownerId = this.auth.getUser()?.ownerId;
    if (ownerId) {
      this.hotelService.getByOwner(ownerId).subscribe({
        next: (data) => {
          this.hotels = data;
          this.cdr.markForCheck();
        },
        error: () => {},
      });
    }
  }

  onHotelChange() {
    this.reviews = [];
    this.filteredReviews = [];
    this.searchTerm = '';
    this.filterStatus = 'ALL';
    if (!this.selectedHotelId) return;

    this.loading = true;
    this.reviewService.getByHotelAll(this.selectedHotelId).subscribe({
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
          (r.comment && r.comment.toLowerCase().includes(term)),
      );
    }
    this.filteredReviews = result;
  }

  onSearch() {
    this.applyFilter();
    this.cdr.markForCheck();
  }

  submitReply(reviewId: number) {
    const reply = this.replyText[reviewId];
    if (!reply || !reply.trim()) return;

    this.submittingReply[reviewId] = true;
    this.reviewService.reply(reviewId, reply.trim()).subscribe({
      next: (updated) => {
        const idx = this.reviews.findIndex((r) => r.id === updated.id);
        if (idx !== -1) this.reviews[idx] = updated;
        this.applyFilter();
        this.replyText[reviewId] = '';
        this.submittingReply[reviewId] = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.submittingReply[reviewId] = false;
        this.cdr.markForCheck();
      },
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

  getStars(rating: number): number[] {
    return Array.from({ length: 5 }, (_, i) => i + 1);
  }
}
