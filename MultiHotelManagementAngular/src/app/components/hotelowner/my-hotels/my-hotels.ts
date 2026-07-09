import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Hotel, HotelRequest } from '../../../models/hotel.model';
import { HotelService } from '../../../services/hotel.service';
import { LocationService } from '../../../services/location.service';
import { Location } from '../../../models/location.model';

@Component({
  selector: 'app-my-hotels',
  imports: [CommonModule, FormsModule],
  templateUrl: './my-hotels.html',
  styleUrl: './my-hotels.css',
})
export class MyHotels implements OnInit {
  hotels: Hotel[] = [];
  locations: Location[] = [];
  showForm = false;
  editingId: number | null = null;
  form: HotelRequest = this.emptyForm();
  selectedImage?: File;
  preview: string | ArrayBuffer | null = null;
  loading = false;

  constructor(
    private hotelService: HotelService,
    private locationService: LocationService,
  ) {}

  ngOnInit() {
    this.loadHotels();
    this.locationService.getAll().subscribe({
      next: (data) => (this.locations = data),
      error: () => alert('Failed to load locations'),
    });
  }

  emptyForm(): HotelRequest {
    return {
      hotelName: '',
      address: '',
      description: '',
      rating: '',
      status: 'PENDING_APPROVAL',
      locationId: 0,
      ownerId: Number(localStorage.getItem('ownerId')) || 0,
    };
  }

  loadHotels() {
    const ownerId = localStorage.getItem('ownerId');
    if (!ownerId) return;
    this.hotelService.getByOwner(Number(ownerId)).subscribe({
      next: (data) => (this.hotels = data),
      error: () => alert('Failed to load hotels'),
    });
  }

  addNew() {
    this.editingId = null;
    this.form = this.emptyForm();
    this.selectedImage = undefined;
    this.preview = null;
    this.showForm = true;
  }

  edit(hotel: Hotel) {
    this.editingId = hotel.id;
    this.form = {
      hotelName: hotel.hotelName,
      address: hotel.address,
      description: hotel.description,
      rating: hotel.rating,
      status: hotel.status,
      locationId: hotel.locationId,
      ownerId: hotel.ownerId,
    };
    this.selectedImage = undefined;
    this.preview = null;
    this.showForm = true;
  }

  cancelForm() {
    this.showForm = false;
    this.editingId = null;
    this.form = this.emptyForm();
    this.selectedImage = undefined;
    this.preview = null;
  }

  save() {
    this.loading = true;
    const request = this.editingId
      ? this.hotelService.update(this.editingId, this.form, this.selectedImage)
      : this.hotelService.create(this.form, this.selectedImage);

    request.subscribe({
      next: () => {
        this.loading = false;
        this.cancelForm();
        this.loadHotels();
      },
      error: (err) => {
        this.loading = false;
        alert(err.error?.message || 'Failed to save hotel');
      },
    });
  }

  deleteHotel(id: number) {
    if (!confirm('Are you sure you want to delete this hotel?')) return;
    this.hotelService.delete(id).subscribe({
      next: () => this.loadHotels(),
      error: () => alert('Failed to delete hotel'),
    });
  }

  onFileSelected(event: any) {
    if (event.target.files?.length) {
      this.selectedImage = event.target.files[0];
      const reader = new FileReader();
      reader.onload = () => (this.preview = reader.result);
      reader.readAsDataURL(this.selectedImage!);
    }
  }
}
