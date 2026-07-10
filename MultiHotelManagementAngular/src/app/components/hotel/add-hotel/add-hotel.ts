import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { HotelRequest } from '../../../models/hotel.model';
import { HotelService } from '../../../services/hotel.service';
import { LocationService } from '../../../services/location.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Location } from '../../../models/location.model';

@Component({
  selector: 'app-add-hotel',
  imports: [CommonModule, FormsModule],
  templateUrl: './add-hotel.html',
  styleUrl: './add-hotel.css',
})
export class AddHotel implements OnInit {


  hotel: HotelRequest = {
    hotelName: '',
    address: '',
    description: '',
    rating: '',
    status: 'PENDING_APPROVAL',
    locationId: 0,
    ownerId: 0,
  };

  locations: Location[] = [];
  selectedImage?: File;
  preview: string | ArrayBuffer | null = null;

  constructor(
    private hotelService: HotelService,
    private locationService: LocationService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    const ownerId = localStorage.getItem('ownerId');
    if (ownerId) {
      this.hotel.ownerId = Number(ownerId);
    }

    this.locationService.getAll().subscribe({
      next: (data) => {
        this.locations = data;
        
        if (data.length > 0 && !this.hotel.locationId) {
          this.hotel.locationId = data[0].id;
        }
        console.log(this.locations)

        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to load locations', err);
       
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

  saveHotel() {
    this.hotelService.create(this.hotel, this.selectedImage).subscribe({
      next: () => {
        const ownerId = localStorage.getItem('ownerId');
        this.router.navigate([ownerId ? '/owner/my-hotels' : '/hotels']);
      },
      error: (err: any) => {
        console.error(err);
        alert(err.error?.message || 'Failed to add hotel');
      },
    });
  }
}
