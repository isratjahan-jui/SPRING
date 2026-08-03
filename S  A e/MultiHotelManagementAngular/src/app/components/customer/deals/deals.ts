import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { DealService } from '../../../services/deal.service';
import { HotelService } from '../../../services/hotel.service';
import { DealResponse } from '../../../models/deal.model';
import { Hotel } from '../../../models/hotel.model';

@Component({
  selector: 'app-customer-deals',
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './deals.html',
  styleUrl: './deals.css',
})
export class CustomerDeals implements OnInit {
  private dealService = inject(DealService);
  private hotelService = inject(HotelService);
  private cdr = inject(ChangeDetectorRef);

  deals: DealResponse[] = [];
  filteredDeals: DealResponse[] = [];
  hotels: Hotel[] = [];
  loading = true;

  selectedHotelId: number | null = null;
  searchText: string = '';

  ngOnInit() {
    this.hotelService.getAllApproved().subscribe({
      next: (hotels) => {
        this.hotels = hotels;
        this.cdr.markForCheck();
      },
      error: () => {},
    });

    this.dealService.getAll().subscribe({
      next: (data) => {
        this.deals = data;
        this.filteredDeals = data;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  applyFilters() {
    let result = [...this.deals];

    if (this.selectedHotelId !== null) {
      result = result.filter((d) => d.hotelId === this.selectedHotelId);
    }

    if (this.searchText.trim()) {
      const s = this.searchText.toLowerCase();
      result = result.filter(
        (d) =>
          (d.dealTitle && d.dealTitle.toLowerCase().includes(s)) ||
          (d.hotelName && d.hotelName.toLowerCase().includes(s)) ||
          (d.description && d.description.toLowerCase().includes(s)) ||
          (d.roomType && d.roomType.toLowerCase().includes(s))
      );
    }

    this.filteredDeals = result;
  }

  clearFilters() {
    this.selectedHotelId = null;
    this.searchText = '';
    this.filteredDeals = [...this.deals];
  }
}
