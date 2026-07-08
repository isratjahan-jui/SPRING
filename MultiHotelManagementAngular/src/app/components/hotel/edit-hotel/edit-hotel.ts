import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HotelService } from '../../../services/hotel.service';
import { Hotel, HotelRequest } from '../../../models/hotel.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-edit-hotel',
  imports: [CommonModule, FormsModule],
  templateUrl: './edit-hotel.html',
  styleUrl: './edit-hotel.css',
})
export class EditHotel implements OnInit {
  id!: number;
  hotel: HotelRequest = {
    hotelName: '',
    address: '',
    description: '',
    pricePerNight: 0,
    rating: '',
    status: '',
    foodAvailable: false,
    foodServiceHours: '',
    locationId: 0,
    ownerId: 0,
  };
  selectedImage?: File;
  preview: string | ArrayBuffer | null = null;
  currentImage: string = '';

  constructor(
    private route: ActivatedRoute,
    private hotelService: HotelService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.hotelService.getById(this.id).subscribe((data: Hotel) => {
      this.currentImage = data.image || '';
      this.hotel = {
        hotelName: data.hotelName,
        address: data.address,
        description: data.description,
        pricePerNight: data.pricePerNight,
        rating: data.rating,
        status: data.status,
        foodAvailable: data.foodAvailable,
        foodServiceHours: data.foodServiceHours,
        locationId: data.locationId,
        ownerId: data.ownerId,
      };
    });
  }

  updateHotel() {
    this.hotelService.update(this.id, this.hotel, this.selectedImage).subscribe(() => {
      this.router.navigate(['/hotels']);
    });
  }

  onFileSelected(event: any): void {
    if (event.target.files && event.target.files.length > 0) {
      this.selectedImage = event.target.files[0];
      const reader = new FileReader();
      reader.onload = () => {
        this.preview = reader.result;
      };
      reader.readAsDataURL(this.selectedImage!);
    }
  }
}
