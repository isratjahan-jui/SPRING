import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { HotelService } from '../../../services/hotel.service';
import { Hotel } from '../../../models/hotel.model';

@Component({
  selector: 'app-hotel-list',
  imports: [CommonModule, RouterLink],
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
    this.hotelService.getAllApproved().subscribe((data) => {
      this.hotels = data;
    });
  }
}
