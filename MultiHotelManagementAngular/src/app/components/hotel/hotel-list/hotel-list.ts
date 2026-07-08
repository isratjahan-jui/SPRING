import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HotelService } from '../../../services/hotel.service';
import { Hotel } from '../../../models/hotel.model';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-hotel-list',
  imports: [CommonModule, FormsModule,RouterLink],
  templateUrl: './hotel-list.html',
  styleUrl: './hotel-list.css',
})
export class HotelList implements OnInit {
  
 hotels: Hotel[] = [];

  constructor(private hotelService: HotelService) {}

  ngOnInit(): void {
    this.loadHotels();
  }

  loadHotels() {
    this.hotelService.getAllApproved().subscribe(data => {
      this.hotels = data;
    });
  }

  deleteHotel(id: number) {
    if (confirm('Are you sure you want to delete this hotel?')) {
      this.hotelService.delete(id).subscribe(() => this.loadHotels());
    }
  }

  
}
