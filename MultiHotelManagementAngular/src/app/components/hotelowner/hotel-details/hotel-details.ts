import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  HotelDetails as HotelDetailsModel,
  HotelDetailsRequest,
} from '../../../models/hotel-details.model';
import { Hotel } from '../../../models/hotel.model';
import { HotelDetailsService } from '../../../services/hotel-details.service';
import { HotelService } from '../../../services/hotel.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-owner-hotel-details',
  imports: [CommonModule, FormsModule],
  templateUrl: './hotel-details.html',
  styleUrl: './hotel-details.css',
})
export class OwnerHotelDetails implements OnInit {
  hotels: Hotel[] = [];
  details: HotelDetailsModel | null = null;
  selectedHotelId = 0;
  showForm = false;
  editingId: number | null = null;
  form: HotelDetailsRequest = this.emptyForm();
  loading = false;
  private authService = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);

  constructor(
    private hotelDetailsService: HotelDetailsService,
    private hotelService: HotelService,
  ) {}

  ngOnInit() {
    const ownerId = this.authService.getUser()?.ownerId;
    if (ownerId) {
      this.hotelService.getByOwner(ownerId).subscribe({
        next: (data) => {
          this.hotels = data;
          this.cdr.markForCheck();
        },
        error: () => alert('Failed to load hotels'),
      });
    }
  }

  emptyForm(): HotelDetailsRequest {
    return {
      hotelId: 0,
      ownerSpeach: '',
      description: '',
      hotelPolicy: '',
      pricePerNight: 0,
      checkInTime: '',
      checkOutTime: '',
      contactEmail: '',
      contactPhone: '',
      cancellationPolicy: '',
      petPolicy: '',
      smokingPolicy: '',
      childPolicy: '',
      languages: '',
      nearbyAttractions: '',
      paymentOption: 'ADVANCE',
      depositPercentage: 20,
      preAuthRequired: false,
      cancellationDepositRefundable: 'FULL_REFUND',
    };
  }

  onHotelChange() {
    this.details = null;
    this.showForm = false;
    this.editingId = null;
    if (this.selectedHotelId) {
      this.hotelDetailsService.getByHotelId(this.selectedHotelId).subscribe({
        next: (data) => {
          this.details = data;
          this.cdr.markForCheck();
        },
        error: () => {
          this.details = null;
          this.cdr.markForCheck();
        },
      });
    }
  }

  addNew() {
    this.editingId = null;
    this.form = { ...this.emptyForm(), hotelId: this.selectedHotelId };
    this.showForm = true;
  }

  edit() {
    if (!this.details) return;
    this.editingId = this.details.id;
    this.form = {
      hotelId: this.selectedHotelId,
      ownerSpeach: this.details.ownerSpeach || '',
      description: this.details.description || '',
      hotelPolicy: this.details.hotelPolicy || '',
      pricePerNight: this.details.pricePerNight || 0,
      checkInTime: this.details.checkInTime || '',
      checkOutTime: this.details.checkOutTime || '',
      contactEmail: this.details.contactEmail || '',
      contactPhone: this.details.contactPhone || '',
      cancellationPolicy: this.details.cancellationPolicy || '',
      petPolicy: this.details.petPolicy || '',
      smokingPolicy: this.details.smokingPolicy || '',
      childPolicy: this.details.childPolicy || '',
      languages: this.details.languages || '',
      nearbyAttractions: this.details.nearbyAttractions || '',
      paymentOption: this.details.paymentOption || 'ADVANCE',
      depositPercentage: this.details.depositPercentage || 20,
      preAuthRequired: this.details.preAuthRequired || false,
      cancellationDepositRefundable: this.details.cancellationDepositRefundable || 'FULL_REFUND',
    };
    this.showForm = true;
  }

  cancelForm() {
    this.showForm = false;
    this.editingId = null;
  }

  save() {
    this.loading = true;
    const request = this.editingId
      ? this.hotelDetailsService.update(this.editingId, this.form)
      : this.hotelDetailsService.create(this.form);

    request.subscribe({
      next: () => {
        this.loading = false;
        this.cancelForm();
        this.onHotelChange();
      },
      error: (err) => {
        this.loading = false;
        alert(err.error?.message || 'Failed to save hotel details');
      },
    });
  }

  deleteDetails() {
    if (!this.details || !confirm('Delete this hotel details?')) return;
    this.hotelDetailsService.delete(this.details.id).subscribe({
      next: () => this.onHotelChange(),
      error: () => alert('Failed to delete hotel details'),
    });
  }
}
