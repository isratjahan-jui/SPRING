import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Hotel } from '../../../models/hotel.model';
import { HotelDetails as HotelDetailsModel } from '../../../models/hotel-details.model';
import { Facility } from '../../../models/facility.model';
import { FoodItem } from '../../../models/food-item.model';
import { Room } from '../../../models/room.model';
import { HotelService } from '../../../services/hotel.service';
import { HotelDetailsService } from '../../../services/hotel-details.service';
import { FacilityService } from '../../../services/facility.service';
import { FoodItemService } from '../../../services/food-item.service';
import { RoomService } from '../../../services/room.service';

@Component({
  selector: 'app-hotel-details',
  imports: [CommonModule, RouterLink],
  templateUrl: './hotel-details.html',
  styleUrl: './hotel-details.css',
})
export class HotelDetails implements OnInit {
  hotel?: Hotel;
  details?: HotelDetailsModel;
  facilities: Facility[] = [];
  foodItems: FoodItem[] = [];
  rooms: Room[] = [];
  detailsError = false;
  loading = true;
  errorMsg = '';

  constructor(
    private route: ActivatedRoute,
    private hotelService: HotelService,
    private hotelDetailsService: HotelDetailsService,
    private facilityService: FacilityService,
    private foodItemService: FoodItemService,
    private roomService: RoomService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.hotelService.getById(id).subscribe({
      next: (data) => {
        this.hotel = data;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Hotel load error:', err);
        this.loading = false;
        this.errorMsg = 'Failed to load hotel details.';
        this.cdr.markForCheck();
      },
    });

    this.hotelDetailsService.getByHotelId(id).subscribe({
      next: (data) => {
        this.details = data;
        this.cdr.markForCheck();
      },
      error: () => {
        this.detailsError = true;
        this.cdr.markForCheck();
      },
    });

    this.facilityService.getByHotel(id).subscribe({
      next: (data) => {
        this.facilities = data;
        this.cdr.markForCheck();
      },
      error: () => {},
    });

    this.foodItemService.getByHotel(id).subscribe({
      next: (data) => {
        this.foodItems = data;
        this.cdr.markForCheck();
      },
      error: () => {},
    });

    this.roomService.getByHotel(id).subscribe({
      next: (data) => {
        this.rooms = data;
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  getImageUrl(image: string): string {
    return image ? `http://localhost:8085/hotel/${image}` : '';
  }

  getRoomImageUrl(image: string): string {
    return image ? `http://localhost:8085/room/${image}` : '';
  }
}
