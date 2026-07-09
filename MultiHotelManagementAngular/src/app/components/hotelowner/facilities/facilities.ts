import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Facility, FacilityRequest } from '../../../models/facility.model';
import { Hotel } from '../../../models/hotel.model';
import { FacilityService } from '../../../services/facility.service';
import { HotelService } from '../../../services/hotel.service';

@Component({
  selector: 'app-owner-facilities',
  imports: [CommonModule, FormsModule],
  templateUrl: './facilities.html',
  styleUrl: './facilities.css',
})
export class OwnerFacilities implements OnInit {
  hotels: Hotel[] = [];
  facilities: Facility[] = [];
  selectedHotelId = 0;
  showForm = false;
  editingId: number | null = null;
  form: FacilityRequest = this.emptyForm();
  loading = false;

  constructor(
    private facilityService: FacilityService,
    private hotelService: HotelService,
  ) {}

  ngOnInit() {
    const ownerId = localStorage.getItem('ownerId');
    if (ownerId) {
      this.hotelService.getByOwner(Number(ownerId)).subscribe({
        next: (data) => (this.hotels = data),
        error: () => alert('Failed to load hotels'),
      });
    }
  }

  emptyForm(): FacilityRequest {
    return { facilityName: '', description: '', hotelId: 0 };
  }

  onHotelChange() {
    this.facilities = [];
    this.showForm = false;
    if (this.selectedHotelId) {
      this.facilityService.getByHotel(this.selectedHotelId).subscribe({
        next: (data) => (this.facilities = data),
        error: () => alert('Failed to load facilities'),
      });
    }
  }

  addNew() {
    this.editingId = null;
    this.form = { ...this.emptyForm(), hotelId: this.selectedHotelId };
    this.showForm = true;
  }

  edit(facility: Facility) {
    this.editingId = facility.id;
    this.form = {
      facilityName: facility.facilityName,
      description: facility.description,
      hotelId: facility.hotelId,
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
      ? this.facilityService.update(this.editingId, this.form)
      : this.facilityService.create(this.form);

    request.subscribe({
      next: () => {
        this.loading = false;
        this.cancelForm();
        this.onHotelChange();
      },
      error: (err) => {
        this.loading = false;
        alert(err.error?.message || 'Failed to save facility');
      },
    });
  }

  deleteFacility(id: number) {
    if (!confirm('Delete this facility?')) return;
    this.facilityService.delete(id).subscribe({
      next: () => this.onHotelChange(),
      error: () => alert('Failed to delete facility'),
    });
  }
}
