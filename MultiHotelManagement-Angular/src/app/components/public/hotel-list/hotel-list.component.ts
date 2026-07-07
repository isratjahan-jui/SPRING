import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { HotelService } from '../../../services/hotel.service';
import { Hotel } from '../../../models/hotel.model';

@Component({
  selector: 'app-hotel-list',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './hotel-list.component.html',
  styleUrls: ['./hotel-list.component.css'],
})
export class HotelListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private hotelService = inject(HotelService);
  hotels: Hotel[] = [];
  city = '';

  ngOnInit() {
    this.city = this.route.snapshot.paramMap.get('city') || '';
    if (this.city) {
      this.hotelService.getByCity(this.city).subscribe((data) => (this.hotels = data));
    }
  }
}
