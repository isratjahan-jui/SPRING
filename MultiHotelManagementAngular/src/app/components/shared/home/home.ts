import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { Hotel } from '../../../models/hotel.model';
import { HotelService } from '../../../services/hotel.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink,CommonModule, FormsModule],
  templateUrl: './home.html',
  styleUrls: ['./home.css'],
})
export class HomeComponent implements OnInit {
  private hotelService = inject(HotelService);
  hotels: Hotel[] = [];
  searchCity = '';

  ngOnInit() {
    this.hotelService.getAllApproved().subscribe((data) => (this.hotels = data));
  }

  search() {
    if (this.searchCity) {
      this.hotelService.getByCity(this.searchCity).subscribe((data) => (this.hotels = data));
    }
  }
}
