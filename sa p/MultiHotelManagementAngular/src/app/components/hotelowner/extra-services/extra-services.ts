import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HotelExtraServiceService } from '../../../services/hotel-extra-service.service';
import { AuthService } from '../../../services/auth.service';
import { HotelService } from '../../../services/hotel.service';
import {
  HotelExtraService,
  HotelExtraServiceRequest,
} from '../../../models/hotel-extra-service.model';
import { Hotel } from '../../../models/hotel.model';

@Component({
  selector: 'app-owner-extra-services',
  imports: [CommonModule, FormsModule],
  templateUrl: './extra-services.html',
  styleUrl: './extra-services.css',
})
export class OwnerExtraServices implements OnInit {
  private service = inject(HotelExtraServiceService);
  private auth = inject(AuthService);
  private hotelService = inject(HotelService);
  private cdr = inject(ChangeDetectorRef);

  extraServices: HotelExtraService[] = [];
  hotels: Hotel[] = [];
  selectedHotelId = 0;
  loading = true;

  showForm = false;
  editingId: number | null = null;
  form: HotelExtraServiceRequest = {
    serviceName: '',
    description: '',
    price: 0,
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
          this.loading = false;
          this.cdr.markForCheck();
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

  onHotelChange() {
    this.extraServices = [];
    this.showForm = false;
    if (this.selectedHotelId) {
      this.service.getByHotel(this.selectedHotelId).subscribe({
        next: (data) => {
          this.extraServices = data;
          this.cdr.markForCheck();
        },
      });
    }
  }

  openCreate() {
    this.editingId = null;
    this.form = {
      serviceName: '',
      description: '',
      price: 0,
      hotelId: this.selectedHotelId || this.hotels[0]?.id || 0,
    };
    this.formError = '';
    this.showForm = true;
  }

  openEdit(item: HotelExtraService) {
    this.editingId = item.id;
    this.form = {
      serviceName: item.serviceName,
      description: item.description,
      price: item.price,
      hotelId: item.hotelId,
    };
    this.formError = '';
    this.showForm = true;
  }

  cancelForm() {
    this.showForm = false;
    this.editingId = null;
  }

  submitForm() {
    if (!this.form.serviceName || !this.form.hotelId || this.form.price < 0) {
      this.formError = 'Please fill all required fields with valid values.';
      return;
    }
    this.submitting = true;
    this.formError = '';

    if (this.editingId) {
      this.service.update(this.editingId, this.form).subscribe({
        next: (updated) => {
          const idx = this.extraServices.findIndex((s) => s.id === this.editingId);
          if (idx >= 0) this.extraServices[idx] = updated;
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
      this.service.create(this.form).subscribe({
        next: (created) => {
          this.extraServices.push(created);
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

  confirmDelete(id: number) {
    if (confirm('Are you sure you want to delete this extra service?')) {
      this.service.delete(id).subscribe({
        next: () => {
          this.extraServices = this.extraServices.filter((s) => s.id !== id);
          this.cdr.markForCheck();
        },
      });
    }
  }
}
