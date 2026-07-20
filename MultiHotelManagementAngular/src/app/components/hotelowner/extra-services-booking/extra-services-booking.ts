import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Hotel } from '../../../models/hotel.model';
import { Booking } from '../../../models/booking.model';
import {
  ExtraServiceService,
  ExtraServiceResponse,
  ExtraServiceRequest,
} from '../../../services/extra-service.service';
import { HotelService } from '../../../services/hotel.service';
import { BookingService } from '../../../services/booking.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-extra-services-booking',
  imports: [CommonModule, FormsModule],
  templateUrl: './extra-services-booking.html',
  styleUrl: './extra-services-booking.css',
})
export class ExtraServicesBookingComponent implements OnInit {
  hotels: Hotel[] = [];
  bookings: Booking[] = [];
  selectedHotelId = 0;
  showModal = false;
  selectedBooking: Booking | null = null;
  extraServices: ExtraServiceResponse[] = [];
  loadingServices = false;

  serviceTypes = [
    'Laundry',
    'Transport',
    'Spa',
    'Airport Transfer',
    'Room Service',
    'Minibar',
    'Tour Guide',
    'Gym',
    'Parking',
    'Other',
  ];

  formData: ExtraServiceRequest = {
    serviceType: '',
    price: 0,
    serviceStatus: 'PENDING',
    bookingId: 0,
  };
  editingId: number | null = null;
  showForm = false;

  private authService = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);

  constructor(
    private extraServiceService: ExtraServiceService,
    private hotelService: HotelService,
    private bookingService: BookingService,
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

  onHotelChange() {
    this.bookings = [];
    this.selectedBooking = null;
    this.showModal = false;
    this.showForm = false;
    if (this.selectedHotelId) {
      this.bookingService.getByHotel(this.selectedHotelId).subscribe({
        next: (data) => {
          this.bookings = data;
          this.cdr.markForCheck();
        },
        error: () => alert('Failed to load bookings'),
      });
    }
  }

  viewExtraServices(booking: Booking) {
    this.selectedBooking = booking;
    this.showModal = true;
    this.showForm = false;
    this.editingId = null;
    this.resetForm();
    this.loadExtraServices(booking.id);
  }

  loadExtraServices(bookingId: number) {
    this.loadingServices = true;
    this.extraServiceService.getByBooking(bookingId).subscribe({
      next: (data) => {
        this.extraServices = data;
        this.loadingServices = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loadingServices = false;
        this.cdr.markForCheck();
      },
    });
  }

  openAddForm() {
    if (!this.selectedBooking) return;
    this.editingId = null;
    this.formData = {
      serviceType: '',
      price: 0,
      serviceStatus: 'PENDING',
      bookingId: this.selectedBooking.id,
    };
    this.showForm = true;
    this.cdr.markForCheck();
  }

  editService(service: ExtraServiceResponse) {
    this.editingId = service.id;
    this.formData = {
      serviceType: service.serviceType,
      price: service.price,
      serviceStatus: service.serviceStatus,
      bookingId: service.bookingId,
    };
    this.showForm = true;
    this.cdr.markForCheck();
  }

  cancelForm() {
    this.showForm = false;
    this.editingId = null;
    this.resetForm();
    this.cdr.markForCheck();
  }

  submitForm() {
    if (!this.formData.serviceType || !this.formData.price) {
      alert('Please fill in all required fields');
      return;
    }
    if (this.editingId) {
      this.extraServiceService.update(this.editingId, this.formData).subscribe({
        next: () => {
          this.cancelForm();
          if (this.selectedBooking) this.loadExtraServices(this.selectedBooking.id);
        },
        error: () => alert('Failed to update extra service'),
      });
    } else {
      this.extraServiceService.create(this.formData).subscribe({
        next: () => {
          this.cancelForm();
          if (this.selectedBooking) this.loadExtraServices(this.selectedBooking.id);
        },
        error: () => alert('Failed to create extra service'),
      });
    }
  }

  deleteService(id: number) {
    if (!confirm('Delete this extra service?')) return;
    this.extraServiceService.delete(id).subscribe({
      next: () => {
        if (this.selectedBooking) this.loadExtraServices(this.selectedBooking.id);
      },
      error: () => alert('Failed to delete extra service'),
    });
  }

  closeModal() {
    this.showModal = false;
    this.selectedBooking = null;
    this.extraServices = [];
    this.showForm = false;
    this.editingId = null;
    this.cdr.markForCheck();
  }

  private resetForm() {
    this.formData = { serviceType: '', price: 0, serviceStatus: 'PENDING', bookingId: 0 };
  }
}
