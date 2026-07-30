import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DealService } from '../../../services/deal.service';
import { AuthService } from '../../../services/auth.service';
import { HotelOwnerService } from '../../../services/hotel-owner.service';
import { HotelService } from '../../../services/hotel.service';
import { DealRequest, DealResponse } from '../../../models/deal.model';
import { Hotel } from '../../../models/hotel.model';

@Component({
  selector: 'app-owner-deals',
  imports: [CommonModule, FormsModule],
  templateUrl: './deals.html',
  styleUrl: './deals.css',
})
export class OwnerDeals implements OnInit {
  private dealService = inject(DealService);
  private auth = inject(AuthService);
  private ownerService = inject(HotelOwnerService);
  private hotelService = inject(HotelService);
  private cdr = inject(ChangeDetectorRef);

  deals: DealResponse[] = [];
  hotels: Hotel[] = [];
  loading = true;

  showForm = false;
  editingId: number | null = null;
  form: DealRequest = {
    dealTitle: '',
    description: '',
    discountPercent: 0,
    discountAmount: 0,
    startDate: '',
    endDate: '',
    hotelId: 0,
    roomId: undefined,
    dealType: 'PERCENTAGE',
  };
  submitting = false;
  formError = '';

  ngOnInit() {
    const userId = this.auth.getUser()?.userId;
    const ownerId = this.auth.getUser()?.ownerId;
    if (ownerId) {
      this.hotelService.getByOwner(ownerId).subscribe({
        next: (data) => {
          this.hotels = data;
          this.loadDeals();
        },
        error: () => {
          this.loading = false;
          this.cdr.markForCheck();
        },
      });
    } else if (userId) {
      this.ownerService.getOwnerByUserId(userId).subscribe({
        next: (owner) => {
          this.hotelService.getByOwner(owner.id!).subscribe({
            next: (data) => {
              this.hotels = data;
              this.loadDeals();
            },
          });
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

  private loadDeals() {
    const ids = this.hotels.map((h) => h.id);
    if (ids.length === 0) {
      this.loading = false;
      return;
    }
    ids.forEach((hotelId) => {
      this.dealService.getByHotel(hotelId).subscribe({
        next: (data) => {
          this.deals = [...this.deals, ...data];
          this.loading = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.loading = false;
          this.cdr.markForCheck();
        },
      });
    });
  }

  openCreate() {
    this.editingId = null;
    this.form = {
      dealTitle: '',
      description: '',
      discountPercent: 0,
      discountAmount: 0,
      startDate: '',
      endDate: '',
      hotelId: this.hotels[0]?.id || 0,
      roomId: undefined,
      dealType: 'PERCENTAGE',
    };
    this.formError = '';
    this.showForm = true;
  }

  openEdit(deal: DealResponse) {
    this.editingId = deal.id;
    this.form = {
      dealTitle: deal.dealTitle,
      description: deal.description,
      discountPercent: deal.discountPercent,
      discountAmount: deal.discountAmount,
      startDate: deal.startDate.substring(0, 16),
      endDate: deal.endDate.substring(0, 16),
      hotelId: this.hotels.find((h) => h.hotelName === deal.hotelName)?.id || 0,
      roomId: undefined,
      dealType: deal.dealType,
    };
    this.formError = '';
    this.showForm = true;
  }

  cancelForm() {
    this.showForm = false;
    this.editingId = null;
  }

  submitForm() {
    if (!this.form.dealTitle || !this.form.startDate || !this.form.endDate || !this.form.hotelId) {
      this.formError = 'Please fill all required fields.';
      return;
    }
    this.submitting = true;
    this.formError = '';

    const payload: DealRequest = {
      ...this.form,
      startDate: new Date(this.form.startDate).toISOString(),
      endDate: new Date(this.form.endDate).toISOString(),
    };

    if (this.editingId) {
      this.dealService.update(this.editingId, payload).subscribe({
        next: (updated) => {
          const idx = this.deals.findIndex((d) => d.id === this.editingId);
          if (idx >= 0) this.deals[idx] = updated;
          this.showForm = false;
          this.submitting = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.formError = err.error?.message || 'Update failed.';
          this.submitting = false;
          this.cdr.markForCheck();
        },
      });
    } else {
      this.dealService.create(payload).subscribe({
        next: (created) => {
          this.deals.push(created);
          this.showForm = false;
          this.submitting = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.formError = err.error?.message || 'Create failed.';
          this.submitting = false;
          this.cdr.markForCheck();
        },
      });
    }
  }

  toggleActive(deal: DealResponse) {
    this.dealService
      .update(deal.id, {
        dealTitle: deal.dealTitle,
        description: deal.description,
        discountPercent: deal.discountPercent,
        discountAmount: deal.discountAmount,
        startDate: deal.startDate,
        endDate: deal.endDate,
        hotelId: this.hotels.find((h) => h.hotelName === deal.hotelName)?.id || 0,
        dealType: deal.dealType,
      })
      .subscribe({
        next: () => {
          deal.isActive = !deal.isActive;
          this.cdr.markForCheck();
        },
      });
  }

  confirmDelete(id: number) {
    if (confirm('Are you sure you want to delete this deal?')) {
      this.dealService.delete(id).subscribe({
        next: () => {
          this.deals = this.deals.filter((d) => d.id !== id);
          this.cdr.markForCheck();
        },
      });
    }
  }

  get today(): string {
    return new Date().toISOString().split('T')[0];
  }
}
