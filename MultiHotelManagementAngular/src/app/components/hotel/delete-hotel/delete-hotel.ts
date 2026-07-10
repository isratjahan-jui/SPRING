import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
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
      this.hotelService.delete(id).subscribe(() => {
        // delete successful → reload list
        this.loadHotels();
      });
    }
  }


  
}
