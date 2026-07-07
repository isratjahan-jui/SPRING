import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { HotelService } from '../../../services/hotel.service';
import { Hotel } from '../../../models/hotel.model';

@Component({
  selector: 'app-owner-dashboard',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class OwnerDashboardComponent implements OnInit {
  auth = inject(AuthService);
  private hotelService = inject(HotelService);
  hotels: Hotel[] = [];
  activeCount = 0;

  ngOnInit() {
    const ownerId = this.auth.user()?.ownerId;
    if (ownerId) {
      this.hotelService.getByOwner(ownerId).subscribe((data) => {
        this.hotels = data;
        this.activeCount = data.filter(h => h.status === 'APPROVED').length;
      });
    }
  }
}
