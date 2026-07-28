import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { HotelService } from '../../../services/hotel.service';
import { Hotel } from '../../../models/hotel.model';
import { environment } from '../../../../environments/environments';

@Component({
  selector: 'app-delete-hotel',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './delete-hotel.html',
  styleUrl: './delete-hotel.css',
})
export class DeleteHotel implements OnInit {
  pendingHotels: Hotel[] = [];
  approvedHotels: Hotel[] = [];
  rejectedHotels: Hotel[] = [];
  imageBaseUrl = environment.imageBaseUrl;

  showRejectModal = false;
  rejectHotelId: number | null = null;
  rejectReason = '';

  constructor(
    private hotelService: HotelService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.loadAllHotels();
  }

  loadAllHotels() {
    this.hotelService.getAll().subscribe({
      next: (data) => {
        this.pendingHotels = data.filter((h) => h.status === 'PENDING_APPROVAL');
        this.approvedHotels = data.filter((h) => h.status === 'APPROVED');
        this.rejectedHotels = data.filter((h) => h.status === 'REJECTED');
        this.cdr.markForCheck();
      },
      error: (err) => console.error('Hotels error:', err.error),
    });
  }

  approveHotel(id: number) {
    this.hotelService.approveHotel(id).subscribe(() => {
      this.loadAllHotels();
    });
  }

  openRejectModal(id: number) {
    console.log('openRejectModal called for hotel:', id);
    this.rejectHotelId = id;
    this.rejectReason = '';
    this.showRejectModal = true;
    this.cdr.markForCheck();
  }

  closeRejectModal() {
    this.showRejectModal = false;
    this.rejectHotelId = null;
    this.rejectReason = '';
    this.cdr.markForCheck();
  }

  confirmReject() {
    if (this.rejectHotelId == null) return;
    this.hotelService.rejectHotel(this.rejectHotelId, this.rejectReason).subscribe(() => {
      this.closeRejectModal();
      this.loadAllHotels();
    });
  }
}
