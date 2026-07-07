import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HotelService } from '../../../services/hotel.service';
import { AuthService } from '../../../services/auth.service';
import { Hotel, HotelRequest } from '../../../models/hotel.model';

@Component({
  selector: 'app-my-hotels',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './my-hotels.component.html',
  styleUrls: ['./my-hotels.component.css'],
})
export class MyHotelsComponent implements OnInit {
  private hotelService = inject(HotelService);
  private auth = inject(AuthService);

  hotels: Hotel[] = [];

  newHotel: HotelRequest = {
    hotelName: '', address: '', description: '', pricePerNight: 0,
    rating: '', foodAvailable: false, foodServiceHours: '', locationId: 1,
  };

  ngOnInit() {
    this.loadHotels();
  }

  private loadHotels() {
    const ownerId = this.auth.user()?.ownerId;
    if (ownerId) this.hotelService.getByOwner(ownerId).subscribe((data) => (this.hotels = data));
  }

  createHotel() {
    this.hotelService.create(this.newHotel).subscribe(() => {
      this.loadHotels();
      this.newHotel = { hotelName: '', address: '', description: '', pricePerNight: 0, rating: '', foodAvailable: false, foodServiceHours: '', locationId: 1 };
    });
  }

  deleteHotel(id: number) {
    if (confirm('Delete this hotel?')) this.hotelService.delete(id).subscribe(() => this.loadHotels());
  }
}
