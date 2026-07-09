import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Hotel } from '../../../models/hotel.model';
import { HotelDetails as HotelDetailsModel } from '../../../models/hotel-details.model';
import { HotelService } from '../../../services/hotel.service';
import { HotelDetailsService } from '../../../services/hotel-details.service';

@Component({
  selector: 'app-hotel-details',
  imports: [CommonModule],
  templateUrl: './hotel-details.html',
  styleUrl: './hotel-details.css',
})
export class HotelDetails implements OnInit {
  hotel?: Hotel;
  details?: HotelDetailsModel;
  detailsError = false;
  loading = true;

  constructor(
    private route: ActivatedRoute,
    private hotelService: HotelService,
    private hotelDetailsService: HotelDetailsService,
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.hotelService.getById(id).subscribe({
      next: (data) => {
        this.hotel = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        alert('Failed to load hotel details');
      },
    });

    this.hotelDetailsService.getByHotelId(id).subscribe({
      next: (data) => (this.details = data),
      error: () => {
        this.detailsError = true;
      },
    });
  }

  getImageUrl(image: string): string {
    return image ? `http://localhost:8085/hotel/${image}` : '';
  }
}
