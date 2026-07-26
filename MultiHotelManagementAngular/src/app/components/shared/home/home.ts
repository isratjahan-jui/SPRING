import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { Hotel } from '../../../models/hotel.model';
import { DealResponse } from '../../../models/deal.model';
import { HotelService } from '../../../services/hotel.service';
import { DealService } from '../../../services/deal.service';
import { CommonModule } from '@angular/common';
import { environment } from '../../../../environments/environments';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, CommonModule, FormsModule],
  templateUrl: './home.html',
  styleUrls: ['./home.css'],
})
export class HomeComponent implements OnInit {
  private hotelService = inject(HotelService);
  private dealService = inject(DealService);
  hotels: Hotel[] = [];
  deals: DealResponse[] = [];
  searchKeyword = '';
  searchActive = false;
  imageBaseUrl = environment.imageBaseUrl;

  activeDivision = '';
  loadingHotels = false;

  divisions = [
    { name: 'Dhaka', icon: '🏙️' },
    { name: 'Chattogram', icon: '⚓' },
    { name: 'Rajshahi', icon: '🥭' },
    { name: 'Khulna', icon: '🌿' },
    { name: 'Barishal', icon: '🚢' },
    { name: 'Sylhet', icon: '🍵' },
    { name: 'Rangpur', icon: '🌾' },
    { name: 'Mymensingh', icon: '🎓' },
  ];

  ngOnInit() {
    this.loadHotels();
    this.dealService.getAll().subscribe((data) => (this.deals = data));
  }

  loadHotels() {
    this.hotelService.getAllApproved().subscribe((data) => {
      this.hotels = data;
      this.searchActive = false;
    });
  }

  search() {
    const q = this.searchKeyword.trim();
    if (!q) {
      this.loadHotels();
      return;
    }
    this.activeDivision = '';
    this.hotelService.search(q).subscribe((data) => {
      this.hotels = data;
      this.searchActive = true;
    });
  }

  clearSearch() {
    this.searchKeyword = '';
    this.loadHotels();
  }

  filterByDivision(division: string) {
    if (this.activeDivision === division) {
      this.clearDivisionFilter();
      return;
    }
    this.searchKeyword = '';
    this.searchActive = false;
    this.activeDivision = division;
    this.loadingHotels = true;
    this.hotelService.getByCity(division).subscribe({
      next: (data) => {
        this.hotels = data;
        this.loadingHotels = false;
      },
      error: () => {
        this.hotels = [];
        this.loadingHotels = false;
      },
    });
  }

  clearDivisionFilter() {
    this.activeDivision = '';
    this.loadHotels();
  }
}
