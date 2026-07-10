import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HotelService } from '../../../services/hotel.service';
import { LocationService } from '../../../services/location.service';
import { Hotel, HotelRequest } from '../../../models/hotel.model';
import { Location } from '../../../models/location.model';
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
    rating: '',
    status: '',
    locationId: 0,
    ownerId: 0,
  };
  locations: Location[] = [];
  selectedImage?: File;
  preview: string | ArrayBuffer | null = null;
  currentImage: string = '';
  loading = false;

  constructor(
    private route: ActivatedRoute,
    private hotelService: HotelService,
    private locationService: LocationService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));

    this.locationService.getAll().subscribe({
      next: (data) => (this.locations = data),
      error: () => console.error('Failed to load locations'),
    });

    this.hotelService.getById(this.id).subscribe({
      next: (data: Hotel) => {
        this.currentImage = data.image || '';
        this.hotel = {
          hotelName: data.hotelName,
          address: data.address,
          description: data.description,
          rating: data.rating,
          status: data.status,
          locationId: data.locationId,
          ownerId: data.ownerId,
        };
      },
      error: () => alert('Failed to load hotel details'),
    });
  }

  updateHotel() {
    this.loading = true;
    this.hotelService.update(this.id, this.hotel, this.selectedImage).subscribe({
      next: () => {
        this.loading = false;
        const ownerId = localStorage.getItem('ownerId');
        this.router.navigate([ownerId ? '/owner/my-hotels' : '/hotels']);
      },
      error: (err) => {
        this.loading = false;
        alert(err.error?.message || 'Failed to update hotel');
      },
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
