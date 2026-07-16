import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Hotel, HotelRequest } from '../../../models/hotel.model';
import { HotelDetails as HotelDetailsModel } from '../../../models/hotel-details.model';
import { Facility } from '../../../models/facility.model';
import { FoodItem } from '../../../models/food-item.model';
import { Room } from '../../../models/room.model';
import { Gallery } from '../../../models/gallery.model';
import { HotelService } from '../../../services/hotel.service';
import { LocationService } from '../../../services/location.service';
import { HotelDetailsService } from '../../../services/hotel-details.service';
import { FacilityService } from '../../../services/facility.service';
import { FoodItemService } from '../../../services/food-item.service';
import { RoomService } from '../../../services/room.service';
import { GalleryService } from '../../../services/gallery.service';
import { Location } from '../../../models/location.model';
import { AuthService } from '../../../services/auth.service';
import { environment } from '../../../../environments/environments';

@Component({
  selector: 'app-my-hotels',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './my-hotels.html',
  styleUrl: './my-hotels.css',
})
export class MyHotels implements OnInit {
  hotels: Hotel[] = [];
  locations: Location[] = [];
  showForm = false;
  editingId: number | null = null;
  imageBaseUrl = environment.imageBaseUrl;
  private authService = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);
  form: HotelRequest = this.emptyForm();
  selectedImage?: File;
  preview: string | ArrayBuffer | null = null;
  loading = false;

  expandedHotelId: number | null = null;
  hotelDetails: Map<number, HotelDetailsModel> = new Map();
  hotelFacilities: Map<number, Facility[]> = new Map();
  hotelFoodItems: Map<number, FoodItem[]> = new Map();
  hotelRooms: Map<number, Room[]> = new Map();
  hotelGallery: Map<number, Gallery[]> = new Map();
  activeTab: Map<number, string> = new Map();

  constructor(
    private hotelService: HotelService,
    private locationService: LocationService,
    private hotelDetailsService: HotelDetailsService,
    private facilityService: FacilityService,
    private foodItemService: FoodItemService,
    private roomService: RoomService,
    private galleryService: GalleryService,
  ) {}

  ngOnInit() {
    const user = this.authService.getUser();
    console.log('LoginResponse:', user);
    console.log('ownerId:', user?.ownerId);
    this.loadHotels();
    this.locationService.getAll().subscribe({
      next: (data) => {
        this.locations = data;
        this.cdr.markForCheck();
      },
      error: () => alert('Failed to load locations'),
    });
  }

  errorMessage = '';

  emptyForm(): HotelRequest {
    return {
      hotelName: '',
      address: '',
      description: '',
      rating: '',
      status: 'PENDING_APPROVAL',
      locationId: 0,
      ownerId: this.authService.getUser()?.ownerId || 0,
    };
  }

  // loadHotels() {
  //   const ownerId = localStorage.getItem('ownerId');
  //   if (!ownerId) return;
  //   this.hotelService.getByOwner(Number(ownerId)).subscribe({
  //     next: (data) => (this.hotels = data),
  //     error: () => alert('Failed to load hotels'),
  //   });
  // }
  loadHotels() {
    const ownerId = this.authService.getUser()?.ownerId;
    console.log('loadHotels called, ownerId:', ownerId);
    if (!ownerId) return;
    this.hotelService.getByOwner(ownerId).subscribe({
      next: (data) => {
        this.hotels = data;
        this.cdr.markForCheck();
      },
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
    this.errorMessage = '';

    if (!this.form.hotelName?.trim()) {
      this.errorMessage = 'Hotel name is required.';
      return;
    }
    if (!this.form.address?.trim()) {
      this.errorMessage = 'Address is required.';
      return;
    }
    if (!this.form.locationId || this.form.locationId === 0) {
      this.errorMessage = 'Please select a location.';
      return;
    }
    if (!this.form.ownerId || this.form.ownerId === 0) {
      this.errorMessage = 'Owner ID not found. Please log in again.';
      return;
    }

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
        this.errorMessage =
          err.error?.message || err.error?.error || 'Failed to save hotel. Please try again.';
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

  toggleExpand(hotelId: number) {
    if (this.expandedHotelId === hotelId) {
      this.expandedHotelId = null;
      return;
    }
    this.expandedHotelId = hotelId;
    if (!this.activeTab.has(hotelId)) {
      this.activeTab.set(hotelId, 'details');
    }
    this.loadHotelData(hotelId);
  }

  setTab(hotelId: number, tab: string) {
    this.activeTab.set(hotelId, tab);
  }

  getTab(hotelId: number): string {
    return this.activeTab.get(hotelId) || 'details';
  }

  loadHotelData(hotelId: number) {
    this.hotelDetailsService.getByHotelId(hotelId).subscribe({
      next: (data) => {
        this.hotelDetails.set(hotelId, data);
        this.cdr.markForCheck();
      },
      error: () => this.hotelDetails.set(hotelId, null as any),
    });

    this.facilityService.getByHotel(hotelId).subscribe({
      next: (data) => {
        this.hotelFacilities.set(hotelId, data);
        this.cdr.markForCheck();
      },
    });

    this.foodItemService.getByHotel(hotelId).subscribe({
      next: (data) => {
        this.hotelFoodItems.set(hotelId, data);
        this.cdr.markForCheck();
      },
    });

    this.roomService.getByHotel(hotelId).subscribe({
      next: (data) => {
        this.hotelRooms.set(hotelId, data);
        this.cdr.markForCheck();
      },
    });

    this.galleryService.getByHotel(hotelId).subscribe({
      next: (data) => {
        this.hotelGallery.set(hotelId, data);
        this.cdr.markForCheck();
      },
    });
  }
}
