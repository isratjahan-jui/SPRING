import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HotelService } from '../../../services/hotel.service';
import { Hotel } from '../../../models/hotel.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
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
