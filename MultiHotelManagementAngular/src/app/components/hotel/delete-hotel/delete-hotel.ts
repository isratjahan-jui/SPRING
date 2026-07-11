import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HotelService } from '../../../services/hotel.service';
import { Hotel } from '../../../models/hotel.model';

@Component({
  selector: 'app-delete-hotel',
  imports: [CommonModule, FormsModule],
  templateUrl: './delete-hotel.html',
  styleUrl: './delete-hotel.css',
})
export class DeleteHotel implements OnInit {

  hotels: Hotel[] = [];
  pendingHotels: Hotel[] = [];


  constructor(private hotelService: HotelService, private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.loadHotels();
    this.loadPending();
  }

  loadHotels() {
    this.hotelService.getAllApproved().subscribe(data => {
      this.hotels = data;
      this.cdr.markForCheck();
    });
  }
  loadPending() {
    this.hotelService.getPending().subscribe(data => {
      this.pendingHotels = data;
      this.cdr.markForCheck();
    });
  }

  approveHotel(id: number) {
    this.hotelService.approveHotel(id).subscribe(() => {
      this.loadPending();
      this.loadHotels();
    });
  }
  rejectHotel(id: number) {
    this.hotelService.rejectHotel(id).subscribe(() => {
      this.loadPending();
      this.loadHotels();
    });
  }





  deleteHotel(id: number) {
    if (confirm('Are you sure you want to delete this hotel?')) {
      this.hotelService.delete(id).subscribe(() => {
        // delete successful → reload list
        this.loadHotels();
      });
    }
  }



}
